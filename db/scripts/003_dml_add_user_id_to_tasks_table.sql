ALTER TABLE tasks
ADD user_id int references todo_user(id);