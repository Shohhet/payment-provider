package com.shoggoth.paymentprovider.service.impl;

import com.shoggoth.paymentprovider.entity.Merchant;
import com.shoggoth.paymentprovider.repository.BankAccountRepository;
import com.shoggoth.paymentprovider.repository.MerchantRepository;
import com.shoggoth.paymentprovider.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;
    private final BankAccountRepository bankAccountRepository;

    @Override
    public Mono<Merchant> getAuthenticatedMerchant() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .map(UUID::fromString)
                .flatMap(merchantRepository::findById);
    }
}
