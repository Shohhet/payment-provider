CREATE TABLE bank_account
(
    id            UUID DEFAULT gen_random_uuid(),
    balance       DECIMAL(100, 2) NOT NULL,
    currency_code VARCHAR(3)      NOT NULL,
    CONSTRAINT bank_account_pk PRIMARY KEY (id)
);