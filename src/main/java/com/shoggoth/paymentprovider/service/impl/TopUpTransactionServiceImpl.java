package com.shoggoth.paymentprovider.service.impl;

import com.shoggoth.paymentprovider.dto.CreateTopUpTransactionRequestDto;
import com.shoggoth.paymentprovider.dto.CreateTopUpTransactionResponseDto;
import com.shoggoth.paymentprovider.dto.CustomerDto;
import com.shoggoth.paymentprovider.dto.GetTopUpTransactionDto;
import com.shoggoth.paymentprovider.entity.Customer;
import com.shoggoth.paymentprovider.entity.PaymentMethod;
import com.shoggoth.paymentprovider.entity.TransactionStatus;
import com.shoggoth.paymentprovider.entity.TransactionType;
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


    @Override
    public Mono<CreateTopUpTransactionResponseDto> createTopUpTransaction(CreateTopUpTransactionRequestDto transactionDto) {

        return paymentCardService.getPaymentCardByNumber(transactionDto.cardData().cardNumber())
                .flatMap(paymentCard -> {
                            if (isCardOwnerDataValid(paymentCard.getOwner(), transactionDto.customer())) {
                                return Mono.just(paymentCard);
                            }
                            else {
                                return Mono.error(new TransactionDataException("Wrong card owner data"));
                            }
                        }
                )
                .switchIfEmpty(paymentCardService.createPaymentCard(transactionDto))
                .flatMap(paymentCard -> paymentCardService.reserveFounds(paymentCard, new BigDecimal(transactionDto.amount())))
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
    public Flux<GetTopUpTransactionDto> getTopUps(Date startDate, Date endDate) {
        return null;
    }

    private boolean isCardOwnerDataValid(Customer customer, CustomerDto customerDto) {
        return customer.getFirstName().equals(customerDto.firstName()) &&
               customer.getLastName().equals(customerDto.lastName()) &&
               customer.getCountryCode().equals(customerDto.country());
    }

}
