package com.shoggoth.paymentprovider.repository;

import com.shoggoth.paymentprovider.entity.WebHookDeliveryEvent;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface WebHookDeliveryEventRepository extends R2dbcRepository<WebHookDeliveryEvent, UUID> {
}
