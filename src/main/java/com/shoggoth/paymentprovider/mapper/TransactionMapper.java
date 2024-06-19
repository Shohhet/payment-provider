package com.shoggoth.paymentprovider.mapper;

import com.shoggoth.paymentprovider.dto.CreateTopUpTransactionRequestDto;
import com.shoggoth.paymentprovider.dto.CreateTopUpTransactionResponseDto;
import com.shoggoth.paymentprovider.dto.GetTopUpTransactionDto;
import com.shoggoth.paymentprovider.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PaymentCardMapper.class, CustomerMapper.class})
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
    Transaction createRequestDtoToTransaction(CreateTopUpTransactionRequestDto transactionDto);

    @Mapping(target = "topUpId", source = "id")
    CreateTopUpTransactionResponseDto transactionToCreateResponseDto(Transaction transaction);

    @Mapping(target = "topUpId", source = "id")
    @Mapping(target = "cardData", source = "card")
    @Mapping(target = "language", source = "languageCode")
    GetTopUpTransactionDto transactionToGetDto(Transaction transaction);

}
