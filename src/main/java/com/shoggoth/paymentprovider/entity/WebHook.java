package com.shoggoth.paymentprovider.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.shoggoth.paymentprovider.mapper.PgJsonObjectDeserializer;
import com.shoggoth.paymentprovider.mapper.PgJsonObjectSerializer;
import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
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

    @Column("request_body")
    @JsonSerialize(using = PgJsonObjectSerializer.class)
    @JsonDeserialize(using = PgJsonObjectDeserializer.class)
    private Json requestBody;

    @Column("response_status")
    @JsonSerialize(using = PgJsonObjectSerializer.class)
    @JsonDeserialize(using = PgJsonObjectDeserializer.class)
    private Integer responseStatus;

    @Column("response_body")
    @JsonSerialize(using = PgJsonObjectSerializer.class)
    @JsonDeserialize(using = PgJsonObjectDeserializer.class)
    private Json responseBody;

    @Column("attempt_number")
    private Integer attemptNumber;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Transient
    private Transaction transaction;
}
