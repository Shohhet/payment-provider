package com.shoggoth.paymentprovider.service;

import com.shoggoth.paymentprovider.entity.Merchant;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface MerchantService {
    Mono<Merchant> getAuthenticatedMerchant();
    Mono<Merchant> reserveFounds(Merchant merchant, BigDecimal amount);
}
