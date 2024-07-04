package com.shoggoth.paymentprovider.repository;

import com.shoggoth.paymentprovider.entity.Transaction;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TransactionRepository extends R2dbcRepository<Transaction, UUID> {
    Mono<Transaction> findTransactionByIdAndMerchantId(UUID id, UUID merchantId);
}
