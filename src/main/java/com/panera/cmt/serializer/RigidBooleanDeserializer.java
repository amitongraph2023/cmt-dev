package com.panera.cmt.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

public class RigidBooleanDeserializer extends JsonDeserializer<Boolean> {

    @Override
    public Boolean deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        JsonToken currentToken = parser.getCurrentToken();
        if (JsonToken.VALUE_TRUE.equals(currentToken)) {
            return Boolean.TRUE;
        } else if (JsonToken.VALUE_STRING.equals(currentToken)) {
            String text = parser.getText().trim();
            return ("true".equalsIgnoreCase(text));
        } else {
            return Boolean.FALSE;
        }
    }

    @Override
    public Boolean getNullValue(DeserializationContext context) throws JsonMappingException {
        return Boolean.FALSE;
    }
}
