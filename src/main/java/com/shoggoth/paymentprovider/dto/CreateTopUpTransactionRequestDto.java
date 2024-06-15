package com.shoggoth.paymentprovider.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.shoggoth.paymentprovider.entity.PaymentMethod;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CreateTopUpTransactionRequestDto(

        PaymentMethod paymentMethod,
        String amount,
        String currency,
        CreatePaymentCardDto cardData,
        String language,
        String notificationUrl,
        CustomerDto customer
) {
}
