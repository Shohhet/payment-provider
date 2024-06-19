package com.shoggoth.paymentprovider.service;

import com.shoggoth.paymentprovider.dto.CreatePaymentCardDto;
import com.shoggoth.paymentprovider.dto.CreateTopUpTransactionRequestDto;
import com.shoggoth.paymentprovider.dto.CustomerDto;
import com.shoggoth.paymentprovider.entity.PaymentCard;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentCardService {
    Mono<PaymentCard> getPaymentCardByNumber(String cardNumber);

    Mono<PaymentCard> getPaymentCardById(UUID id);

    Mono<PaymentCard> createPaymentCard(CreateTopUpTransactionRequestDto transactionDto);

    Mono<PaymentCard> reserveFounds(PaymentCard paymentCard, BigDecimal amount);
}
