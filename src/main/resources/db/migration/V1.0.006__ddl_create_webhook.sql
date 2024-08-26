CREATE TABLE webhook
(
    id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    transaction_id  UUID        NOT NULL,
    request_body    JSONB       NOT NULL,
    response_status NUMERIC(3)  NOT NULL,
    response_body   JSONB       NOT NULL,
    attempt_number  INTEGER     NOT NULL,
    created_at      TIMESTAMP   NOT NULL,
    CONSTRAINT webhook_transaction_fk FOREIGN KEY (transaction_id) REFERENCES transaction (id)
);