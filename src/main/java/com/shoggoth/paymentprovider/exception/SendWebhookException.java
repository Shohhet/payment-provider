package com.shoggoth.paymentprovider.exception;

public class SendWebhookException extends RestException{
    public SendWebhookException(String message, String errorCode) {
        super(message, errorCode);
    }
}
