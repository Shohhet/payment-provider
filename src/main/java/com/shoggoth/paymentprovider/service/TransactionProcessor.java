package com.shoggoth.paymentprovider.service;

import com.shoggoth.paymentprovider.entity.Transaction;
import reactor.core.publisher.Mono;

public interface TransactionProcessor {
    Mono<Transaction> process(Transaction transaction);
}
