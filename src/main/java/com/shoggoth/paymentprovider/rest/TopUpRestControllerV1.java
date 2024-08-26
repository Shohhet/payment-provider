package com.shoggoth.paymentprovider.rest;

import com.shoggoth.paymentprovider.dto.CreateTransactionRequest;
import com.shoggoth.paymentprovider.dto.CreateTransactionResponse;
import com.shoggoth.paymentprovider.dto.GetTransactionResponse;
import com.shoggoth.paymentprovider.service.TransactionService;
import com.shoggoth.paymentprovider.validation.ConsistentBeginEndTimeInterval;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static com.shoggoth.paymentprovider.entity.TransactionType.PAY_OUT;
import static com.shoggoth.paymentprovider.entity.TransactionType.TOP_UP;

@RestController
@RequestMapping("api/v1/payments/top_up")
@RequiredArgsConstructor
public class TopUpRestControllerV1 {

    private final TransactionService transactionService;

    @PostMapping("/")
    public Mono<CreateTransactionResponse> createPayOutTransaction(@RequestBody @Valid CreateTransactionRequest payload) {
        return transactionService.createTransaction(TOP_UP, payload);
    }

    @GetMapping("/{id}/details")
    public Mono<GetTransactionResponse> getPayOutTransaction(@PathVariable UUID id) {
        return transactionService.getTransactionDetails(TOP_UP, id);
    }

    @GetMapping("/list")
    public Flux<GetTransactionResponse> getPayOutTransactionsList() {
        return transactionService.getTransactions(TOP_UP);
    }

    @GetMapping(value = "/list", params = {"start_date", "end_date"})
    @ConsistentBeginEndTimeInterval
    public Flux<GetTransactionResponse> getTopUpTransactionsList(@RequestParam(name = "start_date") Long startDate,
                                                                 @RequestParam(name = "end_date") Long endDate) {
        var timeZone = ZoneId.systemDefault();
        return transactionService.getTransactions(
                TOP_UP,
                LocalDateTime.ofInstant(Instant.ofEpochMilli(startDate), timeZone),
                LocalDateTime.ofInstant(Instant.ofEpochMilli(endDate), timeZone)
        );
    }

}
