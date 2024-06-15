package com.shoggoth.paymentprovider.mapper;

import com.shoggoth.paymentprovider.dto.CreatePaymentCardDto;
import com.shoggoth.paymentprovider.dto.PaymentCardNumberDto;
import com.shoggoth.paymentprovider.entity.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

    @Mapping(target = "number", source = "cardNumber")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bankAccountId", ignore = true)
    @Mapping(target = "bankAccount", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "owner", ignore = true)
    PaymentCard createDtoToPaymentCard(CreatePaymentCardDto dto);

    @Mapping(target = "cardNumber", source = "number")
    PaymentCardNumberDto paymentCardToGetDto(PaymentCard paymentCard);


}
