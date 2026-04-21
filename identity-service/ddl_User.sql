CREATE TABLE user
(
    user_id        VARCHAR(255)                            NOT NULL,
    username       VARCHAR(225) COLLATE utf8mb4_unicode_ci NULL,
    password       VARCHAR(255)                            NULL,
    email          VARCHAR(225) COLLATE utf8mb4_unicode_ci NULL,
    email_verified BIT(1) DEFAULT 0                        NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (user_id)
);

CREATE TABLE user_roles
(
    user_user_id VARCHAR(255) NOT NULL,
    roles_name   VARCHAR(255) NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_user_id, roles_name)
);

ALTER TABLE user
    ADD CONSTRAINT uc_user_email UNIQUE (email);

ALTER TABLE user
    ADD CONSTRAINT uc_user_username UNIQUE (username);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (roles_name) REFERENCES `role` (name);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_user_id) REFERENCES user (user_id);