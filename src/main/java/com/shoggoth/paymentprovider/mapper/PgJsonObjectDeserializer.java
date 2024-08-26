package com.shoggoth.paymentprovider.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import io.r2dbc.postgresql.codec.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jackson.JsonObjectDeserializer;

import java.io.IOException;

@Slf4j
public class PgJsonObjectDeserializer extends JsonObjectDeserializer<Json> {

    @Override
    protected Json deserializeObject(JsonParser jsonParser, DeserializationContext context, ObjectCodec codec, JsonNode tree) throws IOException {
        var value = context.readTree(jsonParser);
        log.info("read json value: {}", value);
        return Json.of(value.toString());
    }
}
