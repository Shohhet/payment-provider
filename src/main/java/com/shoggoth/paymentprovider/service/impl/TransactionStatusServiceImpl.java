package com.shoggoth.paymentprovider.service.impl;

import com.shoggoth.paymentprovider.entity.Transaction;
import com.shoggoth.paymentprovider.repository.TransactionRepository;
import com.shoggoth.paymentprovider.service.TransactionService;
import com.shoggoth.paymentprovider.service.TransactionStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Random;

import static com.shoggoth.paymentprovider.entity.TransactionStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionStatusServiceImpl implements TransactionStatusService {
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;

    @Override
    @Scheduled(initialDelay = 5000, fixedDelay = 5000)
    public void handleTransactionsStatuses() {
        transactionRepository.findByStatus(IN_PROGRESS)
                .flatMap(this::processRandomTransactionStatus)
                .subscribe(
                        transaction -> log.debug("Transaction status updated: {}", transaction),
                        throwable -> log.error("Transaction status update failed: ", throwable)
                );
    }

    private Mono<Transaction> processRandomTransactionStatus(Transaction transaction) {
        Random random = new Random();
        return random.nextInt(10) > 3 ? transactionService.acceptTransaction(transaction) : transactionService.rejectTransaction(transaction);
    }
}
