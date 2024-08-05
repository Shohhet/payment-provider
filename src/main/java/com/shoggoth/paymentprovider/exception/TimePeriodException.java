package com.shoggoth.paymentprovider.exception;

public class TimePeriodException extends RestException{
    public TimePeriodException(String message, String errorCode) {
        super(message, errorCode);
    }
}
