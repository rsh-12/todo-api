drop table task;

create table if not exists task
(
    id              serial      not null
        constraint task_pkey primary key,
    title           varchar(50) not null,
    completed       boolean   default false,
    starred         boolean   default false,
    completion_date date      default CURRENT_DATE,
    created_at      timestamp default CURRENT_TIMESTAMP,
    updated_at      timestamp default CURRENT_TIMESTAMP,
    list_id         integer
        constraint task_list_id_fkey
            references custom_list
            on update cascade on delete set null
);
