package com.shoggoth.paymentprovider.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.shoggoth.paymentprovider.entity.PaymentMethod;
import com.shoggoth.paymentprovider.entity.TransactionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GetPayOutTransactionDto(
        PaymentMethod paymentMethod,
        String amount,
        String currency,
        UUID payOutId,

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
