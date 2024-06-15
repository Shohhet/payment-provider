package com.shoggoth.paymentprovider.exception;

public class AuthenticationException extends RestException {
    public AuthenticationException(String message) {
        super(message);
    }
}
