insert into section(id, title, created_at, updated_at)
values (1, 'Important', current_timestamp, current_timestamp),
       (2, 'Starred', current_timestamp, current_timestamp),
       (3, 'Later', current_timestamp, current_timestamp);


insert into task(id, title, completed, starred, completion_date, created_at, updated_at, list_id)
values (1, 'Read a book', false, false, current_timestamp,
        current_timestamp, current_timestamp, 1),

       (2, 'Create a presentation', false, false, current_timestamp,
        current_timestamp, current_timestamp, 2),

       (3, 'Write a letter', false, false, current_timestamp,
        current_timestamp, current_timestamp, 3);

