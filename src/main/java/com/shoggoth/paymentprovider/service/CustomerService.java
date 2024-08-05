package com.shoggoth.paymentprovider.service;

import com.shoggoth.paymentprovider.dto.CustomerRequestResponse;
import com.shoggoth.paymentprovider.entity.Customer;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Mono<Customer> getCustomer(CustomerRequestResponse customerRequest);
}
