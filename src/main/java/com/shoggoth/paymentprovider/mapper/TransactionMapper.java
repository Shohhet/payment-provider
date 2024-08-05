package com.shoggoth.paymentprovider.mapper;

import com.shoggoth.paymentprovider.dto.*;
import com.shoggoth.paymentprovider.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PaymentCardMapper.class, CustomerMapper.class, PaymentMethodMapper.class})
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "languageCode", source = "language")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "message", ignore = true)
    @Mapping(target = "paymentCardId", ignore = true)
    @Mapping(target = "card", source = "cardData")
    @Mapping(target = "merchantId", ignore = true)
    @Mapping(target = "merchant", ignore = true)
    Transaction createRequestToTransaction(CreateTransactionRequest createTransactionRequest);

    @Mapping(target = "transactionId", source = "id")
    CreateTransactionResponse transactionToCreateResponse(Transaction transaction);


    @Mapping(target = "transactionId", source = "id")
    @Mapping(target = "cardData", source = "card")
    @Mapping(target = "language", source = "languageCode")
    GetTransactionResponse transactionToGetResponse(Transaction transaction);
}
