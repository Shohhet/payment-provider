package com.shoggoth.paymentprovider.service.impl;

import com.shoggoth.paymentprovider.dto.*;
import com.shoggoth.paymentprovider.entity.*;
import com.shoggoth.paymentprovider.exception.TransactionDataException;
import com.shoggoth.paymentprovider.mapper.TransactionMapper;
import com.shoggoth.paymentprovider.repository.TransactionRepository;
import com.shoggoth.paymentprovider.service.MerchantService;
import com.shoggoth.paymentprovider.service.PaymentCardService;
import com.shoggoth.paymentprovider.service.TopUpTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopUpTransactionServiceImpl implements TopUpTransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final PaymentCardService paymentCardService;
    private final MerchantService merchantService;


    @Override
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
                        .map(transactionMapper::transactionToCreateResponseDto));
    }

    @Override
    public Mono<GetTopUpTransactionDto> getTopUpDetails(UUID id) {
        return merchantService.getAuthenticatedMerchant()
                .flatMap(merchant -> transactionRepository.findTransactionByIdAndMerchantId(id, merchant.getId()))
                .zipWhen(transaction -> paymentCardService.getPaymentCardById(transaction.getPaymentCardId()))
                .map(tuple -> {
                            var transaction = tuple.getT1();
                            var paymentCard = tuple.getT2();
                            transaction.setCard(paymentCard);
                            transaction.setCustomer(paymentCard.getOwner());
                            return transaction;
                        }
                )
                .map(transactionMapper::transactionToGetDto);
    }

    @Override
    public Flux<GetTopUpTransactionDto> getTopUps() {
        return transactionRepository.findTransactionByCreatedAtBetween(LocalDate.now().atStartOfDay(), LocalDateTime.now())
                .map(transactionMapper::transactionToGetDto);
    }

    @Override
    public Flux<GetTopUpTransactionDto> getTopUps(Date startDate, Date endDate) {
        return Flux.empty();
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
