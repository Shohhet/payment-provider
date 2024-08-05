package com.shoggoth.paymentprovider.service;

import com.shoggoth.paymentprovider.dto.CreateTransactionRequest;
import com.shoggoth.paymentprovider.dto.CreateTransactionResponse;
import com.shoggoth.paymentprovider.dto.GetTransactionResponse;
import com.shoggoth.paymentprovider.entity.Transaction;
import com.shoggoth.paymentprovider.entity.TransactionStatus;
import com.shoggoth.paymentprovider.entity.TransactionType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TransactionService {
    Mono<CreateTransactionResponse> createTransaction(TransactionType transactionType, CreateTransactionRequest createTransactionRequest);
    Mono<GetTransactionResponse> getTransactionDetails(TransactionType transactionType, UUID id);
    Flux<GetTransactionResponse> getTransactions(TransactionType transactionType);
    Flux<GetTransactionResponse> getTransactions(TransactionType transactionType, LocalDateTime startDate, LocalDateTime endDate);
    Mono<Transaction> acceptTransaction(Transaction transaction);
    Mono<Transaction> rejectTransaction(Transaction transaction);
    Mono<Transaction> setRelatedEntities(Transaction transaction);
}
