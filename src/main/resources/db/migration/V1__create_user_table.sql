CREATE TABLE users (
    uuid UUID primary key,
    email varchar(100) unique,
    password_hash varchar(60)
)