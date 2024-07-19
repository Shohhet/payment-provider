package com.shoggoth.paymentprovider.service.impl;

import com.shoggoth.paymentprovider.dto.*;
import com.shoggoth.paymentprovider.entity.*;
import com.shoggoth.paymentprovider.exception.TimePeriodException;
import com.shoggoth.paymentprovider.exception.TransactionDataException;
import com.shoggoth.paymentprovider.mapper.TransactionMapper;
import com.shoggoth.paymentprovider.repository.TransactionRepository;
import com.shoggoth.paymentprovider.service.MerchantService;
import com.shoggoth.paymentprovider.service.PaymentCardService;
import com.shoggoth.paymentprovider.service.TopUpTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopUpTransactionServiceImpl implements TopUpTransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final PaymentCardService paymentCardService;
    private final MerchantService merchantService;


    @Override
    @Transactional
    public Mono<CreateTopUpTransactionResponseDto> createTopUpTransaction(CreateTopUpTransactionRequestDto transactionDto) {

        return paymentCardService.getPaymentCardByNumber(transactionDto.cardData().cardNumber())
                .flatMap(paymentCard -> {
                            if (isCardOwnerDataValid(paymentCard.getOwner(), transactionDto.customer()) &&
                                isCardDataValid(paymentCard, transactionDto.cardData())) {
                                return Mono.just(paymentCard);
                            } else {
                                return Mono.error(new TransactionDataException("Card data does not match with existing card."));
                            }
                        }
                )
                .switchIfEmpty(paymentCardService.createPaymentCard(transactionDto))
                .flatMap(card -> paymentCardService.reserveFounds(card, transactionDto.amount())
                        .map(paymentCard -> transactionMapper.createRequestDtoToTransaction(transactionDto).toBuilder()
                                .paymentCardId(paymentCard.getId())
                                .type(TransactionType.TOP_UP)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .status(TransactionStatus.IN_PROGRESS)
                                .message("Transaction created.")
                                .customer(paymentCard.getOwner())
                                .build()
                        )
                        .zipWith(merchantService.getAuthenticatedMerchant())
                        .flatMap(tuple -> {
                                    var transaction = tuple.getT1();
                                    var merchant = tuple.getT2();
                                    if (transaction.getCurrency().equals(merchant.getCurrency())) {
                                        transaction.setMerchant(merchant);
                                        transaction.setMerchantId(merchant.getId());
                                        return transactionRepository.save(transaction);
                                    } else {
                                        return Mono.error(new TransactionDataException("Wrong transaction currency."));
                                    }
                                }
                        )
                        .map(transactionMapper::topUpTransactionToCreateResponseDto));
    }

    @Override
    public Mono<GetTopUpTransactionDto> getTopUpDetails(UUID id) {
        return merchantService.getAuthenticatedMerchant()
                .flatMap(merchant -> transactionRepository.findByIdAndMerchantIdAndType(id, merchant.getId(), TransactionType.TOP_UP))
                .flatMap(this::setRelatedEntities)
                .map(transactionMapper::topUpTransactionToGetDto);
    }

    @Override
    public Flux<GetTopUpTransactionDto> getTopUps() {
        return merchantService.getAuthenticatedMerchant()
                .flatMapMany(merchant ->
                        transactionRepository.findByCreatedAtBetweenAndMerchantIdAndType(LocalDate.now().atStartOfDay(), LocalDateTime.now(), merchant.getId(), TransactionType.TOP_UP))
                .flatMap(this::setRelatedEntities)
                .map(transactionMapper::topUpTransactionToGetDto);
    }

    @Override
    public Flux<GetTopUpTransactionDto> getTopUps(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            return Flux.error(new TimePeriodException("End date is earlier then start date."));
        } else {
            return merchantService.getAuthenticatedMerchant()
                    .flatMapMany(merchant ->
                            transactionRepository.findByCreatedAtBetweenAndMerchantIdAndType(startDate, endDate, merchant.getId(), TransactionType.TOP_UP))
                    .flatMap(this::setRelatedEntities)
                    .map(transactionMapper::topUpTransactionToGetDto);
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

    private boolean isCardOwnerDataValid(Customer customer, CustomerDto customerDto) {
        return customer.getFirstName().equals(customerDto.firstName()) &&
               customer.getLastName().equals(customerDto.lastName()) &&
               customer.getCountryCode().equals(customerDto.country());
    }

    private boolean isCardDataValid(PaymentCard paymentCard, CreatePaymentCardDto paymentCardDto) {
        return YearMonth.from(paymentCard.getExpirationDate()).equals(paymentCardDto.expirationDate()) &&
               paymentCard.getCvv().equals(paymentCardDto.cvv());
    }

}
