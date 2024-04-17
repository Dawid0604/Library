create table if not exists Users(
    UserId BIGINT NOT NULL,
    Username VARCHAR(64) NOT NULL,
    Password VARCHAR(64) NOT NULL,
    Roles VARCHAR(64) NOT NULL,

    PRIMARY KEY (UserId)
);