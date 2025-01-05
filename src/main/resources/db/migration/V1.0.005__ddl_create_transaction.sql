CREATE TABLE transaction
(
    id               UUID DEFAULT gen_random_uuid(),
    amount           DECIMAL(100, 2) NOT NULL,
    currency         VARCHAR(3)      NOT NULL,
    payment_method   VARCHAR(15)     NOT NULL,
    created_at       TIMESTAMP       NOT NULL,
    updated_at       TIMESTAMP       NOT NULL,
    type             VARCHAR(15)     NOT NULL,
    language_code    VARCHAR(2)      NOT NULL,
    notification_url VARCHAR(2048)   NOT NULL,
    status           VARCHAR(15)     NOT NULL,
    message          VARCHAR(200)    NOT NULL,
    card_id          UUID NOT NULL,
    merchant_id      UUID NOT NULL,
    CONSTRAINT transaction_pk PRIMARY KEY (id),
    CONSTRAINT transaction_card_fk FOREIGN KEY (card_id) REFERENCES card (id),
    CONSTRAINT transaction_merchant_fk FOREIGN KEY (merchant_id) REFERENCES merchant (id)
);