create table user_roles
(
    roles   integer,
    user_id integer references app_user (id)
);
