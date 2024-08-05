package com.shoggoth.paymentprovider.mapper;

import com.shoggoth.paymentprovider.dto.CustomerRequestResponse;
import com.shoggoth.paymentprovider.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "countryCode", source = "country")
    @Mapping(target = "id", ignore = true)
    Customer requestToCustomer(CustomerRequestResponse customerDto);

    @Mapping(target = "country", source = "countryCode")
    CustomerRequestResponse customerToResponse(Customer customer);
}
