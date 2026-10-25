CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE branches (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
restaurant_id BIGINT NOT NULL,
country_code TEXT NOT NULL CHECK (country_code IN ('egy','ksa')),
label TEXT NOT NULL,
address_text TEXT NOT NULL,
is_active BOOLEAN NOT NULL,
lat DECIMAL(10, 6) NOT NULL CHECK (lat BETWEEN -90 AND 90),
lng DECIMAL(10, 6) NOT NULL CHECK (lng BETWEEN -180 AND 180),
delivery_radius SMALLINT NOT NULL CHECK (delivery_radius > 0),
location geography(Point, 4326) GENERATED ALWAYS AS ( ST_MakePoint(lng::float, lat::float)::geography) STORED,
opens_at TIME NOT NULL,
closes_at TIME NOT NULL,
accept_orders BOOLEAN NOT NULL,
currency TEXT NOT NULL CHECK(currency IN ('egp','sar')),
commission SMALLINT NOT NULL CHECK (commission BETWEEN 0 AND 100),
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL,

CONSTRAINT fk_branches_restaurant_id FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

CREATE INDEX idx_branches_restaurant_id ON branches(restaurant_id);
CREATE INDEX idx_branches_is_active ON branches(is_active);
CREATE INDEX idx_branches_location ON branches USING GIST(location);