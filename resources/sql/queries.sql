-- Place your queries here. Docs available https://www.hugsql.org/
-- :name insert-user :execute
insert into user (first_name, last_name, email, password)
values (:first-name, :last-name, :email, :password)
