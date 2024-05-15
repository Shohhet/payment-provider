CREATE TABLE customer_card
(
    customer_id UUID,
    card_id UUID,
    CONSTRAINT customer_card_pk PRIMARY KEY (customer_id, card_id)
);
