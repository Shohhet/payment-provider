package com.shoggoth.paymentprovider.repository;

import com.shoggoth.paymentprovider.entity.Customer;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CustomerRepository extends R2dbcRepository<Customer, UUID> {
    Mono<Customer> findCustomerById(UUID id);
}
