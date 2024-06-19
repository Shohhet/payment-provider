package com.shoggoth.paymentprovider.service;

import com.shoggoth.paymentprovider.dto.CustomerDto;
import com.shoggoth.paymentprovider.entity.Customer;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Mono<Customer> findCustomerByPersonalData(CustomerDto customerDto);

    Mono<Customer> createCustomer(CustomerDto customerDto);
}
