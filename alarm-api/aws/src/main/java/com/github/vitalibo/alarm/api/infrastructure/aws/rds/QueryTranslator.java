package com.github.vitalibo.alarm.api.infrastructure.aws.rds;

import com.github.vitalibo.alarm.api.core.util.Jackson;
import freemarker.template.Template;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.io.CharArrayWriter;
import java.io.Writer;
import java.util.Map;

@AllArgsConstructor
public class QueryTranslator {

    private final Template template;

    @SneakyThrows
    public <T> String from(T o) {
        Writer out = new CharArrayWriter();
        template.process(
            Jackson.transfrom(o, Map.class), out);
        return out.toString();
    }

}