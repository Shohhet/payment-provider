package com.shoggoth.paymentprovider.mapper;

import com.shoggoth.paymentprovider.entity.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {
    @ValueMapping(target = "CARD", source = "CARD")
    PaymentMethod mapStringToPaymentMethod(String stringPaymentMethod);
}
