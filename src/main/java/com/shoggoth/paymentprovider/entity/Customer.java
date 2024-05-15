package com.shoggoth.paymentprovider.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Locale;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    @Id
    private UUID id;
    private String firstName;
    private String lastName;
    private Locale locale;
}
