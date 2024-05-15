CREATE TABLE transaction
(
    id               UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    amount           DECIMAL(100, 2) NOT NULL,
    currency         VARCHAR(3) NOT NULL,
    payment_method   VARCHAR(15) NOT NULL,
    created_at       DATE NOT NULL,
    updated_at       DATE NOT NULL,
    type             VARCHAR(15) NOT NULL,
    language_code    VARCHAR(2) NOT NULL,
    status           VARCHAR(15) NOT NULL,
    message          VARCHAR(200) NOT NULL,
    card_id          UUID,
    merchant_id      UUID,
    CONSTRAINT transaction_card_fk FOREIGN KEY (card_id) REFERENCES card (id),
    CONSTRAINT transaction_merchant_fk FOREIGN KEY (merchant_id) REFERENCES merchant (id)
);