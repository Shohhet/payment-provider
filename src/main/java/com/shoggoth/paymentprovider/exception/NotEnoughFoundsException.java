package com.shoggoth.paymentprovider.exception;

public class NotEnoughFoundsException extends RestException{
    public NotEnoughFoundsException(String message) {
        super(message);
    }
}
