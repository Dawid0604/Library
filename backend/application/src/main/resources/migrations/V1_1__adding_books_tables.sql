create table if not EXISTS Authors (
    AuthorId BIGINT AUTO_INCREMENT NOT NULL,
    Name VARCHAR(1024) NOT NULL,
    Description LONGTEXT NOT NULL,
    Picture VARCHAR(1024),

    PRIMARY KEY (AuthorId)
);

create table if not EXISTS Publishers(
    PublisherId BIGINT AUTO_INCREMENT NOT NULL,
    Name VARCHAR(1024) NOT NULL,

    PRIMARY KEY (PublisherId)
);

create table if not EXISTS Categories(
    CategoryId BIGINT AUTO_INCREMENT NOT NULL,
    Name varchar(256) NOT NULL,
    CategoryIndex varchar(256) NOT NULL,

    PRIMARY KEY (CategoryId)
);

create table if not EXISTS Books(
    BookId BIGINT AUTO_INCREMENT NOT NULL,
    Title VARCHAR(128),
    Price DOUBLE,
    OriginalPrice DOUBLE DEFAULT NULL,
    Quantity INT NOT NULL DEFAULT 0,
    PublisherId BIGINT NOT NULL,
    NumberOfPages INT NOT NULL DEFAULT 0,
    Edition INT NOT NULL DEFAULT 0,
    NumberOfStars INT NOT NULL DEFAULT 0,
    PublicationYear INT,
    Description LONGTEXT,
    CategoryId BIGINT NOT NULL,
    MainPicture VARCHAR(1024),
    BookCover VARCHAR(16),

    PRIMARY KEY (BookId),
    FOREIGN KEY (PublisherId) REFERENCES Publishers(PublisherId),
    FOREIGN KEY (CategoryId) REFERENCES Categories(CategoryId)
);

create table if not EXISTS Pictures(
    PictureId BIGINT AUTO_INCREMENT NOT NULL,
    Path VARCHAR(1024),
    BookId BIGINT NOT NULL,

    PRIMARY KEY (PictureId),
    FOREIGN KEY (BookId) REFERENCES Books(BookId)
);

create table if not EXISTS AuthorsBooks(
    BookId BIGINT NOT NULL,
    AuthorId BIGINT NOT NULL,

    FOREIGN KEY (BookId) REFERENCES Books(BookId),
    FOREIGN KEY (AuthorId) REFERENCES Authors(AuthorId)
);
