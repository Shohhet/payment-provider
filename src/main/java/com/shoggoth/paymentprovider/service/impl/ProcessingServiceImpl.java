package com.shoggoth.paymentprovider.service.impl;

import com.shoggoth.paymentprovider.entity.Transaction;
import com.shoggoth.paymentprovider.service.ProcessingService;
import com.shoggoth.paymentprovider.service.TransactionProcessorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProcessingServiceImpl implements ProcessingService {
    private final TransactionProcessorFactory transactionProcessorFactory;

    @Override
    public Mono<Transaction> process(Transaction transaction) {
        return transactionProcessorFactory.getTransactionProcessor(transaction)
                .flatMap(processor -> processor.process(transaction));
    }
}
