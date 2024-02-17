--;;
create table user
(
    id         integer primary key asc,
    email      text unique,
    first_name text,
    last_name  text,
    password   text
);
--;;
insert into user (email, first_name, last_name, password)
values ('whamtet@gmail.com', 'Matthew', 'Molloy', '$2a$11$5os8Fix.C95RKUgc8gS/mO.5gQWYCsq5kNRanWANQUmsraRtTsgBa');
