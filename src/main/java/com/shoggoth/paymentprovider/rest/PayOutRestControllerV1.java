package com.shoggoth.paymentprovider.rest;

import com.shoggoth.paymentprovider.dto.CreateTransactionRequest;
import com.shoggoth.paymentprovider.dto.CreateTransactionResponse;
import com.shoggoth.paymentprovider.dto.GetTransactionResponse;
import com.shoggoth.paymentprovider.service.TransactionService;
import com.shoggoth.paymentprovider.validation.ConsistentBeginEndTimeInterval;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static com.shoggoth.paymentprovider.entity.TransactionType.PAY_OUT;

@RestController
@RequestMapping(PayOutRestControllerV1.PAYOUT_CONTROLLER_URL)
@RequiredArgsConstructor
public class PayOutRestControllerV1 {

    public static final String PAYOUT_CONTROLLER_URL = "api/v1/payments/payout";
    public static final String PAYOUT_CONTROLLER_CREATE_TRANSACTION_URL = "/";
    public static final String PAYOUT_CONTROLLER_GET_TRANSACTION_URL = "/{id}/details";
    public static final String PAYOUT_CONTROLLER_GET_TRANSACTION_LIST_URL = "/list";
    public static final String START_DATE_PARAM = "start_date";
    public static final String END_DATE_PARAM = "end_date";

    private final TransactionService transactionService;

    @PostMapping(PAYOUT_CONTROLLER_CREATE_TRANSACTION_URL)
    public Mono<CreateTransactionResponse> createPayOutTransaction(@RequestBody @Valid CreateTransactionRequest payload) {
        return transactionService.createTransaction(PAY_OUT, payload);
    }

    @GetMapping(PAYOUT_CONTROLLER_GET_TRANSACTION_URL)
    public Mono<GetTransactionResponse> getPayOutTransaction(@PathVariable UUID id) {
        return transactionService.getTransactionDetails(PAY_OUT, id);
    }

    @GetMapping(PAYOUT_CONTROLLER_GET_TRANSACTION_LIST_URL)
    public Flux<GetTransactionResponse> getPayOutTransactionsList() {
        return transactionService.getTransactions(PAY_OUT);
    }

    @GetMapping(value = PAYOUT_CONTROLLER_GET_TRANSACTION_LIST_URL, params = {START_DATE_PARAM, END_DATE_PARAM})
    @ConsistentBeginEndTimeInterval
    public Flux<GetTransactionResponse> getPayOutTransactionsList(@RequestParam(name = START_DATE_PARAM) @NotNull @Valid Long startDate,
                                                                 @RequestParam(name = END_DATE_PARAM) @NotNull @Valid Long endDate) {
        var timeZone = ZoneId.systemDefault();
        LocalDateTime startDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(startDate), timeZone);
        LocalDateTime endDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(endDate), timeZone);
        return transactionService.getTransactions(PAY_OUT, startDateTime, endDateTime);
    }
}
