package com.shoggoth.paymentprovider.repository;

import com.shoggoth.paymentprovider.entity.WebHook;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface WebHookRepository extends R2dbcRepository<WebHook, UUID> {
}
