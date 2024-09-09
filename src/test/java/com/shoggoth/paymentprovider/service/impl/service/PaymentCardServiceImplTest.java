package com.shoggoth.paymentprovider.service.impl.service;

import com.shoggoth.paymentprovider.mapper.PaymentCardMapper;
import com.shoggoth.paymentprovider.repository.BankAccountRepository;
import com.shoggoth.paymentprovider.repository.CustomerRepository;
import com.shoggoth.paymentprovider.repository.PaymentCardRepository;
import com.shoggoth.paymentprovider.service.BankAccountService;
import com.shoggoth.paymentprovider.service.CustomerService;
import com.shoggoth.paymentprovider.service.PaymentCardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceImplTest {

    @InjectMocks
    private PaymentCardService paymentCardService;

    @Spy
    private PaymentCardMapper paymentCardMapper;

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
    void givenTopUpTransaction_WhenPaymentCardExistAndCustomerValid_ThenReturnExistingPaymentCard() {
        //given

        //when
        //then


    }

}