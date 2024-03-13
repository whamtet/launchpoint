-- :name delete-cv :execute
delete from cv where user_id = :user-id;

-- :name delete-user :execute
delete from user where user_id = :user-id;

-- :name delete-orders :returning-execute
delete from orders where user_id = :user-id
returning order_id;
