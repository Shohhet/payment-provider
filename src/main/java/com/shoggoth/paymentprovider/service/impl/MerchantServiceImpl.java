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
    public Mono<Merchant> getMerchantById(UUID id) {
        return merchantRepository.findById(id)
                .flatMap(this::setBankAccount);
    }

    @Override
    public Mono<UUID> getAuthenticatedMerchantId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .map(UUID::fromString);
    }

    private Mono<Merchant> setBankAccount(Merchant merchant) {
        return bankAccountRepository.findById(merchant.getBankAccountId())
                .map(bankAccount -> merchant.toBuilder()
                        .bankAccount(bankAccount)
                        .build()
                );
    }
}
