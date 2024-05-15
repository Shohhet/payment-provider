CREATE TABLE card
(
    id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    number          VARCHAR(19) UNIQUE NOT NULL,
    expiration_date DATE               NOT NULL,
    cvv             VARCHAR(3)         NOT NULL,
    bank_account_id UUID,
    CONSTRAINT card_bank_account_fk FOREIGN KEY (bank_account_id) REFERENCES bank_account (id)
);