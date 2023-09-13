package com.panera.cmt.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StringTrimModule extends SimpleModule {

    /**
     * Custom deserializer for all string values received through our rest services.
     * If the value is blank (ie: "  " or "" ect) the value is changed to null
     * If the value is not blank, then all leading and trailing spaces will be trimmed
     */
    public StringTrimModule() {
        addDeserializer(String.class, new StdScalarDeserializer<String>(String.class) {
            @Override
            public String deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
                return StringUtils.isNotBlank(jsonParser.getValueAsString()) ? jsonParser.getValueAsString().trim() : null;
            }
        });
    }
}
