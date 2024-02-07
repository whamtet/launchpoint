-- Place your queries here. Docs available https://www.hugsql.org/
-- :name insert-user :returning-execute
insert into user (first_name, last_name, email, password)
values (:first-name, :last-name, :email, :password)
returning id

-- :name get-user-by-email :query :one
select id, password from user where email = :email

-- :name get-user :query :one
select * from user where id = :id

-- :name upsert-cv :execute
insert into cv (user_id, cv)
values (:id, :cv)
on conflict(user_id) do update set cv = :cv
