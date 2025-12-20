CREATE TABLE accounts (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          user_name VARCHAR(20) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL,
                          profile_id BIGINT,
                          created_date TIMESTAMP,
                          last_modified_date TIMESTAMP
);

CREATE TABLE roles (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE account_roles (
                               account_id BIGINT NOT NULL,
                               role_id BIGINT NOT NULL,
                               PRIMARY KEY (account_id, role_id),
                               FOREIGN KEY (account_id) REFERENCES accounts(id),
                               FOREIGN KEY (role_id) REFERENCES roles(id)
);

INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');