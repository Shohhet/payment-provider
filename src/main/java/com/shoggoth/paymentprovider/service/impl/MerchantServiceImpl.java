package com.shoggoth.paymentprovider.service.impl;

import com.shoggoth.paymentprovider.entity.Merchant;
import com.shoggoth.paymentprovider.exception.NotEnoughFoundsException;
import com.shoggoth.paymentprovider.repository.BankAccountRepository;
import com.shoggoth.paymentprovider.repository.MerchantRepository;
import com.shoggoth.paymentprovider.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;

    @Override
    public Mono<Merchant> getAuthenticatedMerchant() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .map(UUID::fromString)
                .flatMap(merchantRepository::findById);
    }

    @Override
    public Mono<Merchant> reserveFounds(Merchant merchant, BigDecimal amount) {
        return Mono.just(merchant)
                .flatMap(merchant1 -> {
                            var currentBalance = merchant1.getBalance();
                            if (currentBalance.compareTo(amount) < 0) {
                                return Mono.error(new NotEnoughFoundsException("Not enough founds for pay out."));
                            } else {
                                merchant1.setBalance(currentBalance.subtract(amount));
                                return merchantRepository.save(merchant1);
                            }
                        }
                );
    }
}
