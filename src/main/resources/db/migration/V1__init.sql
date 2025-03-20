DROP TABLE if EXISTS member;

CREATE TABLE member (
    id BINARY(16) PRIMARY KEY,
    github_id INT UNIQUE,
    github_login VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    github_url VARCHAR(255) NOT NULL,
    o_auth_provider VARCHAR(50) NOT NULL,
    priority BIGINT DEFAULT 0 NOT NULL,
    email VARCHAR(255) UNIQUE,
    job VARCHAR(50),
    bio TEXT
);

DROP TABLE if EXISTS language;

CREATE TABLE language (
    id BINARY(16) PRIMARY KEY,
    language VARCHAR(255) NOT NULL,
    code_lines INT NOT NULL,
    member_id BINARY(16),
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);

