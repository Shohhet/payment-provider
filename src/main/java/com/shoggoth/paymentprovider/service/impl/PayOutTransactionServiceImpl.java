package com.shoggoth.paymentprovider.service.impl;

import com.shoggoth.paymentprovider.dto.*;
import com.shoggoth.paymentprovider.entity.*;
import com.shoggoth.paymentprovider.exception.NotFoundException;
import com.shoggoth.paymentprovider.exception.TimePeriodException;
import com.shoggoth.paymentprovider.exception.TransactionDataException;
import com.shoggoth.paymentprovider.mapper.TransactionMapper;
import com.shoggoth.paymentprovider.repository.TransactionRepository;
import com.shoggoth.paymentprovider.service.MerchantService;
import com.shoggoth.paymentprovider.service.PayOutTransactionService;
import com.shoggoth.paymentprovider.service.PaymentCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayOutTransactionServiceImpl implements PayOutTransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final PaymentCardService paymentCardService;
    private final MerchantService merchantService;

    @Override
    public Mono<CreatePayOutTransactionResponseDto> createPayOutTransaction(CreatePayOutTransactionRequestDto transactionDto) {
        return paymentCardService.getPaymentCardByNumber(transactionDto.cardData().cardNumber())
                .switchIfEmpty(Mono.error(new NotFoundException("Payment card with number %s not found.".formatted(transactionDto.cardData().cardNumber()))))
                .flatMap(paymentCard -> {
                            if (isCardExpired(paymentCard)) {
                                return Mono.error(new TransactionDataException("Payment card expired."));
                            } else if (!isCardOwnerDataValid(paymentCard.getOwner(), transactionDto.customer())) {
                                return Mono.error(new TransactionDataException("Wrong card owner data."));
                            } else {
                                return Mono.just(paymentCard);
                            }
                        }
                )
                .zipWith(merchantService.getAuthenticatedMerchant()
                        .flatMap(merchant -> {
                                    if (merchant.getCurrency().equals(transactionDto.currency())) {
                                        return merchantService.reserveFounds(merchant, transactionDto.amount());
                                    } else {
                                        return Mono.error(new TransactionDataException("Wrong transaction currency."));
                                    }
                                }
                        )
                )
                .flatMap(tuple -> {
                            var paymentCard = tuple.getT1();
                            var merchant = tuple.getT2();
                            var transaction = transactionMapper.createRequestDtoToTransaction(transactionDto).toBuilder()
                                    .paymentCardId(paymentCard.getId())
                                    .type(TransactionType.PAY_OUT)
                                    .createdAt(LocalDateTime.now())
                                    .updatedAt(LocalDateTime.now())
                                    .status(TransactionStatus.IN_PROGRESS)
                                    .message("Transaction created.")
                                    .customer(paymentCard.getOwner())
                                    .merchantId(merchant.getId())
                                    .merchant(merchant)
                                    .build();
                            return transactionRepository.save(transaction);
                        }
                )
                .map(transactionMapper::payOutTransactionToCreateResponseDto);
    }

    @Override
    public Mono<GetPayOutTransactionDto> getPayOutDetails(UUID id) {
        return merchantService.getAuthenticatedMerchant()
                .flatMap(merchant -> transactionRepository.findByIdAndMerchantIdAndType(id, merchant.getId(), TransactionType.PAY_OUT))
                .flatMap(this::setRelatedEntities)
                .map(transactionMapper::payOutTransactionToGetDto);
    }

    @Override
    public Flux<GetPayOutTransactionDto> getPayOuts() {
        return merchantService.getAuthenticatedMerchant()
                .flatMapMany(merchant ->
                        transactionRepository.findByCreatedAtBetweenAndMerchantIdAndType(LocalDate.now().atStartOfDay(), LocalDateTime.now(), merchant.getId(), TransactionType.TOP_UP))
                .flatMap(this::setRelatedEntities)
                .map(transactionMapper::payOutTransactionToGetDto);
    }

    @Override
    public Flux<GetPayOutTransactionDto> getPayOuts(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            return Flux.error(new TimePeriodException("End date is earlier then start date."));
        } else {
            return merchantService.getAuthenticatedMerchant()
                    .flatMapMany(merchant ->
                            transactionRepository.findByCreatedAtBetweenAndMerchantIdAndType(startDate, endDate, merchant.getId(), TransactionType.TOP_UP))
                    .flatMap(this::setRelatedEntities)
                    .map(transactionMapper::payOutTransactionToGetDto);
        }
    }

    private Mono<Transaction> setRelatedEntities(Transaction transaction) {
        return Mono.just(transaction)
                .zipWith(paymentCardService.getPaymentCardById(transaction.getPaymentCardId()))
                .map(tuple -> {
                            var filledTransaction = tuple.getT1();
                            var paymentCard = tuple.getT2();
                            filledTransaction.setCard(paymentCard);
                            filledTransaction.setCustomer(paymentCard.getOwner());
                            return filledTransaction;
                        }
                );
    }

    private boolean isCardExpired(PaymentCard paymentCard) {
        return paymentCard.getExpirationDate().isBefore(LocalDate.now());
    }

    private boolean isCardOwnerDataValid(Customer customer, CustomerDto customerDto) {
        return customer.getFirstName().equals(customerDto.firstName()) &&
               customer.getLastName().equals(customerDto.lastName()) &&
               customer.getCountryCode().equals(customerDto.country());
    }
}
