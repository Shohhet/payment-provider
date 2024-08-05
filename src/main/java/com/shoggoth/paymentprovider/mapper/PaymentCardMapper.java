package com.shoggoth.paymentprovider.mapper;

import com.shoggoth.paymentprovider.dto.PaymentCardRequest;
import com.shoggoth.paymentprovider.dto.PaymentCardResponse;
import com.shoggoth.paymentprovider.entity.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "number", source = "cardNumber")
    @Mapping(source = "expirationDate", target = "expirationDate", qualifiedByName = "yearMonthToLocalDate")
    @Mapping(target = "bankAccountId", ignore = true)
    @Mapping(target = "bankAccount", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "owner", ignore = true)
    PaymentCard requestToPaymentCard(PaymentCardRequest dto);

    @Mapping(target = "cardNumber", source = "number")
    PaymentCardResponse paymentCardToResponse(PaymentCard paymentCard);

    @Named("yearMonthToLocalDate")
    static LocalDate yearMonthToLocalDate(YearMonth yearMonth) {
        return Objects.isNull(yearMonth) ? null: yearMonth.atEndOfMonth();
    }
}
