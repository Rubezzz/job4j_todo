create table task_category (
    id serial primary key,
    task_id int references tasks(id),
    category_id int references category(id)
);