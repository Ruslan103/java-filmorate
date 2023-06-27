DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE IF NOT EXISTS users
(
    user_id  int PRIMARY KEY AUTO_INCREMENT,
    login    varchar,
    email    varchar,
    name     varchar,
    birthday date
);DROP TABLE IF EXISTS friends CASCADE;
CREATE TABLE IF NOT EXISTS friends
(
    id  int PRIMARY KEY AUTO_INCREMENT,
    user_id    int,
    friend_id int
);
DROP TABLE IF EXISTS films CASCADE;
   DROP TABLE IF EXISTS mpa;
    CREATE TABLE IF NOT EXISTS mpa
(
    mpa_id int PRIMARY KEY AUTO_INCREMENT,
    name varchar UNIQUE
);

CREATE TABLE IF NOT EXISTS films
(
    film_id      int PRIMARY KEY AUTO_INCREMENT,
    name         varchar,
    description varchar,
    release_date date,
    duration     int,
     mpa_id         int,
    FOREIGN KEY (mpa_id) REFERENCES mpa (mpa_id)
    );
DROP TABLE IF EXISTS genre CASCADE;
CREATE TABLE IF NOT EXISTS genre
(
    genre_id int PRIMARY KEY AUTO_INCREMENT,
    name     varchar UNIQUE
);
DROP TABLE IF EXISTS film_genre CASCADE;
CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  int,
    genre_id int,
    FOREIGN KEY (film_id) REFERENCES films (film_id),
    FOREIGN KEY (genre_id) REFERENCES genre (genre_id),
    UNIQUE (film_id, genre_id)
    );
INSERT INTO MPA (name) VALUES ('G');
INSERT INTO MPA (name) VALUES ('PG');
INSERT INTO MPA (name) VALUES ('PG-13');
INSERT INTO MPA (name) VALUES ('R');
INSERT INTO MPA (name) VALUES ('NC-17');
INSERT INTO genre (genre_id, name)
    VALUES  (1,'Комедия'),            (2,'Драма'),
            (3,'Мультфильм'),            (4,'Триллер'),
            (5,'Документальный'),            (6,'Боевик');











