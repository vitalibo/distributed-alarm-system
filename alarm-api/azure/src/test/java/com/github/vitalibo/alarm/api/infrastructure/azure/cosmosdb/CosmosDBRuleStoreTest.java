package com.github.vitalibo.alarm.api.infrastructure.azure.cosmosdb;

import com.github.vitalibo.alarm.api.core.model.Rule;
import com.github.vitalibo.alarm.api.core.model.RuleCondition;
import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.ResourceResponse;
import com.microsoft.azure.documentdb.internal.DocumentServiceResponse;
import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.util.concurrent.Future;

public class CosmosDBRuleStoreTest {

    @Mock
    private DocumentClient mockDocumentClient;
    @Mock
    private DocumentServiceResponse mockDocumentServiceResponse;
    @Mock
    private Document mockDocument;
    @Mock
    private Future mockFuture;
    @Captor
    private ArgumentCaptor<Document> captorDocument;

    private ResourceResponse<Document> dammyResourceResponse;
    private CosmosDBRuleStore spyRuleStore;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        spyRuleStore = Mockito.spy(new CosmosDBRuleStore(mockDocumentClient, "foo", "bar"));
        Mockito.doAnswer(answer -> {
            CosmosDBRuleStore.Supplier<?> function = answer.getArgument(0);
            Object result = function.get();
            Mockito.when(mockFuture.get()).thenReturn(result);
            return mockFuture;
        }).when(spyRuleStore).supplyAsync(Mockito.any());
        Mockito.when(mockDocumentServiceResponse.getResource(Document.class)).thenReturn(mockDocument);
        Constructor<ResourceResponse> constructor = ResourceResponse.class.getDeclaredConstructor(DocumentServiceResponse.class, Class.class);
        constructor.setAccessible(true);
        dammyResourceResponse = constructor.newInstance(mockDocumentServiceResponse, Document.class);
    }

    @Test
    public void testCreateRule() throws Exception {
        Mockito.when(mockDocumentClient.createDocument(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
            .thenReturn(dammyResourceResponse);
        Mockito.when(mockDocument.getId()).thenReturn("12345");

        Rule rule = new Rule();
        rule.withMetricName("foo");

        Future<String> actual = spyRuleStore.createRule(rule);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.get(), "12345");
        Mockito.verify(mockDocumentClient).createDocument(Mockito.eq("/dbs/foo/colls/bar"), captorDocument.capture(), Mockito.any(), Mockito.eq(false));
        Document document = captorDocument.getValue();
        Assert.assertEquals(document.getString("metricName"), "foo");
        Mockito.verify(spyRuleStore).supplyAsync(Mockito.any());
    }

    @Test
    public void testUpdateRule() throws Exception {
        Mockito.when(mockDocumentClient.upsertDocument(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
            .thenReturn(dammyResourceResponse);
        Mockito.when(mockDocument.getId()).thenReturn("baz");
        Mockito.when(mockDocument.getString("metricName")).thenReturn("foo");
        Mockito.doReturn(mockDocument)
            .when(spyRuleStore).makeUpdatedDocument(Mockito.anyString(), Mockito.any(), Mockito.any());

        Rule rule = new Rule();
        rule.withMetricName("foo");

        Future<Rule> future = spyRuleStore.updateRuleById("baz", rule);

        Assert.assertNotNull(future);
        Rule actual = future.get();
        Assert.assertEquals(actual.getRuleId(), "baz");
        Assert.assertEquals(actual.getMetricName(), "foo");
        Mockito.verify(mockDocumentClient).upsertDocument(
            Mockito.eq("/dbs/foo/colls/bar"), Mockito.eq(mockDocument), Mockito.any(), Mockito.eq(true));
        Mockito.verify(spyRuleStore).makeUpdatedDocument("baz", rule, null);
        Mockito.verify(spyRuleStore).supplyAsync(Mockito.any());
    }

    @Test
    public void testDeleteRule() throws Exception {
        Mockito.when(mockDocumentClient.upsertDocument(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
            .thenReturn(dammyResourceResponse);
        Mockito.doReturn(mockDocument)
            .when(spyRuleStore).makeUpdatedDocument(Mockito.anyString(), Mockito.any(), Mockito.any());

        spyRuleStore.deleteRuleById("baz");

        Mockito.verify(mockDocumentClient).upsertDocument(
            Mockito.eq("/dbs/foo/colls/bar"), Mockito.eq(mockDocument), Mockito.any(), Mockito.eq(true));
        Mockito.verify(spyRuleStore).makeUpdatedDocument("baz", new Rule(), 10);
        Mockito.verify(spyRuleStore).supplyAsync(Mockito.any());
    }

    @Test
    public void testGetRuleById() throws Exception {
        Mockito.when(mockDocumentClient.readDocument(Mockito.anyString(), Mockito.any()))
            .thenReturn(dammyResourceResponse);
        Mockito.when(mockDocument.getId()).thenReturn("baz");
        Mockito.when(mockDocument.getString("metricName")).thenReturn("foo");

        Future<Rule> future = spyRuleStore.getRuleById("baz");

        Assert.assertNotNull(future);
        Rule actual = future.get();
        Assert.assertEquals(actual.getRuleId(), "baz");
        Assert.assertEquals(actual.getMetricName(), "foo");
        Mockito.verify(mockDocumentClient).readDocument(Mockito.eq("/dbs/foo/colls/bar/docs/baz"), Mockito.any());
        Mockito.verify(spyRuleStore).supplyAsync(Mockito.any());
    }

    @Test
    public void testMakeUpdatedDocumentNoUpdates() throws Exception {
        Mockito.when(mockDocumentClient.readDocument(Mockito.anyString(), Mockito.any()))
            .thenReturn(dammyResourceResponse);
        Mockito.when(mockDocument.getId()).thenReturn("baz");
        Mockito.when(mockDocument.getString("metricName")).thenReturn("foo");
        Mockito.when(mockDocument.getString("condition")).thenReturn("GreaterThanThreshold");
        Mockito.when(mockDocument.getDouble("threshold")).thenReturn(1.234);

        Document actual = spyRuleStore.makeUpdatedDocument("taz", new Rule(), 10);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getId(), "taz");
        Assert.assertEquals(actual.getString("metricName"), "foo");
        Assert.assertEquals(actual.getString("condition"), "GreaterThanThreshold");
        Assert.assertEquals(actual.getDouble("threshold"), 1.234);
        Assert.assertEquals(actual.getTimeToLive(), (Integer) 10);
        Mockito.verify(mockDocumentClient).readDocument(Mockito.eq("/dbs/foo/colls/bar/docs/taz"), Mockito.any());
    }

    @Test
    public void testMakeUpdatedDocument() throws Exception {
        Mockito.when(mockDocumentClient.readDocument(Mockito.anyString(), Mockito.any()))
            .thenReturn(dammyResourceResponse);
        Mockito.when(mockDocument.getId()).thenReturn("baz");
        Mockito.when(mockDocument.getString("metricName")).thenReturn("foo");
        Mockito.when(mockDocument.getString("condition")).thenReturn("GreaterThanThreshold");
        Mockito.when(mockDocument.getDouble("threshold")).thenReturn(1.234);

        Rule rule = new Rule();
        rule.setMetricName("NewFoo");
        rule.setCondition(RuleCondition.LessThanOrEqualToThreshold);
        rule.setThreshold(123.4);

        Document actual = spyRuleStore.makeUpdatedDocument("taz", rule, null);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getId(), "taz");
        Assert.assertEquals(actual.getString("metricName"), "NewFoo");
        Assert.assertEquals(actual.getString("condition"), "LessThanOrEqualToThreshold");
        Assert.assertEquals(actual.getDouble("threshold"), 123.4);
        Assert.assertNull(actual.getTimeToLive());
        Mockito.verify(mockDocumentClient).readDocument(Mockito.eq("/dbs/foo/colls/bar/docs/taz"), Mockito.any());
    }

}