truncate table app_user cascade ;
truncate table user_roles cascade ;

alter sequence app_user_id_seq restart with 1;

insert into app_user(username, password)
values ('admin@mail.com', '$2a$10$YamL9ghddJL6OLQcSpYb1uPkno8Pw64/jyVCkK1nZDKoE2T.ZrdFC'), -- admin
       ('client@mail.com', '$2a$10$ByBjnv4AAlFwtu2NT2N6puUcUZwh2lMmV0gSKv7ZQ6vKfNqTRTJTi'); -- client

insert into user_roles (user_id, role)
values (1, 'ADMIN'),
       (2, 'USER');