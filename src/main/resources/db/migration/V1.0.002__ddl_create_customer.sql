CREATE TABLE customer
(
    id           UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    first_name   VARCHAR(50) NOT NULL,
    last_name    VARCHAR(50) NOT NULL,
    country_code VARCHAR(2)  NOT NULL
);
