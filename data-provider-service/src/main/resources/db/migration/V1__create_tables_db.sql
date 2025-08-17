-- Пользователи системы
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    role VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Информация о пользователе Telegram
CREATE TABLE IF NOT EXISTS telegram_users (
    id BIGINT PRIMARY KEY, -- Telegram user id
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    user_id BIGINT REFERENCES users(id) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    username VARCHAR(255)
);

-- Информация о чате Telegram
CREATE TABLE IF NOT EXISTS telegram_chats (
    id BIGINT PRIMARY KEY, -- Telegram chat id
    telegram_user_id BIGINT REFERENCES telegram_users(id) NOT NULL,
    ui_element VARCHAR(255),
    ui_element_value VARCHAR(255),
    chat_state VARCHAR(255)
);

-- Сессии пользователя Telegram
CREATE TABLE IF NOT EXISTS telegram_sessions (
    id BIGSERIAL PRIMARY KEY,
    telegram_user_id BIGINT REFERENCES telegram_users(id) NOT NULL,
    chat_id BIGINT REFERENCES telegram_chats(id) NOT NULL,
    session_data VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    expiration_time TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Элементы меню верхнего уровня
CREATE TABLE IF NOT EXISTS top_level_menus (
    id BIGSERIAL PRIMARY KEY,
    command_key VARCHAR(255) UNIQUE NOT NULL,
    command_name VARCHAR(255) NOT NULL
);

-- Элементы подменю
CREATE TABLE IF NOT EXISTS sub_level_menus (
    id BIGSERIAL PRIMARY KEY,
    top_level_menu_id BIGINT REFERENCES top_level_menus(id) NOT NULL,
    command_key VARCHAR(255) UNIQUE NOT NULL,
    command_name VARCHAR(255) NOT NULL
);

-- Элементы меню нижнего уровня
CREATE TABLE IF NOT EXISTS leaf_level_menus (
    id BIGSERIAL PRIMARY KEY,
    sub_level_menu_id BIGINT REFERENCES sub_level_menus(id) NOT NULL,
    command_key VARCHAR(255) UNIQUE NOT NULL,
    command_name VARCHAR(255) NOT NULL
);