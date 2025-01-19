-- Добавление рейтингов MPAA
MERGE INTO mpa (rating_id, rating_name, description) KEY (rating_id) VALUES (1, 'G', 'У фильма нет возрастных ограничений');
MERGE INTO mpa (rating_id, rating_name, description) KEY (rating_id) VALUES (2, 'PG', 'Детям рекомендуется смотреть фильм с родителями');
MERGE INTO mpa (rating_id, rating_name, description) KEY (rating_id) VALUES (3, 'PG-13', 'Детям до 13 лет просмотр не желателен');
MERGE INTO mpa (rating_id, rating_name, description) KEY (rating_id) VALUES (4, 'R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого');
MERGE INTO mpa (rating_id, rating_name, description) KEY (rating_id) VALUES (5, 'NC-17', 'Лицам до 18 лет просмотр запрещён');

-- Добавление жанров
MERGE INTO genres (genre_id, genre_name) KEY (genre_id) VALUES (1, 'Комедия');
MERGE INTO genres (genre_id, genre_name) KEY (genre_id) VALUES (2, 'Драма');
MERGE INTO genres (genre_id, genre_name) KEY (genre_id) VALUES (3, 'Мультфильм');
MERGE INTO genres (genre_id, genre_name) KEY (genre_id) VALUES (4, 'Триллер');
MERGE INTO genres (genre_id, genre_name) KEY (genre_id) VALUES (5, 'Документальный');
MERGE INTO genres (genre_id, genre_name) KEY (genre_id) VALUES (6, 'Боевик');