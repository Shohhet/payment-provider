package com.shoggoth.paymentprovider.dto;

import com.shoggoth.paymentprovider.entity.PaymentMethod;
import com.shoggoth.paymentprovider.entity.TransactionStatus;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GetTopUpTransactionDto(
        PaymentMethod paymentMethod,
        String amount,
        String currency,
        UUID topUpId,
        Date createdAt,
        Date updatedAt,
        GetPaymentCardDto cardData,
        String language,
        String notificationUrl,
        CustomerDto customer,
        TransactionStatus status,
        String message
) {
}
