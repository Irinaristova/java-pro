DO $$
DECLARE
    user1_id BIGINT;
    user2_id BIGINT;
    user3_id BIGINT;
    user4_id BIGINT;
    user5_id BIGINT;
BEGIN
    SELECT id INTO user1_id FROM users WHERE username = 'ivan.petrov';
    SELECT id INTO user2_id FROM users WHERE username = 'maria.sidorova';
    SELECT id INTO user3_id FROM users WHERE username = 'alexey.smirnov';
    SELECT id INTO user4_id FROM users WHERE username = 'elena.kozlova';
    SELECT id INTO user5_id FROM users WHERE username = 'dmitry.ivanov';

    -- Продукты для Ивана
    INSERT INTO products (account_number, balance, type, user_id) VALUES
        ('40817810000000000001', 150000.50, 'ACCOUNT', user1_id),
        ('40817810000000000002', 25000.00, 'CARD', user1_id);

    -- Продукты для Марии
    INSERT INTO products (account_number, balance, type, user_id) VALUES
        ('40817810000000000003', 500000.00, 'ACCOUNT', user2_id),
        ('40817810000000000004', 75000.00, 'CARD', user2_id),
        ('40817810000000000005', 10000.00, 'CARD', user2_id);

    -- Продукты для Алексея
    INSERT INTO products (account_number, balance, type, user_id) VALUES
        ('40817810000000000006', 250000.00, 'ACCOUNT', user3_id),
        ('40817810000000000007', 5000.00, 'CARD', user3_id);

    -- Продукты для Елены
    INSERT INTO products (account_number, balance, type, user_id) VALUES
        ('40817810000000000008', 750000.00, 'ACCOUNT', user4_id);

    -- Продукты для Дмитрия
    INSERT INTO products (account_number, balance, type, user_id) VALUES
        ('40817810000000000009', 120000.00, 'ACCOUNT', user5_id),
        ('40817810000000000010', 30000.00, 'CARD', user5_id),
        ('40817810000000000011', 15000.00, 'CARD', user5_id);
END $$;