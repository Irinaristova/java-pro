INSERT INTO users (username, email, first_name, last_name, age, is_active) VALUES
    ('ivan.petrov', 'ivan@example.com', 'Иван', 'Петров', 28, TRUE),
    ('maria.sidorova', 'maria@example.com', 'Мария', 'Сидорова', 32, TRUE),
    ('alexey.smirnov', 'alexey@example.com', 'Алексей', 'Смирнов', 25, TRUE),
    ('elena.kozlova', 'elena@example.com', 'Елена', 'Козлова', 29, TRUE),
    ('dmitry.ivanov', 'dmitry@example.com', 'Дмитрий', 'Иванов', 35, TRUE)
ON CONFLICT (username) DO NOTHING;