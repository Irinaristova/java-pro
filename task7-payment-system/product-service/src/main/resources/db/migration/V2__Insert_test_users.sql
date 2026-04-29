INSERT INTO users (username, email, first_name, last_name, age, is_active) VALUES
    ('ivan.petrov', 'ivan@example.com', 'Иван', 'Петров', 28, TRUE),
    ('maria.sidorova', 'maria@example.com', 'Мария', 'Сидорова', 32, TRUE),
    ('alexey.smirnov', 'alexey@example.com', 'Алексей', 'Смирнов', 25, TRUE),
    ('elena.kozlova', 'elena@example.com', 'Елена', 'Козлова', 29, TRUE),
    ('dmitry.ivanov', 'dmitry@example.com', 'Дмитрий', 'Иванов', 35, TRUE),
    ('anna.morozova', 'anna@example.com', 'Анна', 'Морозова', 27, TRUE),
    ('sergey.volkov', 'sergey@example.com', 'Сергей', 'Волков', 31, TRUE),
    ('olga.sokolova', 'olga@example.com', 'Ольга', 'Соколова', 26, TRUE)
ON CONFLICT (username) DO NOTHING;

UPDATE users SET updated_at = CURRENT_TIMESTAMP WHERE updated_at IS NULL;