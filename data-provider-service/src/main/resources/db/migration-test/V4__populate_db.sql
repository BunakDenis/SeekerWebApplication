-- V4__populate_db_test.sql
-- Идемпотентное заполнение тестовой БД (только данные, без правки схемы)
-- PostgreSQL

-- ============================
-- 1) Добавляем пользователей
-- ============================
INSERT INTO users (username, password, email, role, active)
SELECT 'dbunak', '$2a$12$7aAytGGjXfLWyZuP.82HH.vKGoOwB2/CyAIN24EB5gG6mbTnVOCj6', 'xisi926@ukr.net', 'ADMIN', TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'xisi926@ukr.net');

INSERT INTO users (username, password, email, role, active)
SELECT 'tourist', '$2a$12$nIJtokJ7mstpyw.p/dQgSuNK9NLzo8bfOdIe8cSxjJOVhAKuskfq.', 'tourist@gmail.com', 'GUEST', TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'tourist@gmail.com');

INSERT INTO users (username, password, email, role, active)
SELECT 'telegram-bot-service', '$2a$12$BDBWx0rCdFlBdfoJ9XCd/OpX3m4zNnBTpFmOUMkFJPG0G/D9LBmue', 'benzzin123@gmail.com', 'ADMIN', TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'benzzin123@gmail.com');


-- ============================
-- 2) Добавляем куратора (dbunak)
-- ============================
DO $$
DECLARE
    v_user_id BIGINT;
    v_curator_id BIGINT;
BEGIN
    SELECT id INTO v_user_id FROM users WHERE email = 'xisi926@ukr.net' LIMIT 1;
    IF v_user_id IS NULL THEN
        RAISE NOTICE '⚠️ User xisi926@ukr.net not found — skipping curator insert';
        RETURN;
    END IF;

    -- Добавляем куратора, если ещё нет
    IF NOT EXISTS (SELECT 1 FROM curators WHERE user_id = v_user_id) THEN
        INSERT INTO curators (id, user_id) VALUES (55, v_user_id);
    END IF;

    SELECT id INTO v_curator_id FROM curators WHERE user_id = v_user_id LIMIT 1;

    -- Добавляем user_details
    IF NOT EXISTS (SELECT 1 FROM user_details WHERE user_id = v_user_id) THEN
        INSERT INTO user_details (
            user_id, first_name, last_name, birthday, phone_number, gender, location, date_start_studying_school
        )
        VALUES (
            v_user_id,
            'Денис', 'Бунак',
            '1990-10-14'::date,
            '+380664988869',
            'MALE',
            'Украина, г. Чернигов',
            '2020-02-11'::date
        );
    END IF;

    -- Telegram пользователя и связанные записи
    IF NOT EXISTS (SELECT 1 FROM telegram_users WHERE telegram_user_id = 465963651) THEN
        INSERT INTO telegram_users (telegram_user_id, user_id, username, active)
        VALUES (465963651, v_user_id, 'dbunakns', TRUE);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM telegram_chats WHERE telegram_chat_id = 465963651) THEN
        INSERT INTO telegram_chats (telegram_chat_id, telegram_user_id, ui_element, ui_element_value, chat_state)
        SELECT 465963651, tu.id, '', '', ''
        FROM telegram_users tu
        WHERE tu.telegram_user_id = 465963651;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM telegram_sessions ts JOIN telegram_users tu ON tu.id = ts.telegram_user_id
                   WHERE tu.telegram_user_id = 465963651) THEN
        INSERT INTO telegram_sessions (session_data, telegram_user_id)
        SELECT '', tu.id
        FROM telegram_users tu
        WHERE tu.telegram_user_id = 465963651;
    END IF;
END $$;


-- ============================
-- 3) Добавляем ученика (tourist)
-- ============================
DO $$
DECLARE
    v_user_id BIGINT;
    v_disciple_id BIGINT;
    v_curator_id BIGINT;
BEGIN
    SELECT id INTO v_user_id FROM users WHERE email = 'tourist@gmail.com' LIMIT 1;
    IF v_user_id IS NULL THEN
        RAISE NOTICE '⚠️ User tourist@gmail.com not found — skipping disciple insert';
        RETURN;
    END IF;

    SELECT c.id INTO v_curator_id
    FROM curators c
    JOIN users u ON c.user_id = u.id
    WHERE u.email = 'xisi926@ukr.net'
    LIMIT 1;

    IF v_curator_id IS NULL THEN
        RAISE NOTICE '⚠️ Curator not found for tourist@gmail.com — skipping disciple insert';
        RETURN;
    END IF;

    -- Добавляем ученика, если нет
    IF NOT EXISTS (SELECT 1 FROM disciples WHERE user_id = v_user_id) THEN
        INSERT INTO disciples (user_id, curator_id)
        VALUES (v_user_id, v_curator_id);
    END IF;

    SELECT id INTO v_disciple_id FROM disciples WHERE user_id = v_user_id LIMIT 1;

    -- Обновляем curator.disciple_id
    UPDATE curators
    SET disciple_id = v_disciple_id
    WHERE id = v_curator_id
      AND (disciple_id IS NULL OR disciple_id <> v_disciple_id);

    -- Добавляем user_details
    IF NOT EXISTS (SELECT 1 FROM user_details WHERE user_id = v_user_id) THEN
        INSERT INTO user_details (
            user_id, first_name, last_name, birthday, phone_number, gender, location, date_start_studying_school
        )
        VALUES (
            v_user_id,
            'Василий', 'Тёркин',
            '1980-09-20'::date,
            '+380555555555',
            'MALE',
            'Украина, г. Киев',
            '2020-02-11'::date
        );
    END IF;

    -- Telegram пользователя и связанные записи
    IF NOT EXISTS (SELECT 1 FROM telegram_users WHERE telegram_user_id = 55555) THEN
        INSERT INTO telegram_users (telegram_user_id, user_id, username, active)
        VALUES (55555, v_user_id, 'vterk', TRUE);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM telegram_chats WHERE telegram_chat_id = 55555) THEN
        INSERT INTO telegram_chats (telegram_chat_id, telegram_user_id, ui_element, ui_element_value, chat_state)
        SELECT 55555, tu.id, '', '', ''
        FROM telegram_users tu
        WHERE tu.telegram_user_id = 55555;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM telegram_sessions ts JOIN telegram_users tu ON tu.id = ts.telegram_user_id
                   WHERE tu.telegram_user_id = 55555) THEN
        INSERT INTO telegram_sessions (session_data, telegram_user_id)
        SELECT '', tu.id
        FROM telegram_users tu
        WHERE tu.telegram_user_id = 55555;
    END IF;
END $$;
