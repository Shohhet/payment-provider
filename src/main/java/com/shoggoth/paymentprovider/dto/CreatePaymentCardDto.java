package com.shoggoth.paymentprovider.dto;

import java.util.Date;

public record CreatePaymentCardDto(
        String cardNumber,
        Date expirationDate,
        String cvv
) {
}
