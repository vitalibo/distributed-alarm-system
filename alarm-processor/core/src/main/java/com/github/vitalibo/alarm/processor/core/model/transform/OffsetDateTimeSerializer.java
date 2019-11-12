package com.github.vitalibo.alarm.processor.core.model.transform;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.OffsetDateTime;

public class OffsetDateTimeSerializer extends StdSerializer<OffsetDateTime> {

    public OffsetDateTimeSerializer() {
        this(null);
    }

    private OffsetDateTimeSerializer(Class<OffsetDateTime> t) {
        super(t);
    }

    @Override
    public void serialize(OffsetDateTime value, JsonGenerator gen,
                          SerializerProvider provider) throws IOException {
        gen.writeNumber(value.toEpochSecond());
    }

}