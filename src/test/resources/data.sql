insert into app_user(username, password, created_at)
values ('admin@mail.com', '$2a$10$YamL9ghddJL6OLQcSpYb1uPkno8Pw64/jyVCkK1nZDKoE2T.ZrdFC', current_timestamp),
       ('client@mail.com', '$2a$10$ByBjnv4AAlFwtu2NT2N6puUcUZwh2lMmV0gSKv7ZQ6vKfNqTRTJTi', current_timestamp);

insert into user_roles(roles, user_id)
VALUES (1, 1),
       (0, 2);


insert into section(id, title, created_at, updated_at, user_id)
values (1, 'Important', current_timestamp, DATEADD('HOUR', 1, current_timestamp), 1),
       (2, 'Starred', DATEADD('DAY', -7, current_timestamp), DATEADD('DAY', -2, current_timestamp), 2),
       (3, 'Later', DATEADD('MONTH', -1, current_timestamp), DATEADD('WEEK', -1, current_timestamp), 1);


insert into task(id, title, completed, starred, completion_date, created_at, updated_at, list_id, user_id)
values (1, 'Read a book', false, false, DATEADD('WEEK', +1, current_timestamp),
        current_timestamp, DATEADD('MINUTE', 40, current_timestamp), 1, 1),

       (2, 'Create a presentation', false, true, DATEADD('WEEK', -1, current_timestamp),
        DATEADD('WEEK', -3, current_timestamp), DATEADD('DAY', -3, current_timestamp), 2, 2),

       (3, 'Write a letter', false, false, current_timestamp,
        DATEADD('MONTH', -2, current_timestamp), DATEADD('WEEK', -1, current_timestamp), 3, 1),

       --- for testing some sections methods
       (4, 'Section task 1', false, false, current_timestamp,
        current_timestamp, current_timestamp, null, null),

       (5, 'Section task 2', false, false, current_timestamp,
        current_timestamp, current_timestamp, null, 2),

       (6, 'Seciton task 3', false, false, current_timestamp,
        current_timestamp, current_timestamp, null, null);

