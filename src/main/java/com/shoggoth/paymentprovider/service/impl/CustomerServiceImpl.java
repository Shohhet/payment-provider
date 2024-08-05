package com.shoggoth.paymentprovider.service.impl;

import com.shoggoth.paymentprovider.dto.CustomerRequestResponse;
import com.shoggoth.paymentprovider.entity.Customer;
import com.shoggoth.paymentprovider.mapper.CustomerMapper;
import com.shoggoth.paymentprovider.repository.CustomerRepository;
import com.shoggoth.paymentprovider.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public Mono<Customer> getCustomer(CustomerRequestResponse requestPayload) {
        var customer = customerMapper.requestToCustomer(requestPayload);
        return customerRepository.findCustomerByFirstNameAndLastNameAndCountryCode(requestPayload.firstName(), requestPayload.lastName(), requestPayload.country())
                .doOnSuccess(c -> log.debug("Get existing customer: {}", c))
                .switchIfEmpty(Mono.defer(() -> customerRepository.save(customer)))
                .doOnSuccess(c -> log.debug("Create new customer: {}", c))
                .doOnError(throwable -> log.error("Error creating new customer: {}", throwable.getMessage()));
    }
}
