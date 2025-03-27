DROP TABLE if EXISTS comment;

CREATE TABLE comment (
    id BINARY(16) PRIMARY KEY,
    review TEXT NOT NULL,
    member_id BINARY(16),
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);

ALTER TABLE member
    MODIFY provider_login VARCHAR(100) NOT NULL;

ALTER TABLE member
    MODIFY name VARCHAR(100) NOT NULL;

ALTER TABLE member
    MODIFY provider_url VARCHAR(200) NOT NULL;

ALTER TABLE member
    MODIFY email VARCHAR(100);

ALTER TABLE member
    MODIFY job VARCHAR(20);

ALTER TABLE skill
    MODIFY language VARCHAR(50);