package com.shoggoth.paymentprovider.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "webhook_delivery")
public class WebHookDeliveryEvent {
    @Id
    private UUID id;
    @Column
    private UUID webhookId;
    @Column("response_status")
    private String response_status;
    @Column("response_payload")
    private String response_payload;
    @Column("created_at")
    private Date createdAt;
}
