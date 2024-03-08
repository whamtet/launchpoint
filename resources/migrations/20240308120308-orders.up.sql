--;;
create table orders (
    order_id    integer primary key asc,
    user_id     integer,
    description text,
    foreign key(user_id) references user(user_id)
);
