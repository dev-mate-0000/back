DROP TABLE if EXISTS skill;
DROP TABLE if EXISTS member;

CREATE TABLE member (
    id BINARY(16) PRIMARY KEY,
    provider_id INT,
    provider_login VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    provider_url VARCHAR(255) NOT NULL,
    o_auth_provider VARCHAR(50) NOT NULL,
    priority BIGINT DEFAULT 0 NOT NULL,
    email VARCHAR(255),
    job VARCHAR(50),
    bio TEXT
);

CREATE TABLE skill (
    id BINARY(16) PRIMARY KEY,
    language VARCHAR(255),
    code_lines INT,
    member_id BINARY(16),
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);

