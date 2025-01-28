-- Вставка данных в таблицу жанров (с проверкой на дубликаты)
INSERT INTO genres (genre_id, genre_name)
SELECT 1, 'Комедия' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 1);
INSERT INTO genres (genre_id, genre_name)
SELECT 2, 'Драма' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 2);
INSERT INTO genres (genre_id, genre_name)
SELECT 3, 'Мультфильм' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 3);
INSERT INTO genres (genre_id, genre_name)
SELECT 4, 'Триллер' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 4);
INSERT INTO genres (genre_id, genre_name)
SELECT 5, 'Документальный' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 5);
INSERT INTO genres (genre_id, genre_name)
SELECT 6, 'Боевик' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 6);

-- Вставка данных в таблицу рейтингов MPA (с проверкой на дубликаты)
INSERT INTO mpa_rating (rating_id, name)
SELECT 1, 'G' WHERE NOT EXISTS (SELECT 1 FROM mpa_rating WHERE rating_id = 1);
INSERT INTO mpa_rating (rating_id, name)
SELECT 2, 'PG' WHERE NOT EXISTS (SELECT 1 FROM mpa_rating WHERE rating_id = 2);
INSERT INTO mpa_rating (rating_id, name)
SELECT 3, 'PG13' WHERE NOT EXISTS (SELECT 1 FROM mpa_rating WHERE rating_id = 3);
INSERT INTO mpa_rating (rating_id, name)
SELECT 4, 'R' WHERE NOT EXISTS (SELECT 1 FROM mpa_rating WHERE rating_id = 4);
INSERT INTO mpa_rating (rating_id, name)
SELECT 5, 'NC17' WHERE NOT EXISTS (SELECT 1 FROM mpa_rating WHERE rating_id = 5);