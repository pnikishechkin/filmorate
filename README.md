# Платформа о кинематографе
Приложение позволяет хранить базу фильмов с их описанием и рейтингом, а также имеет функциональность общения пользователей и сохранения фильмов в Избранное.

#### Функциональность:
- добавление, удаление фильмов и пользователей
- привязка режиссёров к фильмам
- общий поиск фильмов
- поиск самых популярных фильмов по жанру и годам
- общие фильмы пользователей
- рекомендации
- отзывы о фильмах
- лента событий

## ER диаграмма
![ER диаграмма базы данных приложения](/ER_filmorate.png)

#### Описание таблиц:
- `films` - фильмы
- `users` - зарегистрированные пользователи
- `genres` - жанры фильмов
- `mpa` - возможные рейтинги ассоциации кинокомпаний (МРА)
- `films_genres` - связующая таблица для хранения жанров фильма
- `users_films_likes` - связующая таблица для хранения лайков пользователя к фильмам
- `users_friends` - связующая таблица для хранения друзей пользователей. Поле `status` является флагом, подтвердил ли пользователь с идентификатором `friend_id` дружбу
- `directors` - режиссеры фильмов
- `film_directors` - связующая таблица для хранения режиссеров фильма
- `reviews` - отзывы на фильм
- `reviews_likes` - связующая таблица для хранения лайков к отзывам
- `events` - лента событий
