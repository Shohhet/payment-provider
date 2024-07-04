package com.shoggoth.paymentprovider.service;

import com.shoggoth.paymentprovider.entity.BankAccount;
import reactor.core.publisher.Mono;

public interface BankAccountService {

    Mono<BankAccount> createDefaultBankAccount(String currency);
}
