package com.shoggoth.paymentprovider.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "merchant")
public class Merchant {
    @Id
    private UUID id;
    @ToString.Exclude
    @Column("secret_key")
    private String secretKey;
    @Transient
    private List<BankAccount> bankAccounts;
}


