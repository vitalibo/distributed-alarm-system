package com.github.vitalibo.alarm.processor.core.model.transform;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class OffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {

    @Override
    public OffsetDateTime deserialize(JsonParser jp, DeserializationContext cx) throws IOException {
        return OffsetDateTime.ofInstant(
            Instant.ofEpochSecond(
                ((IntNode) jp.getCodec().readTree(jp)).asLong()),
            ZoneId.of("UTC"));
    }

}