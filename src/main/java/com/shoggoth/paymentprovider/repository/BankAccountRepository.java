package com.shoggoth.paymentprovider.repository;

import com.shoggoth.paymentprovider.entity.BankAccount;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BankAccountRepository extends R2dbcRepository<BankAccount, UUID> {
    @Query("""
                       SELECT  id, balance, currency_code
                       FROM bank_account
                       WHERE id = :id
                       FOR UPDATE;
            """)
    Mono<BankAccount> findByIdForUpdate(UUID id);
}
