package com.shoggoth.paymentprovider.security;

import com.shoggoth.paymentprovider.exception.AuthenticationException;
import com.shoggoth.paymentprovider.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReactiveMerchantUserDetailsService implements ReactiveUserDetailsService {

    private final MerchantRepository merchantRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return merchantRepository.findById(UUID.fromString(username))
                .switchIfEmpty(
                        Mono.error(new AuthenticationException("Merchant with ID# %s does not exist.".formatted(username)))
                )
                .map(MerchantUserDetails::new);
    }
}
