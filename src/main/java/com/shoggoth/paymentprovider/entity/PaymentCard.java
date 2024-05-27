package com.shoggoth.paymentprovider.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "card")
public class PaymentCard {
    @Id
    private UUID id;
    @Column("number")
    private String number;
    @Column("expiration_date")
    private Date expirationDate;
    @Column("cvv")
    private String cvv;
    @Column("bank_account_Id")
    private UUID bankAccountId;
    @Transient
    private BankAccount bankAccount;
}
