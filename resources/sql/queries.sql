-- Place your queries here. Docs available https://www.hugsql.org/
-- :name insert-user :returning-execute
insert into user (first_name, last_name, email, password)
values (:first-name, :last-name, :email, :password)
returning rowid

-- :name get-user-by-email :query :one
select rowid, password from user where email = :email

-- :name get-user :query :one
select * from user where rowid = :id
