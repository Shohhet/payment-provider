package com.shoggoth.paymentprovider.service.impl.util;

import com.shoggoth.paymentprovider.dto.CreateTransactionRequest;
import com.shoggoth.paymentprovider.dto.CustomerRequestResponse;
import com.shoggoth.paymentprovider.dto.PaymentCardRequest;
import com.shoggoth.paymentprovider.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.UUID;

import static com.shoggoth.paymentprovider.entity.PaymentMethod.*;
import static com.shoggoth.paymentprovider.entity.TransactionStatus.*;
import static com.shoggoth.paymentprovider.entity.TransactionType.*;

public class TestDataUtils {
    public static UUID ACCOUNT_ID = UUID.randomUUID();
    public static UUID MERCHANT_ID = UUID.randomUUID();
    public static UUID PAYMENT_CARD_ID = UUID.randomUUID();
    public static UUID CUSTOMER_ID = UUID.randomUUID();
    public static UUID TRANSACTION_ID = UUID.randomUUID();
    public static LocalDate EXPIRATION_DATE = LocalDate.of(Year.now().getValue() + 1, 1, 31);
    public static String PAYMENT_CARD_NUMBER = "370000000100018";
    public static String CVV = "111";
    public static String CURRENCY = "EUR";
    public static String WRONG_CURRENCY = "RUB";
    public static String LANGUAGE = "ru";
    public static String FIRST_NAME = "Ivan";
    public static String LAST_NAME = "Ivanov";
    public static String COUNTRY = "RU";
    public static String NOTIFICATION_URL = "https://proselyte.net/webhook/transaction";
    public static String SECRET_KEY = "12345";
    public static BigDecimal INITIAL_ACCOUNT_AMOUNT = new BigDecimal("1000.00");
    public static BigDecimal TRANSACTION_AMOUNT = new BigDecimal("100.00");
    public static BigDecimal TOO_BIG_TRANSACTION_AMOUNT = new BigDecimal("1100.00");


    public static CustomerRequestResponse getCustomerRequest() {
        return new CustomerRequestResponse(
                FIRST_NAME,
                LAST_NAME,
                COUNTRY
        );
    }


    public static Customer getPersistedCustomer() {
        return Customer.builder()
                .id(CUSTOMER_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .countryCode(COUNTRY)
                .build();
    }

    public static BankAccount getPersistedBankAccount() {
        return new BankAccount(ACCOUNT_ID, INITIAL_ACCOUNT_AMOUNT, CURRENCY);
    }

    public static PaymentCard getPersistedPaymentCard() {
        return PaymentCard.builder()
                .id(PAYMENT_CARD_ID)
                .number(PAYMENT_CARD_NUMBER)
                .cvv(CVV)
                .expirationDate(EXPIRATION_DATE)
                .bankAccount(getPersistedBankAccount())
                .bankAccountId(getPersistedBankAccount().getId())
                .owner(getPersistedCustomer())
                .ownerId(getPersistedCustomer().getId())
                .build();
    }

    public static PaymentCardRequest getPaymentCardRequest() {
        return new PaymentCardRequest(
                PAYMENT_CARD_NUMBER,
                YearMonth.from(EXPIRATION_DATE),
                CVV
        );
    }

    public static CustomerRequestResponse getCustomerRequestResponse() {
        return new CustomerRequestResponse(
                FIRST_NAME,
                LAST_NAME,
                COUNTRY
        );
    }

    public static CreateTransactionRequest getCreateTransactionRequest() {
        return new CreateTransactionRequest(
                CARD,
                TRANSACTION_AMOUNT,
                CURRENCY,
                getPaymentCardRequest(),
                LANGUAGE,
                NOTIFICATION_URL,
                getCustomerRequestResponse()
        );
    }

    public static CreateTransactionRequest getCreateTransactionRequestWithWrongCurrency() {
        return new CreateTransactionRequest(
                CARD,
                TRANSACTION_AMOUNT,
                WRONG_CURRENCY,
                getPaymentCardRequest(),
                LANGUAGE,
                NOTIFICATION_URL,
                getCustomerRequestResponse()
        );
    }

    public static Merchant getMerchant() {
        return Merchant.builder()
                .id(MERCHANT_ID)
                .secretKey(SECRET_KEY)
                .bankAccountId(ACCOUNT_ID)
                .bankAccount(getPersistedBankAccount())
                .build();
    }

    public static Transaction getTopUpTransaction() {
        return Transaction.builder()
                .id(TRANSACTION_ID)
                .amount(TRANSACTION_AMOUNT)
                .currency(CURRENCY)
                .paymentMethod(CARD)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .type(TOP_UP)
                .languageCode(LANGUAGE)
                .notificationUrl(NOTIFICATION_URL)
                .status(IN_PROGRESS)
                .message(IN_PROGRESS.getMessage())
                .paymentCardId(PAYMENT_CARD_ID)
                .merchantId(MERCHANT_ID)
                .build();
    }
}
