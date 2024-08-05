CREATE TABLE merchant
(
    id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    secret_key      VARCHAR(50) NOT NULL,
    bank_account_id UUID,
    CONSTRAINT merchant_bank_account_fk FOREIGN KEY (bank_account_id) REFERENCES bank_account (id)
);