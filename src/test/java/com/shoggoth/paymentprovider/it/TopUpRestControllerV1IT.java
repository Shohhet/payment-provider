package com.shoggoth.paymentprovider.it;

import com.shoggoth.paymentprovider.dto.CustomerRequestResponse;
import com.shoggoth.paymentprovider.dto.GetTransactionResponse;
import com.shoggoth.paymentprovider.dto.PaymentCardResponse;
import com.shoggoth.paymentprovider.entity.BankAccount;
import com.shoggoth.paymentprovider.entity.Customer;
import com.shoggoth.paymentprovider.entity.PaymentCard;
import com.shoggoth.paymentprovider.entity.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.ZoneId;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import static com.shoggoth.paymentprovider.entity.PaymentMethod.CARD;
import static com.shoggoth.paymentprovider.entity.TransactionStatus.IN_PROGRESS;
import static com.shoggoth.paymentprovider.entity.TransactionType.PAY_OUT;
import static com.shoggoth.paymentprovider.entity.TransactionType.TOP_UP;
import static com.shoggoth.paymentprovider.rest.PayOutRestControllerV1.END_DATE_PARAM;
import static com.shoggoth.paymentprovider.rest.PayOutRestControllerV1.START_DATE_PARAM;
import static com.shoggoth.paymentprovider.util.TestDataUtils.*;
import static com.shoggoth.paymentprovider.util.TestDataUtils.CREATED_AT;

public class TopUpRestControllerV1IT extends AbstractIntegrationTest {
    @Test
    void createTopUpTransactionTest() {
        //given
        String authCredentials = persistMerchant.getId() + ":" + SECRET_KEY;
        String authHeaderPayload = "Basic " + Base64.getEncoder().encodeToString(authCredentials.getBytes());

        String requestBody = """
                 {
                   "payment_method":"%s",
                   "amount":"%s",
                   "currency":"%s",
                   "card_data":{
                      "card_number":"%s",
                      "exp_date":"%s",
                      "cvv":"%s"
                   },
                   "language":"%s",
                   "notification_url":"%s",
                   "customer":{
                      "first_name":"%s",
                      "last_name":"%s",
                      "country":"%s"
                   }
                 }
                """
                .formatted(
                        CARD.name(),
                        TRANSACTION_AMOUNT,
                        CURRENCY,
                        PAYMENT_CARD_NUMBER,
                        STRING_EXPIRATION_DATE,
                        CVV,
                        LANGUAGE,
                        NOTIFICATION_URL,
                        FIRST_NAME,
                        LAST_NAME,
                        COUNTRY
                );

        BankAccount customerBankAccount = bankAccountRepository.save(
                        BankAccount.builder()
                                .balance(INITIAL_ACCOUNT_AMOUNT)
                                .currency(CURRENCY)
                                .build()
                )
                .block();
        Customer customer = customerRepository.save(
                        Customer.builder()
                                .firstName(FIRST_NAME)
                                .lastName(LAST_NAME)
                                .countryCode(COUNTRY)
                                .build())
                .block();

        paymentCardRepository.save(
                        PaymentCard.builder()
                                .number(PAYMENT_CARD_NUMBER)
                                .expirationDate(EXPIRATION_DATE)
                                .cvv(CVV)
                                .bankAccountId(Objects.requireNonNull(customerBankAccount).getId())
                                .ownerId(Objects.requireNonNull(customer).getId())
                                .build()
                )
                .block();

        //when
        webTestClient.post()
                .uri("/api/v1/payments/top_up/")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authHeaderPayload)
                .bodyValue(requestBody)
                .exchange()
                //then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.transaction_id").isNotEmpty()
                .jsonPath("$.status").isEqualTo(IN_PROGRESS.name())
                .jsonPath("$.message").isEqualTo(IN_PROGRESS.getMessage());
    }

    @Test
    void getTopUpTransactionTest() {
        //given
        String authCredentials = persistMerchant.getId() + ":" + SECRET_KEY;
        String authHeaderPayload = "Basic " + Base64.getEncoder().encodeToString(authCredentials.getBytes());


        BankAccount customerBankAccount = bankAccountRepository.save(
                        BankAccount.builder()
                                .balance(INITIAL_ACCOUNT_AMOUNT)
                                .currency(CURRENCY)
                                .build()
                )
                .block();
        Customer customer = customerRepository.save(
                        Customer.builder()
                                .firstName(FIRST_NAME)
                                .lastName(LAST_NAME)
                                .countryCode(COUNTRY)
                                .build())
                .block();

        PaymentCard paymentCard = paymentCardRepository.save(
                        PaymentCard.builder()
                                .number(PAYMENT_CARD_NUMBER)
                                .expirationDate(EXPIRATION_DATE)
                                .cvv(CVV)
                                .bankAccountId(Objects.requireNonNull(customerBankAccount).getId())
                                .ownerId(Objects.requireNonNull(customer).getId())
                                .build()
                )
                .block();


        Transaction trancientTransaction = getTransaction().toBuilder()
                .type(TOP_UP)
                .paymentCardId(Objects.requireNonNull(paymentCard).getId())
                .merchantId(persistMerchant.getId())
                .build();

        Transaction transaction = transactionRepository.save(trancientTransaction).block();

        GetTransactionResponse getTransactionResponse = new GetTransactionResponse(
                CARD,
                TRANSACTION_AMOUNT.toString(),
                CURRENCY,
                Objects.requireNonNull(transaction).getId(),
                CREATED_AT,
                UPDATED_AT,
                new PaymentCardResponse(paymentCard.getNumber()),
                LANGUAGE,
                NOTIFICATION_URL,
                new CustomerRequestResponse(customer.getFirstName(), customer.getLastName(), customer.getCountryCode()),
                IN_PROGRESS,
                IN_PROGRESS.getMessage()
        );

        //when
        webTestClient.get()
                .uri("/api/v1/payments/top_up/%s/details".formatted(Objects.requireNonNull(transaction).getId().toString()))
                .header(HttpHeaders.AUTHORIZATION, authHeaderPayload)
                .exchange()
                //then
                .expectStatus().isOk()
                .expectBody(GetTransactionResponse.class)
                .isEqualTo(getTransactionResponse);
    }

    @Test
    void getTopUpTransactionListTest() {
        //given
        String authCredentials = persistMerchant.getId() + ":" + SECRET_KEY;
        String authHeaderPayload = "Basic " + Base64.getEncoder().encodeToString(authCredentials.getBytes());


        BankAccount customerBankAccount = bankAccountRepository.save(
                        BankAccount.builder()
                                .balance(INITIAL_ACCOUNT_AMOUNT)
                                .currency(CURRENCY)
                                .build()
                )
                .block();
        Customer customer = customerRepository.save(
                        Customer.builder()
                                .firstName(FIRST_NAME)
                                .lastName(LAST_NAME)
                                .countryCode(COUNTRY)
                                .build())
                .block();

        PaymentCard paymentCard = paymentCardRepository.save(
                        PaymentCard.builder()
                                .number(PAYMENT_CARD_NUMBER)
                                .expirationDate(EXPIRATION_DATE)
                                .cvv(CVV)
                                .bankAccountId(Objects.requireNonNull(customerBankAccount).getId())
                                .ownerId(Objects.requireNonNull(customer).getId())
                                .build()
                )
                .block();


        Transaction trancientTransaction = getTransaction().toBuilder()
                .type(TOP_UP)
                .paymentCardId(Objects.requireNonNull(paymentCard).getId())
                .merchantId(persistMerchant.getId())
                .build();

        Transaction transaction = transactionRepository.save(trancientTransaction).block();

        GetTransactionResponse getTransactionResponse = new GetTransactionResponse(
                CARD,
                TRANSACTION_AMOUNT.toString(),
                CURRENCY,
                Objects.requireNonNull(transaction).getId(),
                CREATED_AT,
                UPDATED_AT,
                new PaymentCardResponse(paymentCard.getNumber()),
                LANGUAGE,
                NOTIFICATION_URL,
                new CustomerRequestResponse(customer.getFirstName(), customer.getLastName(), customer.getCountryCode()),
                IN_PROGRESS,
                IN_PROGRESS.getMessage()
        );


        //when
        webTestClient.get()
                .uri("/api/v1/payments/top_up/list")
                .header(HttpHeaders.AUTHORIZATION, authHeaderPayload)
                .exchange()
                //then
                .expectStatus().isOk()
                .expectBodyList(GetTransactionResponse.class)
                .isEqualTo(List.of(getTransactionResponse));

    }

    @Test
    void getTopUpTransactionListByPeriod() {
        //given
        String authCredentials = persistMerchant.getId() + ":" + SECRET_KEY;
        String authHeaderPayload = "Basic " + Base64.getEncoder().encodeToString(authCredentials.getBytes());


        BankAccount customerBankAccount = bankAccountRepository.save(
                        BankAccount.builder()
                                .balance(INITIAL_ACCOUNT_AMOUNT)
                                .currency(CURRENCY)
                                .build()
                )
                .block();
        Customer customer = customerRepository.save(
                        Customer.builder()
                                .firstName(FIRST_NAME)
                                .lastName(LAST_NAME)
                                .countryCode(COUNTRY)
                                .build())
                .block();

        PaymentCard paymentCard = paymentCardRepository.save(
                        PaymentCard.builder()
                                .number(PAYMENT_CARD_NUMBER)
                                .expirationDate(EXPIRATION_DATE)
                                .cvv(CVV)
                                .bankAccountId(Objects.requireNonNull(customerBankAccount).getId())
                                .ownerId(Objects.requireNonNull(customer).getId())
                                .build()
                )
                .block();


        Transaction trancientTransaction = getTransaction().toBuilder()
                .type(TOP_UP)
                .paymentCardId(Objects.requireNonNull(paymentCard).getId())
                .merchantId(persistMerchant.getId())
                .build();

        Transaction transaction = transactionRepository.save(trancientTransaction).block();

        GetTransactionResponse getTransactionResponse = new GetTransactionResponse(
                CARD,
                TRANSACTION_AMOUNT.toString(),
                CURRENCY,
                Objects.requireNonNull(transaction).getId(),
                CREATED_AT,
                UPDATED_AT,
                new PaymentCardResponse(paymentCard.getNumber()),
                LANGUAGE,
                NOTIFICATION_URL,
                new CustomerRequestResponse(customer.getFirstName(), customer.getLastName(), customer.getCountryCode()),
                IN_PROGRESS,
                IN_PROGRESS.getMessage()
        );

        //when
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/payments/top_up/list")
                        .queryParam(START_DATE_PARAM, CREATED_AT.minusHours(1).atZone(ZoneId.systemDefault()).toEpochSecond())
                        .queryParam(END_DATE_PARAM, CREATED_AT.plusHours(1).atZone(ZoneId.systemDefault()).toEpochSecond())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, authHeaderPayload)
                .exchange()
                //then
                .expectStatus().isOk()
                .expectBodyList(GetTransactionResponse.class)
                .isEqualTo(List.of(getTransactionResponse));
    }
}
