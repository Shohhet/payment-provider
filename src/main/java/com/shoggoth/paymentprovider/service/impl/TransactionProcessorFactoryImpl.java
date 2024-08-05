package com.shoggoth.paymentprovider.service.impl;

import com.shoggoth.paymentprovider.service.TransactionProcessor;
import com.shoggoth.paymentprovider.service.TransactionProcessorFactory;
import com.shoggoth.paymentprovider.entity.Transaction;
import com.shoggoth.paymentprovider.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TransactionProcessorFactoryImpl implements TransactionProcessorFactory {
    private final List<TransactionProcessor> transactionProcessors;

    @Override
    public Mono<TransactionProcessor> getTransactionProcessor(Transaction transaction) {
        return switch (transaction.getType()) {
            case TOP_UP -> getConcreteProcessor(TopUpTransactionProcessor.class);
            case PAY_OUT -> getConcreteProcessor(PayOutTransactionProcessor.class);
        };
    }

    private Mono<TransactionProcessor> getConcreteProcessor(Class<? extends TransactionProcessor> processorClass) {
        return Mono.just(
                transactionProcessors.stream()
                        .filter(processorClass::isInstance)
                        .map(processorClass::cast)
                        .findFirst()
                        .orElseThrow()
        );
    }
}
