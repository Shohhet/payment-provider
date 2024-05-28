package com.shoggoth.paymentprovider.dto;

import com.shoggoth.paymentprovider.entity.PaymentMethod;


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
