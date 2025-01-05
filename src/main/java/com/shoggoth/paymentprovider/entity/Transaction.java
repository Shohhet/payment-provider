package com.shoggoth.paymentprovider.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table(name = "transaction")
public class Transaction {
    @Id
    private UUID id;

    private BigDecimal amount;

    private String currency;

    @Column("payment_method")
    private PaymentMethod paymentMethod;

    @Column("created_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @Column("updated_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedAt;

    private TransactionType type;

    @Column("language_code")
    private String languageCode;

    @Column("notification_url")
    private String notificationUrl;

    private TransactionStatus status;

    private String message;

    @Column("card_id")
    private UUID paymentCardId;

    @Transient
    private PaymentCard card;

    @Column("merchant_id")
    private UUID merchantId;

    @Transient
    private Merchant merchant;

    @Transient
    private Customer customer;
}
