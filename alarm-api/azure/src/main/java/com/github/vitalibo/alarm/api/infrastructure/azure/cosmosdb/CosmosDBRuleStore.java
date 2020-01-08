package com.github.vitalibo.alarm.api.infrastructure.azure.cosmosdb;

import com.github.vitalibo.alarm.api.core.model.Rule;
import com.github.vitalibo.alarm.api.core.store.RuleStore;
import com.github.vitalibo.alarm.api.core.util.Jackson;
import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.DocumentClientException;
import com.microsoft.azure.documentdb.RequestOptions;
import lombok.RequiredArgsConstructor;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class CosmosDBRuleStore implements RuleStore {

    private final DocumentClient client;
    private final String databaseId;
    private final String collectionId;

    @Override
    public Future<String> createRule(Rule rule) {
        return supplyAsync(() -> {
            Document resource = client
                .createDocument(
                    String.format("/dbs/%s/colls/%s", databaseId, collectionId),
                    new Document(Jackson.toJsonString(rule)),
                    new RequestOptions(),
                    false)
                .getResource();

            return resource.getId();
        });
    }

    @Override
    public Future<Rule> updateRuleById(String ruleId, Rule rule) {
        return supplyAsync(() -> {
            Document resource = client
                .upsertDocument(
                    String.format("/dbs/%s/colls/%s", databaseId, collectionId),
                    makeUpdatedDocument(ruleId, rule, null),
                    new RequestOptions(),
                    true)
                .getResource();

            return RuleTranslator.from(resource);
        });
    }

    @Override
    public Future<Void> deleteRuleById(String ruleId) {
        return supplyAsync(() -> {
            Document resource = client
                .upsertDocument(
                    String.format("/dbs/%s/colls/%s", databaseId, collectionId),
                    makeUpdatedDocument(ruleId, new Rule(), 10),
                    new RequestOptions(),
                    true)
                .getResource();

            return null;
        });
    }

    @Override
    public Future<Rule> getRuleById(String ruleId) {
        return supplyAsync(() -> {
            Document resource = client
                .readDocument(
                    String.format("/dbs/%s/colls/%s/docs/%s", databaseId, collectionId, ruleId),
                    new RequestOptions())
                .getResource();

            return RuleTranslator.from(resource);
        });
    }

    Document makeUpdatedDocument(String ruleId, Rule rule, Integer ttl) throws DocumentClientException {
        Document document = client
            .readDocument(
                String.format("/dbs/%s/colls/%s/docs/%s", databaseId, collectionId, ruleId),
                new RequestOptions())
            .getResource();

        Map<String, ?> updated = Stream
            .of(tuple("id", ruleId),
                tuple("metricName", Optional.ofNullable(rule.getMetricName())
                    .orElse(document.getString("metricName"))),
                tuple("condition", Optional.ofNullable(rule.getCondition())
                    .map(String::valueOf)
                    .orElse(document.getString("condition"))),
                tuple("threshold", Optional.ofNullable(rule.getThreshold())
                    .orElse(document.getDouble("threshold"))),
                tuple("ttl", ttl))
            .filter(o -> Objects.nonNull(o.getValue()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new Document(Jackson.toJsonString(updated));
    }

    <T> Future<T> supplyAsync(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.get();
            } catch (DocumentClientException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static <K, V> Map.Entry<K, V> tuple(K key, V value) {
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

    interface Supplier<T> {
        T get() throws DocumentClientException;
    }

}