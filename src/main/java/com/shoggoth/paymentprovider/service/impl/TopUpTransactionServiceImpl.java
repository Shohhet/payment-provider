package com.shoggoth.paymentprovider.service.impl;

import com.shoggoth.paymentprovider.dto.CreateTopUpTransactionRequestDto;
import com.shoggoth.paymentprovider.dto.CreateTopUpTransactionResponseDto;
import com.shoggoth.paymentprovider.dto.CustomerDto;
import com.shoggoth.paymentprovider.dto.GetTopUpTransactionResponseDto;
import com.shoggoth.paymentprovider.entity.Customer;
import com.shoggoth.paymentprovider.entity.PaymentMethod;
import com.shoggoth.paymentprovider.entity.TransactionStatus;
import com.shoggoth.paymentprovider.entity.TransactionType;
import com.shoggoth.paymentprovider.exception.NotFoundException;
import com.shoggoth.paymentprovider.exception.TransactionDataException;
import com.shoggoth.paymentprovider.mapper.TransactionMapper;
import com.shoggoth.paymentprovider.repository.PaymentCardRepository;
import com.shoggoth.paymentprovider.repository.TransactionRepository;
import com.shoggoth.paymentprovider.service.MerchantService;
import com.shoggoth.paymentprovider.service.PaymentCardService;
import com.shoggoth.paymentprovider.service.TopUpTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopUpTransactionServiceImpl implements TopUpTransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final PaymentCardService paymentCardService;
    private final MerchantService merchantService;
    private final PaymentCardRepository paymentCardRepository;


    @Override
    public Mono<CreateTopUpTransactionResponseDto> create(CreateTopUpTransactionRequestDto transactionDto) {

        return paymentCardService.getPaymentCardByNumber(transactionDto.cardData().cardNumber())
                .switchIfEmpty(Mono.error(new NotFoundException("Payment card not found.")))
                .flatMap(paymentCard -> {
                    if (isCardOwnerDataValid(paymentCard.getOwner(), transactionDto.customer())) {
                        return Mono.just(paymentCard);
                    } else {
                        return Mono.error(new TransactionDataException("Payment card belong to another customer."));
                    }
                })
                .flatMap(paymentCard -> paymentCardService.reserveFounds(paymentCard, new BigDecimal(transactionDto.amount()))
                )
                .map(paymentCard -> {
                            var transaction = transactionMapper.createRequestDtoToTransaction(transactionDto);

                            transaction.setPaymentCardId(paymentCard.getId());
                            transaction.setType(TransactionType.TOP_UP);
                            transaction.setCreatedAt(LocalDateTime.now());
                            transaction.setUpdatedAt(LocalDateTime.now());
                            transaction.setStatus(TransactionStatus.IN_PROGRESS);
                            transaction.setMessage("OK");
                            transaction.setCustomer(paymentCard.getOwner());
                            transaction.setPaymentMethod(PaymentMethod.CARD);
                            return transaction;
                        }
                )
                .zipWith(merchantService.getAuthenticatedMerchant())
                .map(tuple -> {
                            var transaction = tuple.getT1();
                            var merchant = tuple.getT2();
                            transaction.setMerchant(merchant);
                            transaction.setMerchantId(merchant.getId());
                            return transaction;
                        }
                )
                .flatMap(transactionRepository::save)
                .map(transactionMapper::transactionToCreateResponseDto);


    }

    @Override
    public Mono<GetTopUpTransactionResponseDto> getTopUpDetails(UUID id) {
        return transactionRepository.findById(id)
                .zipWhen(transaction -> paymentCardService.getPaymentCardById(transaction.getPaymentCardId()))
                .map(tuple -> {
                    var transaction = tuple.getT1();
                    var paymentCard = tuple.getT2();
                    transaction.setCard(paymentCard);
                    transaction.setCustomer(paymentCard.getOwner());
                    return transaction;
                })
                .map(transactionMapper::transactionToGetDto);
    }

    @Override
    public Flux<GetTopUpTransactionResponseDto> getTopUps(Date startDate, Date endDate) {
        return null;
    }

    private boolean isCardOwnerDataValid(Customer customer, CustomerDto customerDto) {
        return customer.getFirstName().equals(customerDto.firstName()) &&
               customer.getLastName().equals(customerDto.lastName()) &&
               customer.getCountryCode().equals(customerDto.country());
    }

}
