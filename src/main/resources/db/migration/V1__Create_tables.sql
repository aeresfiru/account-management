CREATE SCHEMA IF NOT EXISTS account_db;

SET search_path TO account_db;

CREATE TABLE roles
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255)       NOT NULL
);

CREATE TABLE accounts
(
    id         SERIAL PRIMARY KEY,
    balance    NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    is_blocked BOOLEAN        NOT NULL DEFAULT FALSE,
    user_id    INTEGER UNIQUE NOT NULL,
    CONSTRAINT fk_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL
);

CREATE TABLE user_roles
(
    user_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_user
        FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_role_role
        FOREIGN KEY (role_id) REFERENCES roles (id)
);

-- fake data

INSERT INTO account_db.roles (id, name)
VALUES (1, 'ADMIN'),
       (2, 'USER');

INSERT INTO account_db.users (id, username, password)
VALUES (1, 'user1', '$2a$12$pfpPpqx09eqDU9Du3ebqMuTJnBs0bnWXy4fE0R7ahA4f/JgPZTvfK'), -- password1
       (2, 'user2', '$2a$12$Z2GLRT6k78qLYsv/eEB6EeZcvphFcmiMrlWiohWTo81zyT2zWkH.G'), -- password2
       (3, 'admin', '$2a$12$JhUJomR1XK8F0Ed7EG/CPulqMydR4QbCHGk/vclCzS5BVOVnvRLJ.'); -- admin

INSERT INTO account_db.accounts (id, balance, is_blocked, user_id)
VALUES (1, 100.00, FALSE, 1),
       (2, 200.00, FALSE, 2),
       (3, 500.00, FALSE, 3);

INSERT INTO account_db.user_roles (user_id, role_id)
VALUES (1, 2),
       (2, 2),
       (3, 1);
