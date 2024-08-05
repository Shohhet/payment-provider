package com.shoggoth.paymentprovider.service;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;

public interface TransactionStatusService {
    void handleTransactionsStatuses();
}
