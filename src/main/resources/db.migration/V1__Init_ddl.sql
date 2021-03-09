create table if not exists custom_list
(
    id   serial      not null constraint custom_list_pkey primary key,
    title varchar(50) not null
);


create table if not exists task
(
    id              serial      not null        constraint task_pkey primary key,
    title            varchar(50) not null,
    starred         boolean   default false,
    completion_date date      default CURRENT_DATE,
    created_at      timestamp default CURRENT_TIMESTAMP,
    updated_at      timestamp default CURRENT_TIMESTAMP,
    completed       boolean   default false,
    list_id         integer constraint task_list_id_fkey
            references custom_list
            on update cascade on delete set null
);


