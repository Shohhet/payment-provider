package com.shoggoth.paymentprovider.rest;

import com.shoggoth.paymentprovider.dto.CreateTransactionRequest;
import com.shoggoth.paymentprovider.dto.CreateTransactionResponse;
import com.shoggoth.paymentprovider.dto.GetTransactionResponse;
import com.shoggoth.paymentprovider.entity.TransactionType;
import com.shoggoth.paymentprovider.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

@RestController
@RequestMapping("api/v1/payments/payout")
@RequiredArgsConstructor
public class PayOutRestControllerV1 {
    private final TransactionService transactionService;

    @PostMapping("/")
    public Mono<CreateTransactionResponse> createPayOutTransaction(@RequestBody @Valid CreateTransactionRequest payload) {
        return transactionService.createTransaction(PAY_OUT, payload);
    }

    @GetMapping("/{id}/details")
    public Mono<GetTransactionResponse> getPayOutTransaction(@PathVariable UUID id) {
        return transactionService.getTransactionDetails(PAY_OUT, id);
    }

    @GetMapping("/list")
    public Flux<GetTransactionResponse> getPayOutTransactionsList() {
        return transactionService.getTransactions(PAY_OUT);
    }

    @GetMapping(value = "/list", params = {"start_date", "end_date"})
    public Flux<GetTransactionResponse> getTopUpTransactionsList(@RequestParam(name = "start_date") @NotNull Long startDate,
                                                                 @RequestParam(name = "end_date") @NotNull Long endDate) {
        var timeZone = ZoneId.systemDefault();
        return transactionService.getTransactions(
                PAY_OUT,
                LocalDateTime.ofInstant(Instant.ofEpochMilli(startDate), timeZone),
                LocalDateTime.ofInstant(Instant.ofEpochMilli(endDate), timeZone)
        );
    }
}
