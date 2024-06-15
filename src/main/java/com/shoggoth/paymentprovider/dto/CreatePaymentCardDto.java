package com.shoggoth.paymentprovider.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;
import java.util.Date;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CreatePaymentCardDto(
        String cardNumber,
        LocalDate expirationDate,
        String cvv
) {
}
