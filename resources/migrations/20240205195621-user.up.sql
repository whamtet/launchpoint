--;;
create table user
(
    email      text unique,
    first_name text,
    last_name  text,
    password   text
);
