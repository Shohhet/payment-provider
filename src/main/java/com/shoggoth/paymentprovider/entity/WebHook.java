package com.shoggoth.paymentprovider.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "webhook")
public class WebHook {
    @Id
    private UUID id;
    @Column("transaction_id")
    private UUID transactionId;
    @Transient
    private Transaction transaction;
    @Transient
    private List<WebHookDeliveryEvent> events;
}
