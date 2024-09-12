package com.shoggoth.paymentprovider.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoggoth.paymentprovider.dto.GetTransactionResponse;
import com.shoggoth.paymentprovider.dto.WebhookResponse;
import com.shoggoth.paymentprovider.entity.Transaction;
import com.shoggoth.paymentprovider.entity.WebHook;
import com.shoggoth.paymentprovider.exception.SendWebhookException;
import com.shoggoth.paymentprovider.mapper.TransactionMapper;
import com.shoggoth.paymentprovider.repository.WebHookRepository;
import com.shoggoth.paymentprovider.service.WebHookService;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class WebHookServiceImpl implements WebHookService {
    private final WebHookRepository webHookRepository;
    private final WebClient webClient;
    private final TransactionMapper transactionMapper;
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public void sendWebHook(Transaction transaction) {
        AtomicInteger retryCounter = new AtomicInteger(1);
        RetryBackoffSpec retryBackoffSpec = Retry.fixedDelay(5, Duration.ofSeconds(10)).doBeforeRetry(retrySignal -> retryCounter.getAndIncrement());
        GetTransactionResponse requestBody = transactionMapper.transactionToGetResponse(transaction);
        webClient.post()
                .uri(transaction.getNotificationUrl())
                .body(Mono.just(requestBody), GetTransactionResponse.class)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.toEntity(WebhookResponse.class)
                                .flatMap(responseEntity -> {
                                            saveWebhook(transaction, responseEntity, retryCounter.intValue());
                                            return Mono.error(new SendWebhookException(Objects.requireNonNull(responseEntity.getBody()).message(), "WEB_CLIENT_ERROR"));
                                        }
                                )
                )
                .toEntity(WebhookResponse.class)
                .timeout(Duration.ofSeconds(5))
                .retryWhen(retryBackoffSpec)
                .doOnSuccess(responseEntity -> saveWebhook(transaction, responseEntity, retryCounter.intValue()))
                .subscribe();

    }

    @SneakyThrows
    private void saveWebhook(Transaction transaction, ResponseEntity<WebhookResponse> responseEntity, Integer attemptNumber) {
        Json jsonRequestBody = Json.of(objectMapper.writeValueAsString(transactionMapper.transactionToGetResponse(transaction)));
        Json jsonResponseBody = Json.of(objectMapper.writeValueAsString(responseEntity.getBody()));
        WebHook webHook = WebHook.builder()
                .transactionId(transaction.getId())
                .attemptNumber(attemptNumber)
                .requestBody(jsonRequestBody)
                .transaction(transaction)
                .responseStatus(responseEntity.getStatusCode().value())
                .responseBody(jsonResponseBody)
                .createdAt(LocalDateTime.now())
                .build();
        webHookRepository.save(webHook).subscribe();
    }
}

