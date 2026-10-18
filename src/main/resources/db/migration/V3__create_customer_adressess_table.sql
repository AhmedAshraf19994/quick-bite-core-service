
CREATE TABLE customer_addresses (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL ,
    label Text NOT NULL,
    country Text NOT NULL,
    city Text NOT NULL,
    street Text NOT NULL,
    building Text NOT NULL,
    apartment_number TEXT NOT NULL,
    type VARCHAR(30) CHECK(type IN ('home', 'work', 'public_place')),
    is_default boolean NOT NULL ,
    lng Decimal NOT NULL,
    lat Decimal not NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_customer_addresses_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_customer_addresses_user_id ON customer_addresses(user_id);