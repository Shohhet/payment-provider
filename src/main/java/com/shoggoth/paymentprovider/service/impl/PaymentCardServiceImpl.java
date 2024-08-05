package com.shoggoth.paymentprovider.service.impl;

import com.shoggoth.paymentprovider.dto.CreateTransactionRequest;
import com.shoggoth.paymentprovider.entity.PaymentCard;
import com.shoggoth.paymentprovider.mapper.PaymentCardMapper;
import com.shoggoth.paymentprovider.repository.BankAccountRepository;
import com.shoggoth.paymentprovider.repository.CustomerRepository;
import com.shoggoth.paymentprovider.repository.PaymentCardRepository;
import com.shoggoth.paymentprovider.service.BankAccountService;
import com.shoggoth.paymentprovider.service.CustomerService;
import com.shoggoth.paymentprovider.service.PaymentCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentCardServiceImpl implements PaymentCardService {

    private final CustomerService customerService;
    private final PaymentCardMapper paymentCardMapper;
    private final PaymentCardRepository paymentCardRepository;
    private final BankAccountService bankAccountService;
    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;


    @Override
    public Mono<PaymentCard> getPaymentCardByNumber(String cardNumber) {
        return paymentCardRepository.findPaymentCardByNumber(cardNumber)
                .flatMap(this::setRelatedEntities);
    }

    @Override
    public Mono<PaymentCard> getPaymentCardById(UUID id) {
        return paymentCardRepository.findById(id)
                .flatMap(this::setRelatedEntities);
    }

    @Override
    public Mono<PaymentCard> getOrCreatePaymentCard(CreateTransactionRequest requestPayload) {

        return paymentCardRepository.findPaymentCardByNumber(requestPayload.cardData().cardNumber())
                .doOnSuccess(pc -> log.debug("Get existing payment card: {}", pc))
                .switchIfEmpty(createNewPaymentCard(requestPayload))
                .doOnSuccess(pc -> log.debug("Create new payment card: {}", pc))
                .doOnError(throwable -> log.error("Error creating new payment card: {}", throwable.getMessage()));


    }

    private Mono<PaymentCard> createNewPaymentCard(CreateTransactionRequest requestPayload) {
        var paymentCard = paymentCardMapper.requestToPaymentCard(requestPayload.cardData());
        return Mono.zip(Mono.just(paymentCard),
                        customerService.getCustomer(requestPayload.customer()),
                        bankAccountService.createDefaultBankAccount(requestPayload.currency()))
                .map(t -> {
                            var card = t.getT1();
                            var customer = t.getT2();
                            var bankAccount = t.getT3();
                            card.setOwnerId(customer.getId());
                            card.setBankAccountId(bankAccount.getId());
                            return card;
                        }
                )
                .flatMap(paymentCardRepository::save)
                .flatMap(this::setRelatedEntities);


    }

    private Mono<PaymentCard> setRelatedEntities(PaymentCard paymentCard) {
        return Mono.zip(
                        customerRepository.findById(paymentCard.getOwnerId()),
                        bankAccountRepository.findById(paymentCard.getBankAccountId())
                )
                .map(tuple -> {
                            paymentCard.setOwner(tuple.getT1());
                            paymentCard.setBankAccount(tuple.getT2());
                            return paymentCard;
                        }
                );
    }
}