insert into section(id, title, created_at, updated_at)
values (1, 'Important', current_timestamp, DATEADD('HOUR', 1, current_timestamp)),
       (2, 'Starred', DATEADD('DAY', -7, current_timestamp), DATEADD('DAY', -2, current_timestamp)),
       (3, 'Later', DATEADD('MONTH', -1, current_timestamp), DATEADD('WEEK', -1, current_timestamp));


insert into task(id, title, completed, starred, completion_date, created_at, updated_at, list_id)
values (1, 'Read a book', false, false, current_timestamp,
        current_timestamp, DATEADD('MINUTE', 40, current_timestamp), 1),

       (2, 'Create a presentation', false, false, current_timestamp,
        DATEADD('WEEK', -3, current_timestamp), DATEADD('DAY', -3, current_timestamp), 2),

       (3, 'Write a letter', false, false, current_timestamp,
        DATEADD('MONTH', -2, current_timestamp), DATEADD('WEEK', -1, current_timestamp), 3);

