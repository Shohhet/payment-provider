package com.shoggoth.paymentprovider.rest;

import com.shoggoth.paymentprovider.dto.CreateTopUpTransactionRequestDto;
import com.shoggoth.paymentprovider.dto.CreateTopUpTransactionResponseDto;
import com.shoggoth.paymentprovider.dto.GetTopUpTransactionDto;
import com.shoggoth.paymentprovider.service.TopUpTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/payments")
@RequiredArgsConstructor
public class TopUpRestControllerV1 {

    private final TopUpTransactionService topUpTransactionService;

    @PostMapping("/top_up")
    public Mono<CreateTopUpTransactionResponseDto> create(@RequestBody @Validated CreateTopUpTransactionRequestDto createDto) {
        return topUpTransactionService.createTopUpTransaction(createDto);
    }

    @GetMapping("/top_up/{id}/details")
    public Mono<GetTopUpTransactionDto> getTopUpTransactions(@PathVariable UUID id) {
        return topUpTransactionService.getTopUpDetails(id);
    }

}
