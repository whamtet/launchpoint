--;;
create table user
(
    id         integer primary key asc,
    email      text unique,
    first_name text,
    last_name  text,
    q          text,
    password   text
);
--;;
insert into user (email, first_name, last_name, q, password)
values ('whamtet@gmail.com', 'Matthew', 'Molloy', 'matthew molloy', '$2a$11$5os8Fix.C95RKUgc8gS/mO.5gQWYCsq5kNRanWANQUmsraRtTsgBa');
