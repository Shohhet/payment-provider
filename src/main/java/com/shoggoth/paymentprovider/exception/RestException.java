package com.shoggoth.paymentprovider.exception;

public class RestException extends RuntimeException {
    protected
    RestException(String message) {
        super(message);
    }
}
