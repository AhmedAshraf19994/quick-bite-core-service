CREATE TABLE products (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    image_url TEXT,
    restaurant_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_products_restaurant_id FOREIGN KEY (restaurant_id) REFERENCES restaurants (id),
    CONSTRAINT fk_products_category_id FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE INDEX idx_products_restaurant_id ON products(restaurant_id);
