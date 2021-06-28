create table tokens
(
    id            serial primary key,
    value         varchar   not null,
    user_id       int       not null,
    created_by_ip varchar,
    expires_at    timestamp not null,
    created_at    timestamp not null default current_timestamp,
    updated_at    timestamp not null default current_timestamp
)