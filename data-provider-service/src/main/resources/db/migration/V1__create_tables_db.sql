-- Пользователи системы
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    role VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS user_details (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    birthday TIMESTAMP WITH TIME ZONE,
    phone_number VARCHAR(50),
    gender VARCHAR(50),
    avatar_link VARCHAR(255),
    location VARCHAR(255),
    date_start_studying_school TIMESTAMP WITH TIME ZONE,
    curator VARCHAR(255)
);

CREATE TABLE verification_codes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) NOT NULL,
    otp_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    attempts INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Информация о пользователе Telegram
CREATE TABLE IF NOT EXISTS telegram_users (
    id BIGINT PRIMARY KEY, -- Telegram user id
    username VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    user_id BIGINT REFERENCES users(id) NOT NULL
);

-- Информация о чате Telegram
CREATE TABLE IF NOT EXISTS telegram_chats (
    id BIGSERIAL PRIMARY KEY,
    telegram_chat_id BIGINT,
    telegram_user_id BIGINT REFERENCES telegram_users(id) NOT NULL,
    ui_element VARCHAR(255),
    ui_element_value VARCHAR(255),
    chat_state VARCHAR(255)
);

-- Долгосрочная сессия
CREATE TABLE IF NOT EXISTS persistent_sessions (
    id BIGSERIAL PRIMARY KEY,
    telegram_session_id BIGINT NOT NULL,
    persistent_session_data VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Краткосрочная сессия
CREATE TABLE IF NOT EXISTS transient_sessions (
    id BIGSERIAL PRIMARY KEY,
    telegram_session_id BIGINT NOT NULL,
    transient_session_data VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Сессии пользователя Telegram
CREATE TABLE IF NOT EXISTS telegram_sessions (
    id BIGSERIAL PRIMARY KEY,
    telegram_user_id BIGINT REFERENCES telegram_users(id) NOT NULL,
    telegram_chat_id BIGINT REFERENCES telegram_chats(id) NOT NULL,
    persistent_session_id BIGINT,
    transient_session_id BIGINT
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