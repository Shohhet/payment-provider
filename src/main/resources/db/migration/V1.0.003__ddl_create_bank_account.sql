CREATE TABLE bank_account
(
    id            UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    balance       DECIMAL(100, 2) NOT NULL,
    currency_code VARCHAR(3)      NOT NULL
);