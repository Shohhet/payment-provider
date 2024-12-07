package com.shoggoth.paymentprovider.entity;

import lombok.Getter;

@Getter
public enum TransactionStatus {
    IN_PROGRESS("Transaction created."),
    APPROVED("Transaction approved."),
    FAILED("Transaction failed.");

    private final String message;

    TransactionStatus(String message) {
        this.message = message;
    }

}
