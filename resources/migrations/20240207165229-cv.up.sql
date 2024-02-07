--;;
create table cv
(
    user_id integer primary key asc,
    cv text,
    foreign key(user_id) references user(id)
);
