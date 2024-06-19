package com.shoggoth.paymentprovider.service.impl;

import com.shoggoth.paymentprovider.dto.CustomerDto;
import com.shoggoth.paymentprovider.entity.Customer;
import com.shoggoth.paymentprovider.mapper.CustomerMapper;
import com.shoggoth.paymentprovider.mapper.CustomerMapperImpl;
import com.shoggoth.paymentprovider.repository.CustomerRepository;
import com.shoggoth.paymentprovider.repository.PaymentCardRepository;
import com.shoggoth.paymentprovider.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final PaymentCardRepository paymentCardRepository;
    private final CustomerMapper customerMapper;

    @Override
    public Mono<Customer> findCustomerByPersonalData(CustomerDto customerDto) {
        return customerRepository.findCustomerByFirstNameAndLastNameAndCountryCode(
                        customerDto.firstName(),
                        customerDto.lastName(),
                        customerDto.country()
                )
                .zipWhen(customer -> paymentCardRepository.findPaymentCardsByOwnerId(customer.getId()).collectList())
                .map(tuple -> {
                            var customer = tuple.getT1();
                            var paymentCads = tuple.getT2();
                            customer.setCards(paymentCads);
                            return customer;
                        }
                );
    }

    @Override
    public Mono<Customer> createCustomer(CustomerDto customerDto) {
        return customerRepository.save(customerMapper.dtoToCustomer(customerDto));
    }
}
