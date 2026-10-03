CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email Text UNIQUE NOT NULL,
    name Text NOT NULL,
    phone TEXT NOT NULL,
    password Text NOT NULL,
    role Text NOT NULL CHECK(role IN ('user', 'system_admin','delivery_agent', 'restaurant_user')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP ,
    deleted_at TIMESTAMP
)
