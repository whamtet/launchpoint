--;;
create table user
(
    id         integer primary key asc,
    email      text unique,
    first_name text,
    last_name  text,
    password   text
);
