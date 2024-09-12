package com.shoggoth.paymentprovider.service.impl.service;

import com.shoggoth.paymentprovider.dto.CustomerRequestResponse;
import com.shoggoth.paymentprovider.entity.PaymentCard;
import com.shoggoth.paymentprovider.entity.TransactionType;
import com.shoggoth.paymentprovider.mapper.PaymentCardMapper;
import com.shoggoth.paymentprovider.mapper.PaymentCardMapperImpl;
import com.shoggoth.paymentprovider.repository.BankAccountRepository;
import com.shoggoth.paymentprovider.repository.CustomerRepository;
import com.shoggoth.paymentprovider.repository.PaymentCardRepository;
import com.shoggoth.paymentprovider.service.BankAccountService;
import com.shoggoth.paymentprovider.service.CustomerService;
import com.shoggoth.paymentprovider.service.impl.PaymentCardServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static com.shoggoth.paymentprovider.service.impl.util.TestDataUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceImplTest {

    @InjectMocks
    private PaymentCardServiceImpl paymentCardService;

    @Spy
    private PaymentCardMapper paymentCardMapper = new PaymentCardMapperImpl();

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BankAccountService bankAccountService;

    @Mock
    private BankAccountRepository bankAccountRepository;


    @Test
    @DisplayName("Test get existing payment card functionality.")
    void givenTopUpTransaction_WhenPaymentCardExistAndCustomerValid_ThenReturnExistingPaymentCard() {
        //given
        when(customerRepository.findById(any(UUID.class))).thenReturn(Mono.just(getPersistedCustomer()));
        when(bankAccountRepository.findById(any(UUID.class))).thenReturn(Mono.just(getPersistedBankAccount()));
        when(paymentCardRepository.findPaymentCardByNumber(any(String.class))).thenReturn(Mono.just(getPersistedPaymentCard()));

        //when
        StepVerifier.create(paymentCardService.getOrCreatePaymentCard(getCreateTransactionRequest(), TransactionType.TOP_UP))
                //then
                .consumeNextWith(result -> assertEquals(result, getPersistedPaymentCard()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Test create new payment card functionality.")
    void givenTopUpTransaction_WhenPaymentCardDoesNotExist_ThenReturnNewPaymentCard() {
        //given
        when(paymentCardRepository.findPaymentCardByNumber(any(String.class))).thenReturn(Mono.empty());
        when(paymentCardRepository.save(any(PaymentCard.class))).thenReturn(Mono.just(getPersistedPaymentCard()));
        when(customerService.getCustomer(any(CustomerRequestResponse.class))).thenReturn(Mono.just(getPersistedCustomer()));
        when(customerRepository.findById(any(UUID.class))).thenReturn(Mono.just(getPersistedCustomer()));
        when(bankAccountService.createDefaultBankAccount(any(String.class))).thenReturn(Mono.just(getPersistedBankAccount()));
        when(bankAccountRepository.findById(any(UUID.class))).thenReturn(Mono.just(getPersistedBankAccount()));
        //when
        StepVerifier.create(paymentCardService.getOrCreatePaymentCard(getCreateTransactionRequest(), TransactionType.TOP_UP))
                //then
                .consumeNextWith(result -> assertEquals(result, getPersistedPaymentCard()))
                .verifyComplete();
    }



}