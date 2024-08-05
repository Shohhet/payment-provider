package com.shoggoth.paymentprovider.service;

import com.shoggoth.paymentprovider.entity.BankAccount;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

public interface BankAccountService {

    Mono<BankAccount> createDefaultBankAccount(String currency);
    Mono<BankAccount> withholdFunds(UUID bankAccountId, BigDecimal amount);
    Mono<BankAccount> creditFunds(UUID bankAccountId, BigDecimal amount);
}
