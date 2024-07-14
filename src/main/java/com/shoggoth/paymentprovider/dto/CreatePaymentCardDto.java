package com.shoggoth.paymentprovider.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;
import com.shoggoth.paymentprovider.validation.CardExpirationDate;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.LuhnCheck;

import java.time.YearMonth;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CreatePaymentCardDto(
        @NotNull
        @NotEmpty
        @NotBlank
        @LuhnCheck()
        String cardNumber,

        @JsonProperty("exp_date")
        @JsonSerialize(using = YearMonthSerializer.class)
        @JsonDeserialize(using = YearMonthDeserializer.class)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/yy")
        @NotNull()
        @CardExpirationDate()
        YearMonth expirationDate,

        @NotNull
        @Size(min = 3, max = 3)
        @Pattern(regexp = "^[0-9]*$")
        String cvv
) {
}
