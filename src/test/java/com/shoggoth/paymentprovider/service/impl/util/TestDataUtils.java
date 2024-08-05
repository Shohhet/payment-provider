package com.shoggoth.paymentprovider.service.impl.util;

import com.shoggoth.paymentprovider.dto.CustomerRequestResponse;
import com.shoggoth.paymentprovider.entity.Customer;

import java.util.UUID;

public class TestDataUtils {
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



}
