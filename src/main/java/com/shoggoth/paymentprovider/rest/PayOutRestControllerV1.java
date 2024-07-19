package com.shoggoth.paymentprovider.rest;

import com.shoggoth.paymentprovider.dto.*;
import com.shoggoth.paymentprovider.service.PayOutTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/payments/payout")
@RequiredArgsConstructor
public class PayOutRestControllerV1 {
    private final PayOutTransactionService payOutTransactionService;

    @PostMapping("/")
    public Mono<CreatePayOutTransactionResponseDto> create(@RequestBody @Validated CreatePayOutTransactionRequestDto createDto) {
        return payOutTransactionService.createPayOutTransaction(createDto);
    }

    @GetMapping("/{id}/details")
    public Mono<GetPayOutTransactionDto> getPayOutTransaction(@PathVariable UUID id) {
        return payOutTransactionService.getPayOutDetails(id);
    }

    @GetMapping("/list")
    public Flux<GetPayOutTransactionDto> getPayOutTransactionsList() {
        return payOutTransactionService.getPayOuts();
    }

    @GetMapping(value = "/list", params = {"start_date", "end_date"})
    public Flux<GetPayOutTransactionDto> getTopUpTransactionsList(@RequestParam(name = "start_date") Long startDate,
                                                                 @RequestParam(name = "end_date") Long endDate) {
        var timeZone = ZoneId.systemDefault();
        return payOutTransactionService.getPayOuts(
                LocalDateTime.ofInstant(Instant.ofEpochMilli(startDate), timeZone),
                LocalDateTime.ofInstant(Instant.ofEpochMilli(endDate), timeZone)
        );
    }
}
