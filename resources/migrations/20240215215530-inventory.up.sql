--;;
create table inventory
(
    item_id integer primary key asc,
    price real,
    count integer
);
--;;
create table ratings
(
    user_id integer,
    inventory_id integer,
    rating integer,
    foreign key(user_id) references user(user_id)
);
--;;
create unique index ratings_idx ON ratings(user_id, inventory_id);
