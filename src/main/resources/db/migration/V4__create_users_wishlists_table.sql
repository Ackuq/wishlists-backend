CREATE TABLE users_wish_lists (
    user_id UUID REFERENCES users (uuid) ON DELETE CASCADE ON UPDATE CASCADE,
    wish_list_id INT REFERENCES wish_lists (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT pk_users_wish_lists PRIMARY KEY (user_id, wish_list_id)
)