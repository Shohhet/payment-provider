package com.shoggoth.paymentprovider.service;

import com.shoggoth.paymentprovider.entity.Transaction;

public interface WebHookService {
    void sendWebHook(Transaction transaction);
}
