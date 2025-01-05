package com.shoggoth.paymentprovider.service.impl;

import com.shoggoth.paymentprovider.dto.CreateTransactionRequest;
import com.shoggoth.paymentprovider.dto.CreateTransactionResponse;
import com.shoggoth.paymentprovider.dto.GetTransactionResponse;
import com.shoggoth.paymentprovider.entity.*;
import com.shoggoth.paymentprovider.exception.NotFoundException;
import com.shoggoth.paymentprovider.exception.TransactionDataException;
import com.shoggoth.paymentprovider.mapper.TransactionMapper;
import com.shoggoth.paymentprovider.repository.TransactionRepository;
import com.shoggoth.paymentprovider.service.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private final WebHookService webHookService;

    @SneakyThrows
    @Override
    public Mono<CreateTransactionResponse> createTransaction(TransactionType transactionType, CreateTransactionRequest requestPayload) {

        var transientTransaction = transactionMapper.createRequestToTransaction(requestPayload)
                .toBuilder()
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))
                .status(IN_PROGRESS)
                .message(IN_PROGRESS.getMessage())
                .type(transactionType)
                .build();

        return Mono.zip(this.checkTransactionCurrency(transientTransaction),
                        merchantService.getAuthenticatedMerchantId(),
                        paymentCardService.getOrCreatePaymentCard(requestPayload, transactionType))
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
                .doOnNext(webHookService::sendWebHook)
                .map(transactionMapper::transactionToCreateResponse);


    }

    @Override
    public Mono<GetTransactionResponse> getTransactionDetails(TransactionType transactionType, UUID transactionId) {
        return merchantService.getAuthenticatedMerchantId()
                .flatMap(merchantId ->
                        transactionRepository.findByIdAndMerchantIdAndType(transactionId, merchantId, transactionType))
                .flatMap(this::setRelatedEntities)
                .switchIfEmpty(Mono.error(new NotFoundException("Transaction with id: %s not found.".formatted(transactionId), "NOT_FOUND_ERROR")))
                .map(transactionMapper::transactionToGetResponse);
    }

    @Override
    public Flux<GetTransactionResponse> getTransactions(TransactionType transactionType) {
        return merchantService.getAuthenticatedMerchantId()
                .flatMapMany(merchantId -> transactionRepository.findByCreatedAtBetweenAndMerchantIdAndType(
                        LocalDate.now().atStartOfDay(),
                        LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                        merchantId,
                        transactionType)
                )
                .flatMap(this::setRelatedEntities)
                .map(transactionMapper::transactionToGetResponse);

    }

    @Override
    public Flux<GetTransactionResponse> getTransactions(TransactionType transactionType, LocalDateTime startDate, LocalDateTime endDate) {
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
                                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))
                                .status(APPROVED)
                                .message(APPROVED.getMessage())
                                .build()
                )
                .flatMap(this::setRelatedEntities)
                .flatMap(processingService::process)
                .as(transactionalOperator::transactional)
                .doOnNext(tr -> {
                            log.debug("Transaction accepted: {}", tr);
                            webHookService.sendWebHook(tr);
                        }
                )
                .doOnError(throwable -> log.error("Error accepting transaction: {}", throwable.getMessage()));
    }

    @Override
    public Mono<Transaction> rejectTransaction(Transaction transaction) {
        return transactionRepository.save(
                        transaction.toBuilder()
                                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))
                                .status(FAILED)
                                .message(FAILED.getMessage())
                                .build()
                )
                .flatMap(this::setRelatedEntities)
                .flatMap(processingService::process)
                .as(transactionalOperator::transactional)
                .doOnNext(tr -> {
                            log.debug("Transaction rejected: {}", tr);
                            webHookService.sendWebHook(tr);
                        }
                )
                .doOnError(throwable -> log.error("Error rejecting transaction: {}", throwable.getMessage()));
    }


    private Mono<Transaction> setRelatedEntities(Transaction transaction) {
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
                )
                .doOnNext(tr -> log.debug("Transaction set related entities: {}", tr))
                .doOnError(throwable -> log.error("Error setting related entities: {}", throwable.getMessage()));
    }

    private Mono<Transaction> checkTransactionCurrency(Transaction transaction) {
        return merchantService.getAuthenticatedMerchantId()
                .flatMap(merchantService::getMerchantById)
                .flatMap(merchant -> {
                            if (merchant.getBankAccount().getCurrency().equals(transaction.getCurrency())) {
                                return Mono.just(transaction);
                            } else {
                                return Mono.error(new TransactionDataException("Wrong transaction currency.", "TRANSACTION_DATA_ERROR"));
                            }
                        }
                );
    }
}
