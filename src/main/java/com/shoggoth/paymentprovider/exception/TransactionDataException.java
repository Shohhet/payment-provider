package com.shoggoth.paymentprovider.exception;

public class TransactionDataException extends RestException{
    public TransactionDataException(String message, String errorCode) {
        super(message, errorCode);
    }
}
