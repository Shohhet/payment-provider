package com.shoggoth.paymentprovider.service.impl;

import com.shoggoth.paymentprovider.dto.CreateTransactionRequest;
import com.shoggoth.paymentprovider.dto.CustomerRequestResponse;
import com.shoggoth.paymentprovider.entity.Customer;
import com.shoggoth.paymentprovider.entity.PaymentCard;
import com.shoggoth.paymentprovider.entity.TransactionType;
import com.shoggoth.paymentprovider.exception.TransactionDataException;
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

import static com.shoggoth.paymentprovider.entity.TransactionType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentCardServiceImpl implements PaymentCardService {

    private final PaymentCardMapper paymentCardMapper;
    private final PaymentCardRepository paymentCardRepository;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final BankAccountService bankAccountService;
    private final BankAccountRepository bankAccountRepository;

    @Override
    public Mono<PaymentCard> getPaymentCardById(UUID id) {
        return paymentCardRepository.findById(id)
                .flatMap(this::setRelatedEntities);
    }

    @Override
    public Mono<PaymentCard> getOrCreatePaymentCard(CreateTransactionRequest requestPayload, TransactionType transactionType) {

        return paymentCardRepository.findPaymentCardByNumber(requestPayload.cardData().cardNumber())
                .doOnNext(pc -> log.debug("Get existing payment card: {}", pc))
                .flatMap(this::setRelatedEntities)
                .flatMap(paymentCard -> isOwnerDataValid(paymentCard, requestPayload.customer())
                        ? Mono.just(paymentCard)
                        : Mono.error(new TransactionDataException("Wrong customer data for existing card.", "TRANSACTION_DATA_ERROR")))
                .switchIfEmpty(createNewPaymentCard(requestPayload, transactionType))
                .doOnNext(pc -> log.debug("Create new payment card: {}", pc))
                .doOnError(throwable -> log.error("Error creating new payment card: {}", throwable.getMessage()));
    }

    private Mono<PaymentCard> createNewPaymentCard(CreateTransactionRequest requestPayload, TransactionType transactionType) {
        var paymentCard = paymentCardMapper.requestToPaymentCard(requestPayload.cardData());
        if (transactionType.equals(PAY_OUT)) {
            return Mono.error(new TransactionDataException("Payment card does not exist.", "TRANSACTION_DATA_ERROR"));
        }
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

    private boolean isOwnerDataValid(PaymentCard paymentCard, CustomerRequestResponse customerData) {
        Customer persistCardOwner = paymentCard.getOwner();
        return persistCardOwner.getFirstName().equals(customerData.firstName())
                && persistCardOwner.getLastName().equals(customerData.lastName())
                && persistCardOwner.getCountryCode().equals(customerData.country());
    }
}