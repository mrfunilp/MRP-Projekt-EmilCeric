CREATE TABLE users
(
    username      VARCHAR(50) PRIMARY KEY,
    password_hash VARCHAR(64) NOT NULL
);

CREATE TABLE media_entries
(
    id               VARCHAR(100) PRIMARY KEY,
    title            VARCHAR(255) NOT NULL,
    description      TEXT,
    media_type       VARCHAR(20)  NOT NULL,
    release_year     INTEGER,
    age_restriction  INTEGER DEFAULT 0,
    creator_username VARCHAR(50)  NOT NULL,
    FOREIGN KEY (creator_username) REFERENCES users (username)
);

CREATE TABLE ratings
(
    id                VARCHAR(100) PRIMARY KEY,
    media_id          VARCHAR(100) NOT NULL,
    username          VARCHAR(50)  NOT NULL,
    stars             INTEGER CHECK (stars >= 1 AND stars <= 5),
    comment           TEXT,
    timestamp         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    comment_confirmed BOOLEAN   DEFAULT FALSE,
    likes             INTEGER   DEFAULT 0,
    FOREIGN KEY (media_id) REFERENCES media_entries (id),
    FOREIGN KEY (username) REFERENCES users (username)
);

CREATE TABLE favorites
(
    username VARCHAR(50),
    media_id VARCHAR(100),
    PRIMARY KEY (username, media_id),
    FOREIGN KEY (username) REFERENCES users (username),
    FOREIGN KEY (media_id) REFERENCES media_entries (id)
);

CREATE TABLE media_genres
(
    media_id VARCHAR(100),
    genre    VARCHAR(50),
    PRIMARY KEY (media_id, genre),
    FOREIGN KEY (media_id) REFERENCES media_entries (id)
);