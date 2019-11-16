package com.github.vitalibo.alarm.processor.infrastructure.aws.dms;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class DMSEvent implements Serializable {

    @JsonProperty(value = "data")
    private Map<String, ?> data;

    @JsonProperty(value = "metadata")
    private Metadata metadata;

    @Data
    public static class Metadata implements Serializable {

        @JsonProperty(value = "timestamp")
        private String timestamp;

        @JsonProperty(value = "record-type")
        private String recordType;

        @JsonProperty(value = "operation")
        private String operation;

        @JsonProperty(value = "partition-key-type")
        private String partitionKeyType;

        @JsonProperty(value = "schema-name")
        private String schemaName;

        @JsonProperty(value = "table-name")
        private String tableName;

    }

}