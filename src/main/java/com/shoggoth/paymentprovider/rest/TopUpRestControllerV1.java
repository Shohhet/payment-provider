package com.shoggoth.paymentprovider.rest;

import com.shoggoth.paymentprovider.dto.CreateTopUpTransactionRequestDto;
import com.shoggoth.paymentprovider.dto.CreateTopUpTransactionResponseDto;
import com.shoggoth.paymentprovider.service.TopUpTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/payments")
@RequiredArgsConstructor
public class TopUpRestControllerV1 {

    private final TopUpTransactionService topUpTransactionService;

    @PostMapping("/top_up")
    public Mono<CreateTopUpTransactionResponseDto> create(@RequestBody CreateTopUpTransactionRequestDto createDto) {
        return topUpTransactionService.create(createDto);
    }

}
