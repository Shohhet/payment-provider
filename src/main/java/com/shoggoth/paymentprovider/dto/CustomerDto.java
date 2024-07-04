package com.shoggoth.paymentprovider.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.shoggoth.paymentprovider.validation.Alfa2CountryCode;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CustomerDto(
        @NotBlank
        @Size(min = 2, max = 50)
        @Pattern(regexp = "^[A-Za-z]*$")
        String firstName,

        @NotBlank
        @Size(min = 2, max = 50)
        @Pattern(regexp = "^[A-Za-z]*$")
        String lastName,


        @NotNull
        @NotBlank
        @Size(min = 2, max = 2)
        @Alfa2CountryCode
        String country

) {
}
