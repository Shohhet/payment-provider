package com.shoggoth.paymentprovider.service.impl.service;

import com.shoggoth.paymentprovider.entity.Customer;
import com.shoggoth.paymentprovider.mapper.CustomerMapper;
import com.shoggoth.paymentprovider.mapper.CustomerMapperImpl;
import com.shoggoth.paymentprovider.repository.CustomerRepository;
import com.shoggoth.paymentprovider.service.impl.CustomerServiceImpl;
import com.shoggoth.paymentprovider.service.impl.util.TestDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class CustomerServiceImplTest {

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Spy
    private CustomerMapper customerMapper = new CustomerMapperImpl();


    @Test
    @DisplayName("Test create new customer functionality.")
    public void givenCustomerRequest_whenCustomerNotExist_thenNewCustomerReturned() {
        //given
        var customerRequest = TestDataUtils.getCustomerRequest();
        var persistedCustomer = TestDataUtils.getPersistedCustomer();
        when(customerRepository.findCustomerByFirstNameAndLastNameAndCountryCode(anyString(), anyString(), anyString()))
                .thenReturn(Mono.empty());

        when(customerRepository.save(any(Customer.class)))
                .thenReturn(Mono.just(persistedCustomer));

        //when
        StepVerifier.create(customerService.getCustomer(customerRequest))

                //then
                .consumeNextWith(result -> assertEquals(result, persistedCustomer))
                .verifyComplete();


    }

    @Test
    @DisplayName("Test get existing customer functionality.")
    public void givenCustomerRequest_whenCustomerExist_thenExistingCustomerReturned() {
        //given
        var customerRequest = TestDataUtils.getCustomerRequest();
        var persistedCustomer = TestDataUtils.getPersistedCustomer();
        //when
        when(customerRepository.findCustomerByFirstNameAndLastNameAndCountryCode(anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(persistedCustomer));

        StepVerifier.create(customerService.getCustomer(customerRequest))

                //then
                .consumeNextWith(result -> assertEquals(result, persistedCustomer))
                .verifyComplete();
    }

}