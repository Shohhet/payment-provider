package com.shoggoth.paymentprovider.exception;


import lombok.Getter;

@Getter
public class RestException extends RuntimeException {
    protected String errorCode;

    public RestException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
