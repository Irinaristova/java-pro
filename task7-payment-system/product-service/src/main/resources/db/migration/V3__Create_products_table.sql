CREATE TYPE product_type AS ENUM ('CARD', 'ACCOUNT');

CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    balance DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    type product_type NOT NULL,
    user_id BIGINT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_products_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_products_user_id ON products(user_id);
CREATE INDEX IF NOT EXISTS idx_products_account_number ON products(account_number);
CREATE INDEX IF NOT EXISTS idx_products_type ON products(type);
CREATE INDEX IF NOT EXISTS idx_products_is_active ON products(is_active);

COMMENT ON TABLE products IS 'Таблица продуктов клиентов (счета и карты)';
COMMENT ON COLUMN products.id IS 'Уникальный идентификатор продукта';
COMMENT ON COLUMN products.account_number IS 'Номер счета (уникальный)';
COMMENT ON COLUMN products.balance IS 'Текущий баланс продукта';
COMMENT ON COLUMN products.type IS 'Тип продукта: CARD (карта) или ACCOUNT (счет)';
COMMENT ON COLUMN products.user_id IS 'Владелец продукта (ссылка на users.id)';
COMMENT ON COLUMN products.is_active IS 'Флаг активности продукта';
COMMENT ON COLUMN products.created_at IS 'Дата создания продукта';
COMMENT ON COLUMN products.updated_at IS 'Дата последнего обновления';