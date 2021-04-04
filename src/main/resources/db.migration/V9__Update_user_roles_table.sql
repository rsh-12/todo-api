alter table user_roles
    drop column roles;

alter table user_roles
    add column role varchar(32);