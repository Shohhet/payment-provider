package com.shoggoth.paymentprovider.exception;

public class NotFoundException extends RestException {
    public NotFoundException(String message, String errorCode) {
        super(message, errorCode);
    }
}
