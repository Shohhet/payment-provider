package com.shoggoth.paymentprovider.exception;

public class NotFoundException extends RestException{
    public NotFoundException(String message) {
        super(message);
    }
}
