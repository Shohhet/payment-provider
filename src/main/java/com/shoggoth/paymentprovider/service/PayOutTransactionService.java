package com.shoggoth.paymentprovider.service;

import com.shoggoth.paymentprovider.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

public interface PayOutTransactionService {
    Mono<CreatePayOutTransactionResponseDto> createPayOutTransaction(CreatePayOutTransactionRequestDto transactionDto);
    Mono<GetPayOutTransactionDto> getPayOutDetails(UUID id);
    Flux<GetPayOutTransactionDto> getPayOuts();
    Flux<GetPayOutTransactionDto> getPayOuts(LocalDateTime startDate, LocalDateTime endDate);
}
