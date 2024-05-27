package com.shoggoth.paymentprovider.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "transaction")
public class Transaction {
    @Id
    private UUID id;
    @Column("amount")
    private BigDecimal amount;
    @Column("currency")
    private String currency;
    @Column("payment_method")
    private PaymentMethod paymentMethod;
    @Column("created_at")
    private Date createdAt;
    @Column("updated_at")
    private Date updatedAt;
    @Column("type")
    private TransactionType type;
    @Column("language_code")
    private String languageCode;
    @Column("status")
    private TransactionStatus status;
    @Column("message")
    private String message;
    @Column("card_id")
    private UUID paymentCardId;
    @Transient
    private PaymentCard card;
    @Column("merchant_id")
    private UUID merchantId;
    @Transient
    private Merchant merchant;
}
