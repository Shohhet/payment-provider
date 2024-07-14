package com.shoggoth.paymentprovider.rest;

import com.shoggoth.paymentprovider.dto.CreateTopUpTransactionRequestDto;
import com.shoggoth.paymentprovider.dto.CreateTopUpTransactionResponseDto;
import com.shoggoth.paymentprovider.dto.GetTopUpTransactionDto;
import com.shoggoth.paymentprovider.service.TopUpTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/payments/top_up")
@RequiredArgsConstructor
public class TopUpRestControllerV1 {

    private final TopUpTransactionService topUpTransactionService;

    @PostMapping("/")
    public Mono<CreateTopUpTransactionResponseDto> create(@RequestBody @Validated CreateTopUpTransactionRequestDto createDto) {
        return topUpTransactionService.createTopUpTransaction(createDto);
    }

    @GetMapping("/{id}/details")
    public Mono<GetTopUpTransactionDto> getTopUpTransaction(@PathVariable UUID id) {
        return topUpTransactionService.getTopUpDetails(id);
    }

    @GetMapping("/list")
    public Flux<GetTopUpTransactionDto>  getTopUpTransactionsList() {
        return topUpTransactionService.getTopUps();
    }

}
