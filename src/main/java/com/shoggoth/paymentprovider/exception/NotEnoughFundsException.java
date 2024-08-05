package com.shoggoth.paymentprovider.exception;

public class NotEnoughFundsException extends RestException{
    public NotEnoughFundsException(String message, String errorCode) {
        super(message, errorCode);
    }
}
