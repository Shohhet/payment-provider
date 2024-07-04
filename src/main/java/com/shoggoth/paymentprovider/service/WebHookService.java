package com.shoggoth.paymentprovider.service;

import com.shoggoth.paymentprovider.entity.Transaction;
import com.shoggoth.paymentprovider.entity.WebHook;
import reactor.core.publisher.Mono;

public interface WebHookService {
    Mono<WebHook> createWebHook(Transaction transaction);
}
