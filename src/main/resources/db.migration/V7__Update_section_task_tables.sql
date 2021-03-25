alter table section
    add column user_id integer references app_user (id);

alter table task
    add column user_id integer references app_user (id);
