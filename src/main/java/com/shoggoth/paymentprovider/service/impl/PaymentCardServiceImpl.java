package com.shoggoth.paymentprovider.service.impl;

import com.shoggoth.paymentprovider.entity.PaymentCard;
import com.shoggoth.paymentprovider.exception.NotEnoughFoundsException;
import com.shoggoth.paymentprovider.repository.BankAccountRepository;
import com.shoggoth.paymentprovider.repository.CustomerRepository;
import com.shoggoth.paymentprovider.repository.PaymentCardRepository;
import com.shoggoth.paymentprovider.service.PaymentCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentCardServiceImpl implements PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CustomerRepository customerRepository;


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
    public Mono<PaymentCard> reserveFounds(PaymentCard paymentCard, BigDecimal amount) {
        return bankAccountRepository.findById(paymentCard.getBankAccountId())
                .flatMap(bankAccount -> {
                            var currentBalance = bankAccount.getBalance();
                            if (currentBalance.compareTo(amount) < 0) {
                                return Mono.error(new NotEnoughFoundsException("Not enough founds for top up."));
                            } else {
                                bankAccount.setBalance(currentBalance.subtract(amount));
                                return bankAccountRepository.save(bankAccount);
                            }
                        }
                )
                .map(bankAccount -> paymentCard);
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
                });
    }
}