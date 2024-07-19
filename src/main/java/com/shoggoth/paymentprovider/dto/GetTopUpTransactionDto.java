package com.shoggoth.paymentprovider.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shoggoth.paymentprovider.entity.PaymentMethod;
import com.shoggoth.paymentprovider.entity.TransactionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)

public record GetTopUpTransactionDto(
        PaymentMethod paymentMethod,
        String amount,
        String currency,
        UUID topUpId,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.mss")
        LocalDateTime createdAt,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.mss")
        LocalDateTime updatedAt,

        PaymentCardNumberDto cardData,
        String language,
        String notificationUrl,
        CustomerDto customer,
        TransactionStatus status,
        String message
) {
}
