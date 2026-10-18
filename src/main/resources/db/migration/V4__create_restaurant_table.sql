CREATE TABLE restaurants (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name Text NOT NULL UNIQUE,
    owner_id BIGINT NOT NULL,
    logo_url Text NOT NULL,
    primary_country Text NOT NULL,
    status Text NOT NULL CHECK (status IN ('active','suspended','disabled','pending')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    status_updated_at TIMESTAMP,

    CONSTRAINT fk_restaurants_owner_id FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE INDEX idx_restaurants_status On restaurants(status);
CREATE INDEX idx_restaurants_primary_country On restaurants(primary_country);
CREATE INDEX idx_restaurants_created_at On restaurants(created_at);