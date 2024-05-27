package com.shoggoth.paymentprovider.service;

import com.shoggoth.paymentprovider.dto.CreateTopUpTransactionRequestDto;
import com.shoggoth.paymentprovider.dto.CreateTopUpTransactionResponseDto;
import com.shoggoth.paymentprovider.dto.GetTopUpTransactionResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.UUID;

public interface TopUpTransactionService {
    Mono<CreateTopUpTransactionResponseDto> create(CreateTopUpTransactionRequestDto transactionDto);
    Mono<GetTopUpTransactionResponseDto> getTopUpDetails(UUID id);
    Flux<GetTopUpTransactionResponseDto> getTopUps(Date startDate, Date endDate);
}
