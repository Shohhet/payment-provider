package com.shoggoth.paymentprovider.repository;

import com.shoggoth.paymentprovider.entity.Transaction;
import com.shoggoth.paymentprovider.entity.TransactionStatus;
import com.shoggoth.paymentprovider.entity.TransactionType;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TransactionRepository extends R2dbcRepository<Transaction, UUID> {
    Mono<Transaction> findByIdAndMerchantIdAndType(UUID id, UUID merchantId, TransactionType type);
    Flux<Transaction> findByCreatedAtBetweenAndMerchantIdAndType(LocalDateTime begin, LocalDateTime end, UUID merchantId, TransactionType type);
    Flux<Transaction> findByStatus(TransactionStatus transactionStatus);
}
