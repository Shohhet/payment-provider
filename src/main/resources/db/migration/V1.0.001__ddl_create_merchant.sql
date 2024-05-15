CREATE TABLE merchant
(
    id         UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    secret_key VARCHAR(50) NOT NULL
);