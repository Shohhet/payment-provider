CREATE TABLE webhook_delivery
(
    id               UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    webhook_id       UUID       NOT NULL,
    response_status  VARCHAR(3) NOT NULL,
    response_payload TEXT,
    created_at       TIMESTAMP  NOT NULL,
    CONSTRAINT delivery_webhook_fk FOREIGN KEY (webhook_id) REFERENCES webhook (id)
);
