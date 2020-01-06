package com.github.vitalibo.alarm.api.infrastructure.azure.function;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.vitalibo.alarm.api.core.util.Jackson;
import com.github.vitalibo.alarm.api.infrastructure.azure.eventhub.ChangeFeed;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.CosmosDBTrigger;
import com.microsoft.azure.functions.annotation.EventHubOutput;
import com.microsoft.azure.functions.annotation.FunctionName;

import java.util.List;

public class CosmosDBFunction {

    @FunctionName("CosmosDBTrigger")
    public void transform(
        @CosmosDBTrigger(name = "documents", databaseName = "alarmsys", collectionName = "rule", leaseCollectionName = "leases",
                         createLeaseCollectionIfNotExists = true, connectionStringSetting = "RULE_COSMOS_DB_CONNECTION_STRING") String documents,
        @EventHubOutput(name = "data", eventHubName = "rule-eventhub", connection = "RULE_EVENT_HUB_CONNECTION_STRING") OutputBinding<ChangeFeed> data,
        final ExecutionContext context) {

        data.setValue(new ChangeFeed()
            .withFunctionName(context.getFunctionName())
            .withInvocationId(context.getInvocationId())
            .withDocuments(Jackson.fromJsonString(
                documents, new TypeReference<List<Object>>() {})));
    }

}