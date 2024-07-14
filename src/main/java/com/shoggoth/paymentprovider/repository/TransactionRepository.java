package com.shoggoth.paymentprovider.repository;

import com.shoggoth.paymentprovider.entity.Transaction;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface TransactionRepository extends R2dbcRepository<Transaction, UUID> {
    Mono<Transaction> findTransactionByIdAndMerchantId(UUID id, UUID merchantId);
    Flux<Transaction> findTransactionByCreatedAtBetween(LocalDateTime begin, LocalDateTime end);
}
