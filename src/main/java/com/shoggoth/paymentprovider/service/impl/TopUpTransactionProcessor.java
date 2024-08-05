package com.shoggoth.paymentprovider.service.impl;

import com.shoggoth.paymentprovider.service.TransactionProcessor;
import com.shoggoth.paymentprovider.entity.Transaction;
import com.shoggoth.paymentprovider.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class TopUpTransactionProcessor implements TransactionProcessor {
    private final BankAccountService bankAccountService;

    @Override
    public Mono<Transaction> process(Transaction transaction) {
        return switch (transaction.getStatus()) {
            case IN_PROGRESS -> bankAccountService.withholdFunds(
                            transaction.getCard().getBankAccountId(),
                            transaction.getAmount())
                    .map(bankAccount -> transaction);

            case APPROVED -> bankAccountService.creditFunds(
                            transaction.getMerchant().getBankAccountId(),
                            transaction.getAmount()
                    )
                    .map(bankAccount -> transaction);

            case FAILED -> bankAccountService.creditFunds(
                            transaction.getCard().getBankAccountId(),
                            transaction.getAmount()
                    )
                    .map(bankAccount -> transaction);
        };
    }
}
