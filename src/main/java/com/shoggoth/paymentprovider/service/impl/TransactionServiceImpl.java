package com.shoggoth.paymentprovider.service.impl;

import com.shoggoth.paymentprovider.dto.CreateTransactionRequest;
import com.shoggoth.paymentprovider.dto.CreateTransactionResponse;
import com.shoggoth.paymentprovider.dto.GetTransactionResponse;
import com.shoggoth.paymentprovider.entity.*;
import com.shoggoth.paymentprovider.exception.NotFoundException;
import com.shoggoth.paymentprovider.exception.TimePeriodException;
import com.shoggoth.paymentprovider.mapper.TransactionMapper;
import com.shoggoth.paymentprovider.repository.TransactionRepository;
import com.shoggoth.paymentprovider.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.shoggoth.paymentprovider.entity.TransactionStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final PaymentCardService paymentCardService;
    private final MerchantService merchantService;
    private final TransactionMapper transactionMapper;
    private final TransactionRepository transactionRepository;
    private final ProcessingService processingService;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<CreateTransactionResponse> createTransaction(TransactionType transactionType, CreateTransactionRequest requestPayload) {
        var transientTransaction = transactionMapper.createRequestToTransaction(requestPayload)
                .toBuilder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(IN_PROGRESS)
                .message("Transaction created.")
                .type(transactionType)
                .build();
        return Mono.zip(Mono.just(transientTransaction),
                        merchantService.getAuthenticatedMerchantId(),
                        paymentCardService.getOrCreatePaymentCard(requestPayload))
                .map(t -> {
                            Transaction transaction = t.getT1();
                            UUID merchantId = t.getT2();
                            PaymentCard paymentCard = t.getT3();
                            return transaction.toBuilder()
                                    .paymentCardId(paymentCard.getId())
                                    .merchantId(merchantId)
                                    .build();
                        }
                )
                .flatMap(transactionRepository::save)
                .flatMap(this::setRelatedEntities)
                .flatMap(processingService::process)
                .as(transactionalOperator::transactional)
                .doOnSuccess(t -> log.debug("New transaction created: {}", t))
                .doOnError(throwable -> log.error("Error creating new transaction: {}", throwable.getMessage()))
                .map(transactionMapper::transactionToCreateResponse);

    }

    @Override
    public Mono<GetTransactionResponse> getTransactionDetails(TransactionType transactionType, UUID transactionId) {
        return merchantService.getAuthenticatedMerchantId()
                .flatMap(merchantId ->
                        transactionRepository.findByIdAndMerchantIdAndType(transactionId, merchantId, transactionType))
                .switchIfEmpty(Mono.error(new NotFoundException("Transaction with id: %s not found.".formatted(transactionId), "NOT_FOUND_ERROR")))
                .map(transactionMapper::transactionToGetResponse);
    }

    @Override
    public Flux<GetTransactionResponse> getTransactions(TransactionType transactionType) {
        return merchantService.getAuthenticatedMerchantId()
                .flatMapMany(merchantId -> transactionRepository.findByCreatedAtBetweenAndMerchantIdAndType(
                        LocalDate.now().atStartOfDay(),
                        LocalDateTime.now(),
                        merchantId,
                        transactionType)
                )
                .flatMap(this::setRelatedEntities)
                .map(transactionMapper::transactionToGetResponse);

    }

    @Override
    public Flux<GetTransactionResponse> getTransactions(TransactionType transactionType, LocalDateTime startDate, LocalDateTime endDate) {
        if (!startDate.isBefore(endDate)) {
            return Flux.error(new TimePeriodException("End date is earlier then start date.", "VALIDATION_ERROR"));
        }
        return merchantService.getAuthenticatedMerchantId()
                .flatMapMany(merchantId -> transactionRepository.findByCreatedAtBetweenAndMerchantIdAndType(
                        startDate,
                        endDate,
                        merchantId,
                        transactionType)
                )
                .flatMap(this::setRelatedEntities)
                .map(transactionMapper::transactionToGetResponse);

    }

    @Override
    public Mono<Transaction> acceptTransaction(Transaction transaction) {
        return transactionRepository.save(
                        transaction.toBuilder()
                                .updatedAt(LocalDateTime.now())
                                .status(APPROVED)
                                .message("Transaction approved.")
                                .build()
                )
                .flatMap(this::setRelatedEntities)
                .flatMap(processingService::process)
                .as(transactionalOperator::transactional)
                .doOnSuccess(tr -> log.debug("Transaction accepted: {}", tr));
    }

    @Override
    public Mono<Transaction> rejectTransaction(Transaction transaction) {
        return transactionRepository.save(
                        transaction.toBuilder()
                                .updatedAt(LocalDateTime.now())
                                .status(FAILED)
                                .message("Transaction failed.")
                                .build()
                )
                .flatMap(this::setRelatedEntities)
                .flatMap(processingService::process)
                .as(transactionalOperator::transactional)
                .doOnSuccess(tr -> log.debug("Transaction rejected: {}", tr));
    }


    public Mono<Transaction> setRelatedEntities(Transaction transaction) {
        return Mono.zip(paymentCardService.getPaymentCardById(transaction.getPaymentCardId()),
                        merchantService.getMerchantById(transaction.getMerchantId())
                )
                .map(t -> {
                            PaymentCard paymentCard = t.getT1();
                            Merchant merchant = t.getT2();
                            return transaction.toBuilder()
                                    .card(paymentCard)
                                    .customer(paymentCard.getOwner())
                                    .merchant(merchant)
                                    .build();
                        }
                ).doOnSuccess(transaction1 -> log.debug("Transaction set related entities: {}", transaction1));
    }
}
