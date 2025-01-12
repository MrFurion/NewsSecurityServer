CREATE TABLE Users(
                      id SERIAL PRIMARY KEY,
                      username varchar(255) not null unique ,
                      password varchar(255) not null,
                      email varchar(255) not null unique,
                      role varchar(255)
);

INSERT INTO Users (username, password, email, role)
VALUES ('Admin', '$2a$10$FKpXp61J0hNg5/hYoJisU.77ocCHVfqPKbSvXuLaWj.QBEG7BxVS6', 'admin@mail.com', 'ROLE_ADMIN');
INSERT INTO Users (username, password, email, role)
VALUES ('Journalist', '$2a$10$FKpXp61J0hNg5/hYoJisU.77ocCHVfqPKbSvXuLaWj.QBEG7BxVS6', 'journalist@mail.com', 'ROLE_JOURNALIST');
INSERT INTO Users (username, password, email, role)
VALUES ('Subscriber', '$2a$10$FKpXp61J0hNg5/hYoJisU.77ocCHVfqPKbSvXuLaWj.QBEG7BxVS6', 'subscriber@mail.com', 'ROLE_SUBSCRIBER');