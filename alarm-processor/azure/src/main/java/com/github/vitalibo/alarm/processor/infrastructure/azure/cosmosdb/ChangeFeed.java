package com.github.vitalibo.alarm.processor.infrastructure.azure.cosmosdb;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class ChangeFeed implements Serializable {

    private List<Map<String, ?>> documents;
    private String functionName;
    private String invocationId;

    public ChangeFeed withDocuments(List<Map<String, ?>> documents) {
        this.documents = documents;
        return this;
    }

    public ChangeFeed withFunctionName(String functionName) {
        this.functionName = functionName;
        return this;
    }

    public ChangeFeed withInvocationId(String invocationId) {
        this.invocationId = invocationId;
        return this;
    }

}