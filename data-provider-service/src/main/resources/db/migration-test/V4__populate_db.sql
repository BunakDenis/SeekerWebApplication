-- V4__populate_db_test.sql
-- Идемпотентное заполнение тестовой БД (только данные, без правки схемы)
-- Выполнять в PostgreSQL.

-- ============================
-- 1) Добавляем пользователей (по email). Если уже есть — ничего не делаем.
-- ============================
INSERT INTO users (username, password, email, role, active)
VALUES
  ('dbunak', '$2a$12$7aAytGGjXfLWyZuP.82HH.vKGoOwB2/CyAIN24EB5gG6mbTnVOCj6', 'xisi926@ukr.net', 'ADMIN', TRUE),
  ('tourist', '$2a$12$nIJtokJ7mstpyw.p/dQgSuNK9NLzo8bfOdIe8cSxjJOVhAKuskfq.', 'tourist@gmail.com', 'GUEST', TRUE),
  ('telegram-bot-service', '$2a$12$BDBWx0rCdFlBdfoJ9XCd/OpX3m4zNnBTpFmOUMkFJPG0G/D9LBmue', 'benzzin123@gmail.com', 'ADMIN', TRUE)
ON CONFLICT (email) DO NOTHING;


-- ============================
-- 2) Для пользователя dbunak
-- ============================
DO $$
DECLARE
    d_user_id BIGINT;
    tu_id BIGINT;
BEGIN
    SELECT id INTO d_user_id FROM users WHERE email = 'xisi926@ukr.net' LIMIT 1;
    IF d_user_id IS NULL THEN
        RAISE NOTICE 'User dbunak not found — skipping inserts';
        RETURN;
    END IF;

    -- user_details
    IF NOT EXISTS (SELECT 1 FROM user_details WHERE user_id = d_user_id) THEN
        INSERT INTO user_details (
            user_id, first_name, last_name, birthday, phone_number, gender, location, date_start_studying_school
        ) VALUES (
            d_user_id,
            'Денис',
            'Бунак',
            '1990-10-14'::date,
            '+380664988869',
            'MALE',
            'Украина, г. Чернигов',
            '2020-02-11'::date
        );
    ELSE
        RAISE NOTICE 'user_details for dbunak already exists';
    END IF;

    -- telegram_users
    IF NOT EXISTS (SELECT 1 FROM telegram_users WHERE telegram_user_id = 465963651) THEN
        INSERT INTO telegram_users (telegram_user_id, user_id, username, active)
        VALUES (465963651, d_user_id, 'dbunakns', TRUE);
    ELSE
        RAISE NOTICE 'telegram_user for dbunak already exists';
    END IF;

    -- telegram_chats
    SELECT id INTO tu_id FROM telegram_users WHERE telegram_user_id = 465963651 LIMIT 1;
    IF tu_id IS NOT NULL THEN
        IF NOT EXISTS (SELECT 1 FROM telegram_chats WHERE telegram_chat_id = 465963651) THEN
            INSERT INTO telegram_chats (telegram_chat_id, telegram_user_id, ui_element, ui_element_value, chat_state)
            VALUES (465963651, tu_id, '', '', '');
        END IF;

        IF NOT EXISTS (SELECT 1 FROM telegram_sessions WHERE telegram_user_id = tu_id) THEN
            INSERT INTO telegram_sessions (session_data, telegram_user_id)
            VALUES ('', tu_id);
        END IF;
    ELSE
        RAISE NOTICE 'telegram_user for dbunak not found — skipping chat/session inserts';
    END IF;

END
$$;


-- ============================
-- 3) Для пользователя tourist
-- ============================
DO $$
DECLARE
    t_user_id BIGINT;
    tu_id BIGINT;
BEGIN
    SELECT id INTO t_user_id FROM users WHERE email = 'tourist@gmail.com' LIMIT 1;
    IF t_user_id IS NULL THEN
        RAISE NOTICE 'User tourist not found — skipping inserts';
        RETURN;
    END IF;

    -- user_details
    IF NOT EXISTS (SELECT 1 FROM user_details WHERE user_id = t_user_id) THEN
        INSERT INTO user_details (
            user_id, first_name, last_name, birthday, phone_number, gender, location, date_start_studying_school
        ) VALUES (
            t_user_id,
            'Василий',
            'Тёркин',
            '1980-09-20'::date,
            '+380555555555',
            'MALE',
            'Украина, г. Киев',
            '2020-02-11'::date
        );
    ELSE
        RAISE NOTICE 'user_details for tourist already exists';
    END IF;

    -- telegram_users
    IF NOT EXISTS (SELECT 1 FROM telegram_users WHERE telegram_user_id = 55555) THEN
        INSERT INTO telegram_users (telegram_user_id, user_id, username, active)
        VALUES (55555, t_user_id, 'vterk', TRUE);
    ELSE
        RAISE NOTICE 'telegram_user for tourist already exists';
    END IF;

    -- telegram_chats и sessions
    SELECT id INTO tu_id FROM telegram_users WHERE telegram_user_id = 55555 LIMIT 1;
    IF tu_id IS NOT NULL THEN
        IF NOT EXISTS (SELECT 1 FROM telegram_chats WHERE telegram_chat_id = 55555) THEN
            INSERT INTO telegram_chats (telegram_chat_id, telegram_user_id, ui_element, ui_element_value, chat_state)
            VALUES (55555, tu_id, '', '', '');
        END IF;

        IF NOT EXISTS (SELECT 1 FROM telegram_sessions WHERE telegram_user_id = tu_id) THEN
            INSERT INTO telegram_sessions (session_data, telegram_user_id)
            VALUES ('', tu_id);
        END IF;
    ELSE
        RAISE NOTICE 'telegram_user for tourist not found — skipping chat/session inserts';
    END IF;
END
$$;
