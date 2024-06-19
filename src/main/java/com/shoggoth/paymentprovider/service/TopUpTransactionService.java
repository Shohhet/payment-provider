package com.shoggoth.paymentprovider.service;

import com.shoggoth.paymentprovider.dto.CreateTopUpTransactionRequestDto;
import com.shoggoth.paymentprovider.dto.CreateTopUpTransactionResponseDto;
import com.shoggoth.paymentprovider.dto.GetTopUpTransactionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.UUID;

public interface TopUpTransactionService {
    Mono<CreateTopUpTransactionResponseDto> createTopUpTransaction(CreateTopUpTransactionRequestDto transactionDto);
    Mono<GetTopUpTransactionDto> getTopUpDetails(UUID id);
    Flux<GetTopUpTransactionDto> getTopUps(Date startDate, Date endDate);
}
