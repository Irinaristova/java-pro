-- Вставка тестовых данных
INSERT INTO users (username, email, first_name, last_name, age, is_active) VALUES
    ('ivan.petrov', 'ivan.petrov@example.com', 'Иван', 'Петров', 28, TRUE),
    ('maria.sidorova', 'maria.sidorova@example.com', 'Мария', 'Сидорова', 32, TRUE),
    ('alexey.smirnov', 'alexey.smirnov@example.com', 'Алексей', 'Смирнов', 25, TRUE),
    ('elena.kozlova', 'elena.kozlova@example.com', 'Елена', 'Козлова', 29, TRUE),
    ('dmitry.ivanov', 'dmitry.ivanov@example.com', 'Дмитрий', 'Иванов', 35, TRUE),
    ('anna.morozova', 'anna.morozova@example.com', 'Анна', 'Морозова', 27, TRUE),
    ('sergey.volkov', 'sergey.volkov@example.com', 'Сергей', 'Волков', 31, TRUE),
    ('olga.sokolova', 'olga.sokolova@example.com', 'Ольга', 'Соколова', 26, FALSE),
    ('andrey.popov', 'andrey.popov@example.com', 'Андрей', 'Попов', 33, TRUE),
    ('tatyana.lebedeva', 'tatyana.lebedeva@example.com', 'Татьяна', 'Лебедева', 30, TRUE)
ON CONFLICT (username) DO NOTHING;

-- Обновление updated_at для существующих записей
UPDATE users SET updated_at = CURRENT_TIMESTAMP WHERE updated_at IS NULL;