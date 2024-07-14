package com.shoggoth.paymentprovider.configuration;

import com.shoggoth.paymentprovider.exception.*;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Configuration
@EnableWebFlux
@EnableWebFluxSecurity
public class AppConfiguration {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().authenticated())
                .httpBasic(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean
    @Order(-2)
    public ReactiveExceptionHandler reactiveExceptionHandler(WebProperties webProperties,
                                                             ApplicationContext applicationContext,
                                                             ServerCodecConfigurer serverCodecConfigurer) {
        var defaultErrorAttributes = new DefaultErrorAttributes();
        var reactiveExceptionHandler = new ReactiveExceptionHandler(defaultErrorAttributes,
                webProperties.getResources(), applicationContext, HttpStatus.INTERNAL_SERVER_ERROR, restExceptionToHttpStatus());
        reactiveExceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
        reactiveExceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());
        return reactiveExceptionHandler;
    }

    @Bean
    public Map<Class<? extends RestException>, HttpStatus> restExceptionToHttpStatus() {
        return Map.of(
                AuthenticationException.class, HttpStatus.UNAUTHORIZED,
                NotFoundException.class, HttpStatus.NOT_FOUND,
                NotEnoughFoundsException.class, HttpStatus.BAD_REQUEST,
                TransactionDataException.class, HttpStatus.BAD_REQUEST
        );
    }

}
