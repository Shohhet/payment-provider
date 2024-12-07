package com.shoggoth.paymentprovider.service.impl.service;

import com.shoggoth.paymentprovider.entity.Transaction;
import com.shoggoth.paymentprovider.exception.TransactionDataException;
import com.shoggoth.paymentprovider.mapper.*;
import com.shoggoth.paymentprovider.repository.TransactionRepository;
import com.shoggoth.paymentprovider.service.*;
import com.shoggoth.paymentprovider.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static com.shoggoth.paymentprovider.entity.TransactionStatus.IN_PROGRESS;
import static com.shoggoth.paymentprovider.entity.TransactionType.TOP_UP;
import static com.shoggoth.paymentprovider.service.impl.util.TestDataUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {
    @InjectMocks
    private TransactionServiceImpl transactionService;
    @Mock
    private PaymentCardService paymentCardService;
    @Spy
    private final TransactionMapper transactionMapper = new TransactionMapperImpl(
            new PaymentCardMapperImpl(),
            new CustomerMapperImpl()
    );

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

    @Test
    void givenTopUpTransactionRequest_whenCurrencyValid_ThenReturnNewTransaction() {
        //given
        given(merchantService.getAuthenticatedMerchantId()).willReturn(Mono.just(MERCHANT_ID));
        given(paymentCardService.getOrCreatePaymentCard(getCreateTransactionRequest(), TOP_UP)).willReturn(Mono.just(getPersistedPaymentCard()));
        given(transactionRepository.save(any(Transaction.class))).willAnswer(args -> {
                    Transaction transaction = args.getArgument(0);
                    return Mono.just(transaction.toBuilder()
                            .id(TRANSACTION_ID)
                            .build());
                }
        );
        given(paymentCardService.getPaymentCardById(any(UUID.class))).willReturn(Mono.just(getPersistedPaymentCard()));
        given(merchantService.getMerchantById(any(UUID.class))).willReturn(Mono.just(getMerchant()));
        given(processingService.process(any(Transaction.class))).willAnswer(args -> Mono.just(args.getArgument(0)));
        given(transactionalOperator.transactional(any(Mono.class))).willAnswer(args -> (args.getArgument(0)));

        //when
        StepVerifier.create(transactionService.createTransaction(TOP_UP, getCreateTransactionRequest()))
                //then
                .consumeNextWith(result -> {
                            assertEquals(result.transactionId(), TRANSACTION_ID);
                            assertEquals(result.status(), IN_PROGRESS);
                            assertEquals(result.message(), IN_PROGRESS.getMessage());
                        }
                )
                .verifyComplete();
    }

    @Test
    void givenTopUpTransactionRequest_whenCurrencyNotValid_ThenReturnError() {
        //given
        given(merchantService.getAuthenticatedMerchantId()).willReturn(Mono.just(MERCHANT_ID));
        given(merchantService.getMerchantById(any(UUID.class))).willReturn(Mono.just(getMerchant()));
        given(paymentCardService.getOrCreatePaymentCard(getCreateTransactionRequestWithWrongCurrency(), TOP_UP)).willReturn(Mono.just(getPersistedPaymentCard()));
        given(transactionalOperator.transactional(any(Mono.class))).willAnswer(args -> (args.getArgument(0)));

        //when
        StepVerifier.create(transactionService.createTransaction(TOP_UP, getCreateTransactionRequestWithWrongCurrency()))

                //then
                .expectErrorMatches(throwable -> throwable instanceof TransactionDataException
                                                 && throwable.getMessage().equals("Wrong transaction currency.")
                                                 && ((TransactionDataException) throwable).getErrorCode().equals("TRANSACTION_DATA_ERROR")
                )
                .verify();
    }

}