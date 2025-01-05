package com.shoggoth.paymentprovider.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shoggoth.paymentprovider.entity.PaymentMethod;
import com.shoggoth.paymentprovider.entity.TransactionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.format.annotation.DateTimeFormat;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GetTransactionResponse(
        PaymentMethod paymentMethod,
        String amount,
        String currency,
        UUID transactionId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime createdAt,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime updatedAt,
        PaymentCardResponse cardData,
        String language,
        String notificationUrl,
        CustomerRequestResponse customer,
        TransactionStatus status,
        String message
) {
}
