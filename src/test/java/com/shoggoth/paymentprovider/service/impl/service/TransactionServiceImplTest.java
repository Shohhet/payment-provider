package com.shoggoth.paymentprovider.service.impl.service;

import com.shoggoth.paymentprovider.entity.TransactionType;
import com.shoggoth.paymentprovider.mapper.TransactionMapper;
import com.shoggoth.paymentprovider.mapper.TransactionMapperImpl;
import com.shoggoth.paymentprovider.repository.TransactionRepository;
import com.shoggoth.paymentprovider.service.MerchantService;
import com.shoggoth.paymentprovider.service.PaymentCardService;
import com.shoggoth.paymentprovider.service.ProcessingService;
import com.shoggoth.paymentprovider.service.WebHookService;
import com.shoggoth.paymentprovider.service.impl.util.TestDataUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.shoggoth.paymentprovider.service.impl.util.TestDataUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {
    @Mock
    private PaymentCardService paymentCardService;
    @Spy
    private TransactionMapper transactionMapper = new TransactionMapperImpl();
    @Mock
    private MerchantService merchantService;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private ProcessingService processingService;
    @Mock
    private TransactionalOperator transactionalOperator;
    @Mock
    private WebHookService webHookService;

    void givenTopUpTransactionRequest_whenCurrencyValid_ThenReturnNewTransaction() {
        //given
        when(merchantService.getAuthenticatedMerchantId()).thenReturn(Mono.just(MERCHANT_ID));
        when(merchantService.getMerchantById(any(UUID.class))).thenReturn(Mono.just(getMerchant()));

        when(paymentCardService.getOrCreatePaymentCard(getCreateTransactionRequest(), TransactionType.TOP_UP)).thenReturn(Mono.just(getPersistedPaymentCard()));

        //when
        //then
    }
}