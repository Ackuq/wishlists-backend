CREATE TABLE users (
    uuid UUID NOT NULL PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(60) NOT NULL,
    role INT DEFAULT 1 NOT NULL
);

CREATE TABLE wish_lists (
    id SERIAL PRIMARY KEY,
    owner_id UUID NOT NULL,
    title VARCHAR(1000) NOT NULL,
    description TEXT NULL,
    CONSTRAINT fk_wish_lists_owner_id_uuid FOREIGN KEY (owner_id) REFERENCES users(uuid) ON DELETE CASCADE ON UPDATE RESTRICT
);

CREATE TABLE wish_list_products (
    id SERIAL PRIMARY KEY,
    title VARCHAR(1000) NOT NULL,
    description TEXT NULL,
    link TEXT NULL,
    claimed_by UUID NULL,
    wish_list INT NOT NULL,
    CONSTRAINT fk_wish_list_products_claimed_by_uuid FOREIGN KEY (claimed_by) REFERENCES users(uuid) ON DELETE SET NULL ON UPDATE RESTRICT,
    CONSTRAINT fk_wish_list_products_wish_list_id FOREIGN KEY (wish_list) REFERENCES wish_lists(id) ON DELETE CASCADE ON UPDATE RESTRICT
);

CREATE TABLE users_wish_lists (
    user_id UUID,
    wish_list_id INT,
    CONSTRAINT pk_users_wish_lists PRIMARY KEY (user_id, wish_list_id),
    CONSTRAINT fk_users_wish_lists_wish_list_id_id FOREIGN KEY (wish_list_id) REFERENCES wish_lists(id) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT fk_users_wish_lists_user_id_uuid FOREIGN KEY (user_id) REFERENCES users(uuid) ON DELETE CASCADE ON UPDATE RESTRICT
);