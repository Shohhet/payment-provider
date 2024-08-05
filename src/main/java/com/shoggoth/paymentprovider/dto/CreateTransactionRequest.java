package com.shoggoth.paymentprovider.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.shoggoth.paymentprovider.entity.PaymentMethod;
import com.shoggoth.paymentprovider.validation.Alfa2LanguageCode;
import com.shoggoth.paymentprovider.validation.Alfa3CurrencyCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CreateTransactionRequest(

        @NotNull
        PaymentMethod paymentMethod,

        @NotNull
        @Digits(integer = 100, fraction = 2)
        @DecimalMin(value = "0.00", inclusive = false)
        BigDecimal amount,

        @NotNull
        @NotBlank
        @Size(min = 3, max = 3)
        @Alfa3CurrencyCode
        String currency,

        @Valid
        PaymentCardRequest cardData,

        @NotNull
        @NotBlank
        @Size(min = 2, max = 2)
        @Alfa2LanguageCode
        String language,

        @NotNull
        @URL
        String notificationUrl,

        @Valid
        CustomerRequestResponse customer
) {
}
