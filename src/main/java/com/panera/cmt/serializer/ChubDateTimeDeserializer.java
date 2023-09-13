package com.panera.cmt.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.panera.cmt.config.Constants;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

@Component
public class ChubDateTimeDeserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Date retVal;
        try {
            retVal = Constants.getChubDateTimeFormat().parse(p.getValueAsString());
        } catch (ParseException pe) {
            throw new HttpMessageNotReadableException("Invalid date format. Expected: " + Constants.DATE_TIME_FORMAT);
        }
        return retVal;
    }
}
