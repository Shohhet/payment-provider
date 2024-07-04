package com.shoggoth.paymentprovider.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customer")
public class Customer {
    @Id
    private UUID id;
    @Column("first_name")
    private String firstName;
    @Column("last_name")
    private String lastName;
    @Column("country_code")
    private String countryCode;
    @Transient
    private List<PaymentCard> cards;
}
