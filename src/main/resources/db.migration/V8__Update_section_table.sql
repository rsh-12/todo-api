alter table section
    add column created_at timestamp default CURRENT_TIMESTAMP,
    add column updated_at timestamp default CURRENT_TIMESTAMP