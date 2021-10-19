CREATE TABLE Users (
    uuid UUID primary key,
    email varchar(100) unique,
    password varchar(100)
)