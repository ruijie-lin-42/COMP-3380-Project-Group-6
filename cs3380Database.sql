USE cs3380;

--clean up
DROP TABLE if EXISTS PlaysIn;
DROP TABLE if EXISTS WorksOn;
DROP TABLE if EXISTS Makes;
DROP TABLE if EXISTS Includes;
DROP TABLE if EXISTS Has;
DROP TABLE if EXISTS TaggedWith;
DROP TABLE if EXISTS Rated;

DROP TABLE if EXISTS Worker;
DROP TABLE if EXISTS Movie;
DROP TABLE if EXISTS Language;
DROP TABLE if EXISTS Users;
DROP TABLE if EXISTS Company;
DROP TABLE if EXISTS Country;
DROP TABLE if EXISTS Genre;
DROP TABLE if EXISTS Keywords;

--tables
CREATE TABLE Worker(
    id INT PRIMARY KEY NOT NULL,
    name VARCHAR(30),
    gender INTEGER
);

CREATE TABLE Language(
    id INT PRIMARY KEY NOT NULL,
    iso CHAR(2) UNIQUE,
    name VARCHAR(30)
);

CREATE TABLE Movie(
    movie_id INT PRIMARY KEY NOT NULL,
    vote_count INT,
    vote_average FLOAT,
    revenue INT,
    original_title VARCHAR(50),
    popularity DECIMAL(10, 6),
    budget INT,
    title VARCHAR(50),
    runtime FLOAT,
    release_date DATE,
    original_language CHAR(2),
    FOREIGN KEY (original_language) REFERENCES Language(iso)
);

CREATE TABLE Users(
    user_id INT PRIMARY KEY NOT NULL
);

CREATE TABLE Company(
    production_id INT PRIMARY KEY NOT NULL,
    company_name VARCHAR(30)
);

CREATE TABLE Country(
    iso VARCHAR(2) PRIMARY KEY NOT NULL,
    country_name VARCHAR(50)
);

CREATE TABLE Genre(
    genre_id INT PRIMARY KEY NOT NULL,
    genre_name VARCHAR(20)
);

CREATE TABLE Keywords(
    keyword_id INT PRIMARY KEY NOT NULL,
    keyword_name VARCHAR(20)
);

--relationships
CREATE TABLE PlaysIn(
    cast_id INT,
    character VARCHAR(30),
    id INT NOT NULL,
    movie_id INT NOT NULL,
    PRIMARY KEY (id, movie_id),
    FOREIGN KEY (id) REFERENCES Worker(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES Movie(movie_id) ON DELETE CASCADE
);

CREATE TABLE WorksOn(
    department VARCHAR(30),
    job VARCHAR(30),
    id INT NOT NULL,
    movie_id INT NOT NULL,
    PRIMARY KEY (id, movie_id),
    FOREIGN KEY (id) REFERENCES Worker(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES Movie(movie_id) ON DELETE CASCADE
);

CREATE TABLE Makes(
    production_id INT NOT NULL,
    movie_id INT NOT NULL,
    PRIMARY KEY (production_id, movie_id),
    FOREIGN KEY (production_id) REFERENCES Company(production_id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES Movie(movie_id) ON DELETE CASCADE
);

CREATE TABLE Includes(
    iso VARCHAR(2) NOT NULL,
    movie_id INT NOT NULL,
    PRIMARY KEY (iso, movie_id),
    FOREIGN KEY (iso) REFERENCES Country(iso) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES Movie(movie_id) ON DELETE CASCADE
);

CREATE TABLE Has(
    genre_id INT NOT NULL,
    movie_id INT NOT NULL,
    PRIMARY KEY (genre_id, movie_id),
    FOREIGN KEY (genre_id) REFERENCES Genre(genre_id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES Movie(movie_id) ON DELETE CASCADE
);

CREATE TABLE TaggedWith(
    keyword_id INT NOT NULL,
    movie_id INT NOT NULL,
    PRIMARY KEY (keyword_id, movie_id),
    FOREIGN KEY (keyword_id) REFERENCES Keywords(keyword_id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES Movie(movie_id) ON DELETE CASCADE
);

CREATE TABLE Rated(
    rating FLOAT,
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    PRIMARY KEY (user_id, movie_id),
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES Movie(movie_id) ON DELETE CASCADE
);

--insert data:
insert into Country (iso, country_name) values ('ca', 'Canada');
SELECT * FROM Country