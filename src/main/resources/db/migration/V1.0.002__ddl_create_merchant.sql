CREATE TABLE merchant
(
    id              UUID DEFAULT gen_random_uuid(),
    secret_key      VARCHAR(50) NOT NULL,
    bank_account_id UUID,
    CONSTRAINT merchant_pk PRIMARY KEY (id),
    CONSTRAINT merchant_bank_account_fk FOREIGN KEY (bank_account_id) REFERENCES bank_account (id)
);