--;;
create table item_order
(
    user_id integer,
    inventory_id integer,
    quantity integer,
    foreign key(user_id) references user(user_id)
);
