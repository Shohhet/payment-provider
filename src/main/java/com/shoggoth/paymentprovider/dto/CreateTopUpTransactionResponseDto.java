package com.shoggoth.paymentprovider.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.shoggoth.paymentprovider.entity.TransactionStatus;

import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CreateTopUpTransactionResponseDto(
        UUID topUpId,
        TransactionStatus status,
        String message
) {
}
