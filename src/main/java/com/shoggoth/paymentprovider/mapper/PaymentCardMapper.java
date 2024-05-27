package com.shoggoth.paymentprovider.mapper;

import com.shoggoth.paymentprovider.dto.CreatePaymentCardDto;
import com.shoggoth.paymentprovider.entity.PaymentCard;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

    PaymentCard CreateDtoToPaymentCard(CreatePaymentCardDto dto);

}
