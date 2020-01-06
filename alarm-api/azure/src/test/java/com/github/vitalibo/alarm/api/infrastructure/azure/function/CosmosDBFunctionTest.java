package com.github.vitalibo.alarm.api.infrastructure.azure.function;

import com.github.vitalibo.alarm.api.core.util.Resources;
import com.github.vitalibo.alarm.api.infrastructure.azure.eventhub.ChangeFeed;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.OutputBinding;
import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class CosmosDBFunctionTest {

    @Mock
    private OutputBinding<ChangeFeed> mockOutputBinding;
    @Mock
    private ExecutionContext mockExecutionContext;
    @Captor
    private ArgumentCaptor<ChangeFeed> captorChangeFeed;

    private CosmosDBFunction function;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        function = new CosmosDBFunction();
    }

    @Test
    public void testCosmosDBTrigger() {
        Mockito.when(mockExecutionContext.getFunctionName()).thenReturn("MyFunction");
        Mockito.when(mockExecutionContext.getInvocationId()).thenReturn("aa00cd3d-0000-0800-0000-5e13d9bb0000");

        function.transform(Resources.asString("/ChangeFeed.json"), mockOutputBinding, mockExecutionContext);

        Mockito.verify(mockOutputBinding).setValue(captorChangeFeed.capture());
        ChangeFeed actual = captorChangeFeed.getValue();
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getFunctionName(), "MyFunction");
        Assert.assertEquals(actual.getInvocationId(), "aa00cd3d-0000-0800-0000-5e13d9bb0000");
        List<Object> documents = actual.getDocuments();
        Assert.assertEquals(documents.size(), 1);
        Assert.assertEquals(((Map<String, Object>) documents.get(0)).get("id"), "1S3F");
    }

}