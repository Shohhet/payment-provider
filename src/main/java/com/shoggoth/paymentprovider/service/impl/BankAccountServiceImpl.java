package com.shoggoth.paymentprovider.service.impl;

import com.shoggoth.paymentprovider.entity.BankAccount;
import com.shoggoth.paymentprovider.repository.BankAccountRepository;
import com.shoggoth.paymentprovider.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {
    @Value("${customer-balance.default-amount}")
    private BigDecimal defaultAmount;
    private final BankAccountRepository bankAccountRepository;

    @Override
    public Mono<BankAccount> createDefaultBankAccount(String currency) {
        var newBankAccount = new BankAccount();
        newBankAccount.setBalance(defaultAmount);
        newBankAccount.setCurrency(currency);
        return bankAccountRepository.save(newBankAccount);
    }
}
