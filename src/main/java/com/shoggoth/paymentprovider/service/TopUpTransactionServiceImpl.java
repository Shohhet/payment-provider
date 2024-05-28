package com.shoggoth.paymentprovider.service;

import com.shoggoth.paymentprovider.dto.CreateTopUpTransactionRequestDto;
import com.shoggoth.paymentprovider.dto.CreateTopUpTransactionResponseDto;
import com.shoggoth.paymentprovider.dto.GetTopUpTransactionResponseDto;
import com.shoggoth.paymentprovider.entity.TransactionStatus;
import com.shoggoth.paymentprovider.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopUpTransactionServiceImpl implements TopUpTransactionService {

    private final TransactionMapper transactionMapper;

    @Override
    public Mono<CreateTopUpTransactionResponseDto> create(CreateTopUpTransactionRequestDto transactionDto) {
        return Mono.just(transactionMapper.createRequestDtoToTransaction(transactionDto))
                .map(transaction -> {
                            transaction.setId(UUID.randomUUID());
                            transaction.setCreatedAt(LocalDateTime.now());
                            transaction.setUpdatedAt(LocalDateTime.now());
                            transaction.setStatus(TransactionStatus.IN_PROGRESS);
                            transaction.setMessage("OK");
                            return transaction;
                        }
                )
                .map(transactionMapper::transactionToCreateResponseDto);
    }

    @Override
    public Mono<GetTopUpTransactionResponseDto> getTopUpDetails(UUID id) {
        return null;
    }

    @Override
    public Flux<GetTopUpTransactionResponseDto> getTopUps(Date startDate, Date endDate) {
        return null;
    }
}
