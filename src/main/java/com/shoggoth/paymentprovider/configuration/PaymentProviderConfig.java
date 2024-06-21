package com.shoggoth.paymentprovider.configuration;

import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

@Configuration
public class PaymentProviderConfig {
    private static final String yearMonthFormat = "MM/yy";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.deserializers(new YearMonthDeserializer(DateTimeFormatter.ofPattern(yearMonthFormat)));
            builder.serializers(new YearMonthSerializer(DateTimeFormatter.ofPattern(yearMonthFormat)));
        };
    }
}
