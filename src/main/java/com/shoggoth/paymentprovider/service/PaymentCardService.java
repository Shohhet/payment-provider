package com.shoggoth.paymentprovider.service;

import com.shoggoth.paymentprovider.dto.CreateTransactionRequest;
import com.shoggoth.paymentprovider.entity.PaymentCard;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PaymentCardService {
    Mono<PaymentCard> getPaymentCardByNumber(String cardNumber);

    Mono<PaymentCard> getPaymentCardById(UUID id);

    Mono<PaymentCard> getOrCreatePaymentCard(CreateTransactionRequest requestPayload);

}
