CREATE TABLE permissions (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    resource TEXT NOT NULL,
    action Text NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_resource_action UNIQUE (resource, action)
    );

CREATE TABLE roles (
    id int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT uq_name UNIQUE (name)
);

CREATE TABLE roles_permissions (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    CONSTRAINT fk_roles_permissions_role_id FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_roles_permissions_permission_id FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE ,
    CONSTRAINT pk_roles_permissions PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE restaurant_members (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
    user_id BIGINT NOT NULL,
    restaurant_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_At TIMESTAMP ,
    CONSTRAINT fk_restaurant_members_user_id FOREIGN KEY (user_id) REFERENCES  users(id) ON DELETE CASCADE ,
    CONSTRAINT fk_restaurant_members_restaurant_id FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE ,
    CONSTRAINT fk_restaurant_members_role_id FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE ,
    CONSTRAINT uq_restaurant_member UNIQUE (restaurant_id, user_id)
);

CREATE TABLE member_branches (
    member_id BIGINT NOT NULL ,
    branch_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_member_branches_member_id FOREIGN KEY (member_id) REFERENCES restaurant_members(id),
    CONSTRAINT fk_member_branches_branch_id FOREIGN KEY (branch_id) REFERENCES branches(id),
    CONSTRAINT pk_member_branches PRIMARY KEY (branch_id, member_id)
);