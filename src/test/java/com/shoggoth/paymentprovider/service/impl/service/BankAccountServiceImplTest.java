package com.shoggoth.paymentprovider.service.impl.service;

import com.shoggoth.paymentprovider.entity.BankAccount;
import com.shoggoth.paymentprovider.exception.NotEnoughFundsException;
import com.shoggoth.paymentprovider.repository.BankAccountRepository;
import com.shoggoth.paymentprovider.service.impl.BankAccountServiceImpl;
import com.shoggoth.paymentprovider.service.impl.util.TestDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.shoggoth.paymentprovider.service.impl.util.TestDataUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceImplTest {
    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Test
    @DisplayName("Test withold when account have enough funds.")
    void givenAccountIdAndAmount_WhenHaveEnoughFundsOnAccount_ThenReturnWitholdBankAccount() {
        //given
        when(bankAccountRepository.findByIdForUpdate(ACCOUNT_ID)).thenReturn(Mono.just(TestDataUtils.getPersistedBankAccount()));
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        //when
        StepVerifier.create(bankAccountService.withholdFunds(ACCOUNT_ID, TRANSACTION_AMOUNT))
                //then
                .consumeNextWith(result -> assertEquals(result.getBalance(), INITIAL_ACCOUNT_AMOUNT.subtract(TRANSACTION_AMOUNT)))
                .verifyComplete();
    }

    @Test
    @DisplayName("Test withold when account have not enough funds.")
    void givenAccountIdAndAmount_WhenHaveNotEnoughFundsOnAccount_ThenThrowException() {
        //given
        when(bankAccountRepository.findByIdForUpdate(ACCOUNT_ID)).thenReturn(Mono.just(TestDataUtils.getPersistedBankAccount()));

        //when
        StepVerifier.create(bankAccountService.withholdFunds(ACCOUNT_ID, TOO_BIG_TRANSACTION_AMOUNT))
                //then
                .expectErrorMatches(throwable -> throwable instanceof NotEnoughFundsException
                        && throwable.getMessage().equals("Not enough founds")
                        && ((NotEnoughFundsException) throwable).getErrorCode().equals("BALANCE_AMOUNT_ERROR")
                )
                .verify();
    }

    @Test
    void givenAccountIdAndAmount_WhenCreditFunds_ThenReturnCreditedBankAccount() {
        //given
        when(bankAccountRepository.findByIdForUpdate(ACCOUNT_ID)).thenReturn(Mono.just(TestDataUtils.getPersistedBankAccount()));
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        //when
        StepVerifier.create(bankAccountService.creditFunds(ACCOUNT_ID, TOO_BIG_TRANSACTION_AMOUNT))
                //then
                .consumeNextWith(result -> assertEquals(result.getBalance(), INITIAL_ACCOUNT_AMOUNT.add(TOO_BIG_TRANSACTION_AMOUNT)))
                .verifyComplete();
    }
}