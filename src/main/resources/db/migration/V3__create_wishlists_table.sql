CREATE TABLE wish_lists (
    id SERIAL PRIMARY KEY,
    owner_id UUID REFERENCES users
)