CREATE TABLE merchant_bank_account
(
    merchant_id     UUID,
    bank_account_id UUID,
    CONSTRAINT merchant_bank_account_pk PRIMARY KEY (merchant_id, bank_account_id),
    CONSTRAINT merchant_fk FOREIGN KEY (merchant_id) REFERENCES merchant (id),
    CONSTRAINT bank_account_fk FOREIGN KEY (bank_account_id) REFERENCES bank_account (id)
);