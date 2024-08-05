package com.shoggoth.paymentprovider.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table(name = "merchant")
public class Merchant {
    @Id
    private UUID id;

    @ToString.Exclude
    @Column("secret_key")
    private String secretKey;

    @Column("bank_account_id")
    private UUID bankAccountId;

    @Transient
    private BankAccount bankAccount;
}


