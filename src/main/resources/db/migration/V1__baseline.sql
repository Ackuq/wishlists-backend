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
    title varchar(1000) NOT NULL,
    description TEXT,
    owner_id UUID NOT NULL REFERENCES users ON DELETE CASCADE
);

CREATE TABLE wish_list_product (
    id SERIAL PRIMARY KEY,
    title varchar(1000) NOT NULL,
    description TEXT,
    link TEXT,
    claimed_by UUID REFERENCES users (uuid) ON DELETE SET NULL,
    wish_list INT REFERENCES wish_lists (id) ON DELETE CASCADE
);

CREATE TABLE users_wish_lists (
    user_id UUID REFERENCES users (uuid) ON DELETE CASCADE ON UPDATE CASCADE,
    wish_list_id INT REFERENCES wish_lists (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT pk_users_wish_lists PRIMARY KEY (user_id, wish_list_id)
);