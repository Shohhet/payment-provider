package com.shoggoth.paymentprovider.repository;

import com.shoggoth.paymentprovider.entity.PaymentCard;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface PaymentCardRepository extends R2dbcRepository<PaymentCard, UUID> {
}
