USE cs3380;

--clean up
DROP TABLE if EXISTS siemen40.PlaysIn;
DROP TABLE if EXISTS siemen40.WorksOn;
DROP TABLE if EXISTS siemen40.Makes;
DROP TABLE if EXISTS siemen40.Includes;
DROP TABLE if EXISTS siemen40.Has;
DROP TABLE if EXISTS siemen40.TaggedWith;
DROP TABLE if EXISTS siemen40.Rated;

DROP TABLE if EXISTS siemen40.Worker;
DROP TABLE if EXISTS siemen40.Movie;
DROP TABLE if EXISTS siemen40.Language;
DROP TABLE if EXISTS siemen40.Users;
DROP TABLE if EXISTS siemen40.Company;
DROP TABLE if EXISTS siemen40.Country;
DROP TABLE if EXISTS siemen40.Genre;
DROP TABLE if EXISTS siemen40.Keywords;

--tables
CREATE TABLE siemen40.Worker(
    id INT PRIMARY KEY NOT NULL,
    name VARCHAR(30),
    gender INTEGER
);

CREATE TABLE siemen40.Language(
    id INT PRIMARY KEY NOT NULL,
    iso CHAR(2) UNIQUE,
    name VARCHAR(30)
);

CREATE TABLE siemen40.Movie(
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
    FOREIGN KEY (original_language) REFERENCES siemen40.Language(iso)
);

CREATE TABLE siemen40.Users(
    user_id INT PRIMARY KEY NOT NULL
);

CREATE TABLE siemen40.Company(
    production_id INT PRIMARY KEY NOT NULL,
    company_name VARCHAR(30)
);

CREATE TABLE siemen40.Country(
    iso VARCHAR(2) PRIMARY KEY NOT NULL,
    country_name VARCHAR(50)
);

CREATE TABLE siemen40.Genre(
    genre_id INT PRIMARY KEY NOT NULL,
    genre_name VARCHAR(20)
);

CREATE TABLE siemen40.Keywords(
    keyword_id INT PRIMARY KEY NOT NULL,
    keyword_name VARCHAR(20)
);

--relationships
CREATE TABLE siemen40.PlaysIn(
    cast_id INT,
    character VARCHAR(30),
    id INT NOT NULL,
    movie_id INT NOT NULL,
    PRIMARY KEY (id, movie_id),
    FOREIGN KEY (id) REFERENCES siemen40.Worker(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES siemen40.Movie(movie_id) ON DELETE CASCADE
);

CREATE TABLE siemen40.WorksOn(
    department VARCHAR(30),
    job VARCHAR(30),
    id INT NOT NULL,
    movie_id INT NOT NULL,
    PRIMARY KEY (id, movie_id),
    FOREIGN KEY (id) REFERENCES siemen40.Worker(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES siemen40.Movie(movie_id) ON DELETE CASCADE
);

CREATE TABLE siemen40.Makes(
    production_id INT NOT NULL,
    movie_id INT NOT NULL,
    PRIMARY KEY (production_id, movie_id),
    FOREIGN KEY (production_id) REFERENCES siemen40.Company(production_id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES siemen40.Movie(movie_id) ON DELETE CASCADE
);

CREATE TABLE siemen40.Includes(
    iso VARCHAR(2) NOT NULL,
    movie_id INT NOT NULL,
    PRIMARY KEY (iso, movie_id),
    FOREIGN KEY (iso) REFERENCES siemen40.Country(iso) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES siemen40.Movie(movie_id) ON DELETE CASCADE
);

CREATE TABLE siemen40.Has(
    genre_id INT NOT NULL,
    movie_id INT NOT NULL,
    PRIMARY KEY (genre_id, movie_id),
    FOREIGN KEY (genre_id) REFERENCES siemen40.Genre(genre_id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES siemen40.Movie(movie_id) ON DELETE CASCADE
);

CREATE TABLE siemen40.TaggedWith(
    keyword_id INT NOT NULL,
    movie_id INT NOT NULL,
    PRIMARY KEY (keyword_id, movie_id),
    FOREIGN KEY (keyword_id) REFERENCES siemen40.Keywords(keyword_id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES siemen40.Movie(movie_id) ON DELETE CASCADE
);

CREATE TABLE siemen40.Rated(
    rating FLOAT,
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    PRIMARY KEY (user_id, movie_id),
    FOREIGN KEY (user_id) REFERENCES siemen40.Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES siemen40.Movie(movie_id) ON DELETE CASCADE
);

--insert data:
insert into siemen40.Country (iso, country_name) values ('ca', 'Canada');
SELECT * FROM siemen40.Country