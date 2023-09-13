package com.panera.cmt.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.panera.cmt.config.Constants;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
public class DateTimeSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date date, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(Constants.getDateTimeFormat().format(date));
    }
}
