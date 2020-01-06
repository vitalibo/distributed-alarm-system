package com.github.vitalibo.alarm.api.infrastructure.azure.eventhub;

import lombok.Data;

import java.util.List;

@Data
public class ChangeFeed {

    private List<Object> documents;
    private String functionName;
    private String invocationId;

    public ChangeFeed withDocuments(List<Object> documents) {
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