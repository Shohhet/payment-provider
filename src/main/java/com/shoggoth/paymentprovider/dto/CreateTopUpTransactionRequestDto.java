package com.shoggoth.paymentprovider.dto;

import com.shoggoth.paymentprovider.entity.PaymentMethod;
import com.shoggoth.paymentprovider.entity.TransactionStatus;

import java.util.Date;
import java.util.UUID;

public record CreateTopUpTransactionRequestDto(

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
