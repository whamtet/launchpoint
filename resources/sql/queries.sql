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

-- :name decrement-inventory :execute
update inventory
set count = max(count - :quantity, 0)
where item_id = :id

-- :name update-names :execute
update user
set first_name = :first_name, last_name = :last_name
where user_id = :id

-- :name inventory-all :query
select * from inventory;

-- :name ratings-all :query
select * from ratings;

-- :name rating :query :one
select * from ratings where user_id = :id and inventory_id = :item-id

-- :name search-users :query
select * from user where q like :q

-- :name basket-count :query :one
select count(*) as items from item_order where user_id = :id

-- :name add-order :execute
insert into item_order(user_id, inventory_id, quantity)
values (:id, :inventory_id, 1)
on conflict(user_id, inventory_id) do update set quantity = quantity + 1

-- :name my-order :query
select * from item_order where user_id = :id

-- :name inc-order :returning-execute
update item_order
set quantity = quantity + 1
where user_id = :id and inventory_id = :inventory_id
returning quantity

-- :name dec-order :returning-execute
update item_order
set quantity = quantity - 1
where user_id = :id and inventory_id = :inventory_id
returning quantity

-- :name del-order :execute
delete from item_order
where user_id = :id and inventory_id = :inventory_id

-- :name del-order-all :execute
delete from item_order
where user_id = :id

-- :name finalize-order :returning-execute
insert into orders(user_id, description)
values (:id, :description)
returning order_id

-- :name order1 :query :one
select description from orders where order_id = :order-id and user_id = :id

-- :name complete-orders :query
select order_id, description from orders where user_id = :id
