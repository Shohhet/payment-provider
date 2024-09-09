package com.shoggoth.paymentprovider.service.impl.util;

import com.shoggoth.paymentprovider.dto.CustomerRequestResponse;
import com.shoggoth.paymentprovider.entity.BankAccount;
import com.shoggoth.paymentprovider.entity.Customer;

import java.math.BigDecimal;
import java.util.UUID;

public class TestDataUtils {
    public static UUID ACCOUNT_ID = UUID.randomUUID();
    public static String CURRENCY = "EUR";
    public static BigDecimal INITIAL_ACCOUNT_AMOUNT = new BigDecimal("1000.00");
    public static BigDecimal TRANSACTION_AMOUNT = new BigDecimal("100.00");
    public static BigDecimal TOO_BIG_TRANSACTION_AMOUNT = new BigDecimal("1100.00");


    public static CustomerRequestResponse getCustomerRequest() {
        return new CustomerRequestResponse(
                "Ivan",
                "Ivanov",
                "ru"
        );
    }


    public static Customer getPersistedCustomer() {
        return Customer.builder()
                .id(UUID.randomUUID())
                .firstName("Ivan")
                .lastName("Ivanov")
                .countryCode("ru")
                .build();
    }

    public static BankAccount getPersistedBankAccount() {
        return new BankAccount(ACCOUNT_ID, INITIAL_ACCOUNT_AMOUNT, CURRENCY);
    }

}
