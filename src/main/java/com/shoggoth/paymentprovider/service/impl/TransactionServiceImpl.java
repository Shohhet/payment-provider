package com.shoggoth.paymentprovider.service.impl;

import com.shoggoth.paymentprovider.entity.TransactionStatus;
import com.shoggoth.paymentprovider.entity.TransactionType;
import com.shoggoth.paymentprovider.repository.TransactionRepository;
import com.shoggoth.paymentprovider.service.TopUpTransactionService;
import com.shoggoth.paymentprovider.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TopUpTransactionService topUpTransactionService;

    @Override
    @Scheduled(cron = "*/10 * * ? * *")
    public void handleTransactionsStatuses() {
        transactionRepository.findByStatus(TransactionStatus.IN_PROGRESS)
                .flatMap(transaction -> {
                            switch (transaction.getType()) {
                                case TOP_UP -> {
                                    if(this.getRandomStatus().equals(TransactionStatus.APPROVED)) {

                                    }
                                }
                            }

                            transaction.setStatus(TransactionStatus.APPROVED);
                            transaction.setMessage("Transaction approved");
                            return transactionRepository.save(transaction);
                        }
                )
                .subscribe(transaction -> log.info(transaction.toString()));
    }

    private TransactionStatus getRandomStatus() {
        var random = new Random();
        int val = random.nextInt(10);
        if (val < 2) {
            return TransactionStatus.FAILED;
        } else {
            return TransactionStatus.APPROVED;
        }
    }
}
