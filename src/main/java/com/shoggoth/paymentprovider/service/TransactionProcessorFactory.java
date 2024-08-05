package com.shoggoth.paymentprovider.service;

import com.shoggoth.paymentprovider.entity.Transaction;
import reactor.core.publisher.Mono;

public interface TransactionProcessorFactory {
    Mono<TransactionProcessor> getTransactionProcessor(Transaction transaction);
}
