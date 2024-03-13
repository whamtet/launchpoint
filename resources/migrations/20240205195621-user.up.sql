--;;
create table user
(
    user_id    integer primary key asc,
    email      text unique,
    first_name text,
    last_name  text,
    q          text,
    password   text
);
--;;
insert into user (email, first_name, last_name, q, password)
values ('whamtet@gmail.com', 'Matthew', 'Molloy', 'matthew molloy', '$2a$11$5os8Fix.C95RKUgc8gS/mO.5gQWYCsq5kNRanWANQUmsraRtTsgBa');
--;;
insert into user (email, first_name, last_name, q, password)
values ('whamtet+1@gmail.com', 'Test', 'User', 'test user', '$2a$11$5os8Fix.C95RKUgc8gS/mO.5gQWYCsq5kNRanWANQUmsraRtTsgBa');
