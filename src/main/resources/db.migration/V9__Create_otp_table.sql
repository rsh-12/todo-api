create table otp
(
    id         serial primary key,
    username   varchar(50) unique,
    code       varchar(6) not null,
    created_at timestamp default CURRENT_TIMESTAMP,
    updated_at timestamp default CURRENT_TIMESTAMP,
    expires_at timestamp default CURRENT_TIMESTAMP + interval '10 MINUTES',

    check ( length(code) = 6 )
);