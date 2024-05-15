CREATE TABLE webhook
(
    id               UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    notification_url VARCHAR(200) NOT NULL,
    transaction_id   UUID,
    CONSTRAINT webhook_transaction_fk FOREIGN KEY (transaction_id) REFERENCES transaction (id)
);