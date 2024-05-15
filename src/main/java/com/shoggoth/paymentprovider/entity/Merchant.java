package com.shoggoth.paymentprovider.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Merchant {
   @Id
   private UUID id;
   @ToString.Exclude
   private String secretKey;
   private BigDecimal balance;
   private Currency currency;
}


