CREATE TABLE branch_product_details (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
    product_id BIGINT NOT NULL,
    branch_id BIGINT NOT NULL,
    price INT NOT NULL,
    stock INT NOT NULL,
    is_available BOOLEAN NOT NULL default false,
    CONSTRAINT fk_bpd_product_id FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_bpd_branch_id FOREIGN KEY (branch_id) REFERENCES branches(id),
    CONSTRAINT uq_bpd_product UNIQUE (branch_id, product_id)
);

