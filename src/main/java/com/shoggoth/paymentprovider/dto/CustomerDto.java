package com.shoggoth.paymentprovider.dto;

public record CustomerDto(
        String firstName,
        String lastName,
        String country
) {
}
