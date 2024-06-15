package com.shoggoth.paymentprovider.service;

import com.shoggoth.paymentprovider.entity.Merchant;
import reactor.core.publisher.Mono;

public interface MerchantService {
    Mono<Merchant> getAuthenticatedMerchant();
}
