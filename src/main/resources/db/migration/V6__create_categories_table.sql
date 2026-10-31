
CREATE TABLE categories (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
    name TEXT NOT NULL,
    restaurant_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_At TIMESTAMP,
    CONSTRAINT fk_categories_restaurant_id FOREIGN KEY (restaurant_id) REFERENCES restaurants(id),
    CONSTRAINT uq_categories_restaurant_name UNIQUE (name,restaurant_id)
);

CREATE INDEX idx_categories_restaurant_id ON categories (restaurant_id);
CREATE INDEX idc_categories_restaurant_id_name ON categories (restaurant_id,name);