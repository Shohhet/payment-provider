package com.shoggoth.paymentprovider.service;

import com.shoggoth.paymentprovider.entity.Transaction;
import reactor.core.publisher.Mono;

public interface ProcessingService {
    Mono<Transaction> process(Transaction transaction);
}
