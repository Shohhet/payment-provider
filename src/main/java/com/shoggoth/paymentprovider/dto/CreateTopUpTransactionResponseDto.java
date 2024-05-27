package com.shoggoth.paymentprovider.dto;

import com.shoggoth.paymentprovider.entity.TransactionStatus;

import java.util.UUID;

public record CreateTopUpTransactionResponseDto(
        UUID topUpId,
        TransactionStatus status,
        String message
) {
}
