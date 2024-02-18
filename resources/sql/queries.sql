-- Place your queries here. Docs available https://www.hugsql.org/
-- :name insert-user :returning-execute
insert into user (first_name, last_name, q, email, password)
values (:first-name, :last-name, :q, :email, :password)
returning user_id

-- :name get-user-by-email :query :one
select user_id, password from user where email = :email

-- :name get-user :query :one
select * from user where user_id = :id

-- :name get-cv :query :one
select * from cv where user_id = :id

-- :name upsert-cv :execute
insert into cv (user_id, cv)
values (:id, :cv)
on conflict(user_id) do update set cv = :cv

-- :name upsert-price :execute
insert into inventory (item_id, price)
values (:item_id, :price)
on conflict(item_id) do update set price = :price

-- :name upsert-count :execute
insert into inventory (item_id, count)
values (:item_id, :count)
on conflict(item_id) do update set count = :count

-- :name update-names :execute
update user
set first_name = :first_name, last_name = :last_name
where id = :id

-- :name inventory-all :query
select * from inventory;

-- :name ratings-all :query
select * from ratings;

-- :name search-users :query
select * from user where q like :q;
