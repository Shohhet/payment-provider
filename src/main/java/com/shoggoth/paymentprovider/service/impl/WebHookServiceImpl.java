package com.shoggoth.paymentprovider.service.impl;

import com.shoggoth.paymentprovider.entity.Transaction;
import com.shoggoth.paymentprovider.entity.WebHook;
import com.shoggoth.paymentprovider.repository.WebHookRepository;
import com.shoggoth.paymentprovider.service.WebHookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WebHookServiceImpl implements WebHookService {
    private final WebHookRepository webHookRepository;

    @Override
    public Mono<WebHook> createWebHook(Transaction transaction) {
        var webHook = WebHook.builder()
                .transactionId(transaction.getId())
                .transaction(transaction)
                .build();
        return webHookRepository.save(webHook);


    }
}
