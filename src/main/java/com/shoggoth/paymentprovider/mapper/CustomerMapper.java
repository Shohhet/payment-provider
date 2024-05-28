package com.shoggoth.paymentprovider.mapper;

import com.shoggoth.paymentprovider.dto.CustomerDto;
import com.shoggoth.paymentprovider.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "countryCode", source = "country")
    @Mapping(target = "id", ignore = true)
    Customer dtoToCustomer(CustomerDto customerDto);

    @Mapping(target = "country", source = "countryCode")
    CustomerDto customerToDto(Customer customer);
}
