package com.shoggoth.paymentprovider.service.impl;

import com.shoggoth.paymentprovider.entity.BankAccount;
import com.shoggoth.paymentprovider.exception.NotEnoughFundsException;
import com.shoggoth.paymentprovider.exception.NotFoundException;
import com.shoggoth.paymentprovider.repository.BankAccountRepository;
import com.shoggoth.paymentprovider.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {
    @Value("${customer-balance.default-amount}")
    private BigDecimal defaultAmount;
    private final BankAccountRepository bankAccountRepository;

    @Override
    public Mono<BankAccount> createDefaultBankAccount(String currency) {
        var newBankAccount = new BankAccount();
        newBankAccount.setBalance(defaultAmount);
        newBankAccount.setCurrency(currency);
        return bankAccountRepository.save(newBankAccount)
                .doOnSuccess(bankAccount -> log.debug("Default bank account created: {}", bankAccount))
                .doOnError(throwable -> log.error("Error creating new bank account: {}", throwable.getMessage()));
    }

    @Override
    public Mono<BankAccount> withholdFunds(UUID bankAccountId, BigDecimal amount) {
        return bankAccountRepository.findByIdForUpdate(bankAccountId)
                .switchIfEmpty(Mono.error(new NotFoundException("Bank account not found", "NOT_FOUND")))
                .flatMap(bankAccount -> {
                            var currentBalance = bankAccount.getBalance();
                            if (currentBalance.compareTo(amount) < 0) {
                                return Mono.error(new NotEnoughFundsException("Not enough founds", "BALANCE_AMOUNT_ERROR") );
                            } else {
                                bankAccount.setBalance(currentBalance.subtract(amount));
                                return bankAccountRepository.save(bankAccount);
                            }
                        }
                )
                .doOnSuccess(bankAccount -> log.debug("Bank account withhold: {}", bankAccount))
                .doOnError(throwable -> log.error("Error withold from bank account: {}", throwable.getMessage()));
    }

    @Override
    public Mono<BankAccount> creditFunds(UUID bankAccountId, BigDecimal amount) {
        return bankAccountRepository.findByIdForUpdate(bankAccountId)
                .switchIfEmpty(Mono.error(new NotFoundException("Bank account not found", "NOT_FOUND")))
                .flatMap(bankAccount -> {
                            var currentBalance = bankAccount.getBalance();
                            bankAccount.setBalance(currentBalance.add(amount));
                            return bankAccountRepository.save(bankAccount);
                        }
                )
                .doOnSuccess(bankAccount -> log.debug("Bank account credit: {}", bankAccount))
                .doOnError(throwable -> log.error("Error credit to bank account: {}", throwable.getMessage()));
    }

}
