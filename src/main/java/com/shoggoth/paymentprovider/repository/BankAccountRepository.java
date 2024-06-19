package com.shoggoth.paymentprovider.repository;

import com.shoggoth.paymentprovider.entity.BankAccount;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface BankAccountRepository extends R2dbcRepository<BankAccount, UUID> {
    @Query("""
            SELECT id, balance, currency_code
            FROM bank_account INNER JOIN merchant_bank_account ON bank_account.id = merchant_bank_account.bank_account_id
            WHERE merchant_id = :merchantId;
            """)
    Flux<BankAccount> findBankAccountsByMerchantId(UUID merchantId);
}
