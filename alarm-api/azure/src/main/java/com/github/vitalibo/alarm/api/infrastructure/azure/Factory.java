package com.github.vitalibo.alarm.api.infrastructure.azure;

import com.github.vitalibo.alarm.api.core.Facade;
import com.github.vitalibo.alarm.api.core.facade.CreateRuleFacade;
import com.github.vitalibo.alarm.api.core.facade.DeleteRuleFacade;
import com.github.vitalibo.alarm.api.core.facade.GetRuleFacade;
import com.github.vitalibo.alarm.api.core.facade.UpdateRuleFacade;
import com.github.vitalibo.alarm.api.core.model.*;
import com.github.vitalibo.alarm.api.core.store.AlarmStore;
import com.github.vitalibo.alarm.api.core.store.RuleStore;
import com.github.vitalibo.alarm.api.infrastructure.azure.cosmosdb.CosmosDBRuleStore;
import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.DocumentClient;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Getter;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class Factory {

    private static final String COSMOSDB_ENDPOINT = "azure.cosmosdb.endpoint";
    private static final String COSMOSDB_MASTER_KET = "azure.cosmosdb.masterKey";
    private static final String COSMOSDB_DATABASE_ID = "azure.cosmosdb.databaseId";
    private static final String COSMOSDB_COLLECTION_ID = "azure.cosmosdb.collectionId";

    @Getter(lazy = true)
    private static final Factory instance = new Factory(ConfigFactory.load(), ConfigFactory.parseResources("default-application.conf"));

    private final Config config;
    private final RuleStore ruleStore;
    private final AlarmStore alarmStore;

    Factory(Config... configs) {
        this.config = Arrays.stream(configs)
            .reduce(Config::withFallback)
            .orElseThrow(IllegalStateException::new)
            .resolve();
        this.ruleStore = createCosmosDBRuleStore();
        this.alarmStore = createMockAlarmStore();
    }

    public Facade<CreateRuleRequest, CreateRuleResponse> createCreateRuleFacade() {
        return new CreateRuleFacade(
            ruleStore);
    }

    public Facade<DeleteRuleRequest, DeleteRuleResponse> createDeleteRuleFacade() {
        return new DeleteRuleFacade(
            ruleStore);
    }

    public Facade<GetRuleRequest, GetRuleResponse> createGetRuleFacade() {
        return new GetRuleFacade(
            alarmStore,
            ruleStore);
    }

    public Facade<UpdateRuleRequest, UpdateRuleResponse> createUpdateRuleFacade() {
        return new UpdateRuleFacade(
            ruleStore);
    }

    private RuleStore createCosmosDBRuleStore() {
        return new CosmosDBRuleStore(
            new DocumentClient(
                config.getString(COSMOSDB_ENDPOINT),
                config.getString(COSMOSDB_MASTER_KET),
                ConnectionPolicy.GetDefault(),
                ConsistencyLevel.Session),
            config.getString(COSMOSDB_DATABASE_ID),
            config.getString(COSMOSDB_COLLECTION_ID));
    }

    private AlarmStore createMockAlarmStore() {
        return (o) -> CompletableFuture.supplyAsync(() -> null);
    }

}