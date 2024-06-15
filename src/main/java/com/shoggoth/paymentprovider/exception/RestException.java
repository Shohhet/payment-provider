package com.shoggoth.paymentprovider.exception;

public class RestException extends RuntimeException {

    RestException(String message) {
        super(message);
    }
}
