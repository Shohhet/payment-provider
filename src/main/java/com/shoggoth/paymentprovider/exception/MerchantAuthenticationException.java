package com.shoggoth.paymentprovider.exception;

public class MerchantAuthenticationException extends RestException {
    public MerchantAuthenticationException(String message, String errorCode) {
        super(message, errorCode);
    }
}
