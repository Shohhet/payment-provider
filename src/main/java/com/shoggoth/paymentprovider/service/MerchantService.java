package com.shoggoth.paymentprovider.service;

import com.shoggoth.paymentprovider.entity.Merchant;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MerchantService {
    Mono<Merchant> getMerchantById(UUID id);
    Mono<UUID> getAuthenticatedMerchantId();
}
