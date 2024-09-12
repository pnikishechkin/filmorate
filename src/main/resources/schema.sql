DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS ratings CASCADE;
DROP TABLE IF EXISTS mpa CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS films_genres CASCADE;
DROP TABLE IF EXISTS directors CASCADE;
DROP TABLE IF EXISTS film_directors CASCADE;
DROP TABLE IF EXISTS users_friends CASCADE;
DROP TABLE IF EXISTS users_films_likes CASCADE;
DROP TABLE IF EXISTS likes CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS reviews_likes CASCADE;
DROP TABLE IF EXISTS events CASCADE;

CREATE TABLE IF NOT EXISTS genres
(
    genre_id   INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa
(
    mpa_id   INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    mpa_name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    user_id   INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email     VARCHAR NOT NULL,
    login     VARCHAR NOT NULL,
    user_name VARCHAR NOT NULL,
    birthday  date
);

CREATE TABLE IF NOT EXISTS films
(
    film_id      INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    mpa_id       INT     NOT NULL REFERENCES mpa (mpa_id) ON DELETE CASCADE,
    film_name    VARCHAR NOT NULL,
    description  VARCHAR NOT NULL,
    release_date DATE    NOT NULL,
    duration     INT     NOT NULL
);

CREATE TABLE IF NOT EXISTS films_genres
(
    film_id  INT NOT NULL REFERENCES films (film_id) ON DELETE CASCADE,
    genre_id INT NOT NULL REFERENCES genres (genre_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id,
                 genre_id)
);

CREATE TABLE IF NOT EXISTS directors
(
    director_id   INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    director_name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS film_directors
(
    film_id  INT NOT NULL REFERENCES films (film_id) ON DELETE CASCADE,
    director_id INT NOT NULL REFERENCES directors (director_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id,
                 director_id)
);

CREATE TABLE IF NOT EXISTS users_friends
(
    user_id   INT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    friend_id INT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id,
                 friend_id)
);

CREATE TABLE IF NOT EXISTS users_films_likes
(
    user_id INT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    film_id INT NOT NULL REFERENCES films (film_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id,
                 film_id)
);

CREATE TABLE IF NOT EXISTS reviews
(
    review_id    INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    film_id      INT     NOT NULL REFERENCES films (film_id) ON DELETE CASCADE,
    user_id      INT     NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    content      VARCHAR NOT NULL,
    is_positive     BOOL NOT NULL,
    CONSTRAINT unique_film_user UNIQUE (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS reviews_likes
(
    review_id    INT     NOT NULL REFERENCES reviews (review_id) ON DELETE CASCADE,
    user_id      INT     NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    is_useful    BOOL NOT NULL,
    PRIMARY KEY (review_id, user_id)
);

CREATE TABLE IF NOT EXISTS events
(
    event_id   INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    INT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    entity_id  INT NOT NULL,
    event_type VARCHAR(10) NOT NULL CHECK (event_type IN ('LIKE', 'REVIEW', 'FRIEND')),
    operation  VARCHAR(10) NOT NULL CHECK (operation IN ('ADD', 'UPDATE', 'REMOVE')),
    timestamp  TIMESTAMP NOT NULL
);
