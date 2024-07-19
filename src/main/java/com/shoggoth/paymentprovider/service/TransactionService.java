package com.shoggoth.paymentprovider.service;

import reactor.core.publisher.Flux;

public interface TransactionService {
    void handleTransactionsStatuses();
}
