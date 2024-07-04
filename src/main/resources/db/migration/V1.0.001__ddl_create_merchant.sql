CREATE TABLE merchant
(
    id            UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    secret_key    VARCHAR(50)     NOT NULL,
    balance       DECIMAL(100, 2) NOT NULL,
    currency_code VARCHAR(3)      NOT NULL
);