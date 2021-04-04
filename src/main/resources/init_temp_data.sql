truncate table section cascade;
truncate table task cascade;

alter sequence custom_list_id_seq restart with 1;
alter sequence task_id_seq restart with 1;

insert into section(title, user_id)
VALUES ('Important (admin)', 1),
       ('Later (admin)', 1),
       ('Weekends (client)', 2);

insert into task (title, list_id, user_id)
values ('Buy a milk', 1, 1),
       ('Relax', 2, 2);

-- phantom tasks :)
insert into task(title, list_id, user_id)
VALUES ('Phantom task 1', null, null),
       ('Phantom task 2', null, null),
       ('Phantom task 3', null, null),
       ('Phantom task 4', null, null);
