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

import java.math.BigDecimal;
import java.util.UUID;

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
        UUID accountId = TestDataUtils.ACCOUNT_ID;
        BigDecimal transactionAmount = TestDataUtils.TRANSACTION_AMOUNT;
        when(bankAccountRepository.findByIdForUpdate(accountId)).thenReturn(Mono.just(TestDataUtils.getPersistedBankAccount()));
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        //when
        StepVerifier.create(bankAccountService.withholdFunds(accountId, transactionAmount))
                //then
                .consumeNextWith(result -> assertEquals(result.getBalance(), TestDataUtils.INITIAL_ACCOUNT_AMOUNT.subtract(transactionAmount)))
                .verifyComplete();
    }

    @Test
    @DisplayName("Test withold when account have not enough funds.")
    void givenAccountIdAndAmount_WhenHaveNotEnoughFundsOnAccount_ThenThrowException() {
        //given
        UUID accountId = TestDataUtils.ACCOUNT_ID;
        BigDecimal transactionAmount = TestDataUtils.TOO_BIG_TRANSACTION_AMOUNT;
        when(bankAccountRepository.findByIdForUpdate(accountId)).thenReturn(Mono.just(TestDataUtils.getPersistedBankAccount()));

        //when
        StepVerifier.create(bankAccountService.withholdFunds(accountId, transactionAmount))
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
        UUID accountId = TestDataUtils.ACCOUNT_ID;
        BigDecimal transactionAmount = TestDataUtils.TRANSACTION_AMOUNT;
        when(bankAccountRepository.findByIdForUpdate(accountId)).thenReturn(Mono.just(TestDataUtils.getPersistedBankAccount()));
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        //when
        StepVerifier.create(bankAccountService.creditFunds(accountId, transactionAmount))
                //then
                .consumeNextWith(result -> assertEquals(result.getBalance(), TestDataUtils.INITIAL_ACCOUNT_AMOUNT.add(transactionAmount)))
                .verifyComplete();
    }
}