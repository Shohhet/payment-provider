ALTER TABLE webhook
    DROP CONSTRAINT webhook_pk,
    DROP CONSTRAINT webhook_transaction_fk;

ALTER TABLE transaction
    DROP CONSTRAINT transaction_pk,
    DROP CONSTRAINT transaction_card_fk,
    DROP CONSTRAINT transaction_merchant_fk;

ALTER TABLE card
    DROP CONSTRAINT card_pk,
    DROP CONSTRAINT card_bank_account_fk,
    DROP CONSTRAINT card_customer_fk;

ALTER TABLE customer
    DROP CONSTRAINT customer_pk;

ALTER TABLE merchant
    DROP CONSTRAINT merchant_pk,
    DROP CONSTRAINT merchant_bank_account_fk;

ALTER TABLE bank_account
    DROP CONSTRAINT bank_account_pk;

TRUNCATE TABLE bank_account;
TRUNCATE TABLE customer;
truncate table card;
truncate table merchant;
truncate table transaction;
truncate table webhook;

ALTER TABLE bank_account
    ADD CONSTRAINT bank_account_pk PRIMARY KEY (id);

ALTER TABLE merchant
    ADD CONSTRAINT merchant_pk PRIMARY KEY (id),
    ADD CONSTRAINT merchant_bank_account_fk FOREIGN KEY (bank_account_id) REFERENCES bank_account (id);

ALTER TABLE customer
    ADD CONSTRAINT customer_pk PRIMARY KEY (id);

ALTER TABLE card
    ADD CONSTRAINT card_pk PRIMARY KEY (id),
    ADD CONSTRAINT card_bank_account_fk FOREIGN KEY (bank_account_id) REFERENCES bank_account (id),
    ADD CONSTRAINT card_customer_fk FOREIGN KEY (owner_id) REFERENCES customer (id);

ALTER  TABLE transaction
    ADD CONSTRAINT transaction_pk PRIMARY KEY (id),
    ADD CONSTRAINT transaction_card_fk FOREIGN KEY (card_id) REFERENCES card (id),
    ADD CONSTRAINT transaction_merchant_fk FOREIGN KEY (merchant_id) REFERENCES merchant (id);

ALTER TABLE webhook
    ADD CONSTRAINT webhook_pk PRIMARY KEY (id),
    ADD CONSTRAINT webhook_transaction_fk FOREIGN KEY (transaction_id) REFERENCES transaction (id);




