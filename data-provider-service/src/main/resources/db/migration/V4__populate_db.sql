-- V4__populate_db.sql
-- Только наполнение данными (идемпотентно, без правки схемы)

-- 1) Вставляем основных пользователей (если уже есть — не трогаем)
INSERT INTO users (username, password, email, role, is_active)
VALUES
  ('dbunak', '$2a$12$7aAytGGjXfLWyZuP.82HH.vKGoOwB2/CyAIN24EB5gG6mbTnVOCj6', 'xisi926@ukr.net', 'ADMIN', TRUE),
  ('tourist', '$2a$12$nIJtokJ7mstpyw.p/dQgSuNK9NLzo8bfOdIe8cSxjJOVhAKuskfq.', 'tourist@gmail.com', 'GUEST', TRUE),
  ('telegram-bot-service', '$2a$12$BDBWx0rCdFlBdfoJ9XCd/OpX3m4zNnBTpFmOUMkFJPG0G/D9LBmue', 'benzzin123@gmail.com', 'ADMIN', TRUE)
ON CONFLICT (email) DO NOTHING;


-- 2) Для связанных записей используем PL/pgSQL блок: получим внутренний PK и вставим только если запись отсутствует
DO $$
DECLARE
    d_user_id BIGINT;
    tu_id BIGINT;
BEGIN
    -- Получаем внутренний id пользователя "dbunak" по email
    SELECT id INTO d_user_id FROM users WHERE email = 'xisi926@ukr.net' LIMIT 1;
    IF d_user_id IS NULL THEN
        RAISE NOTICE 'User with email xisi926@ukr.net not found — skipping related inserts';
        RETURN;
    END IF;

    -- Вставляем user_details только если для данного user_id ещё нет записи
    IF NOT EXISTS (SELECT 1 FROM user_details WHERE user_id = d_user_id) THEN
        INSERT INTO user_details (
            user_id, first_name, last_name, birthday, phone_number, gender, location, date_start_studying_school, curator
        ) VALUES (
            d_user_id,
            'Денис',
            'Бунак',
            '1990-10-14'::date,
            '+380664988869',
            'MALE',
            'Украина, г. Чернигов',
            '2020-02-11'::date,
            'Алексей Киселёв'
        );
    ELSE
        -- Не изменяем существующую запись (чисто заполнение)
        RAISE NOTICE 'user_details for user_id % already exists — not inserting', d_user_id;
    END IF;

    -- Вставляем telegram_user (внешний telegram_user_id = 465963651) только если нет такой внешней записи
    IF NOT EXISTS (SELECT 1 FROM telegram_users WHERE telegram_user_id = 465963651) THEN
        INSERT INTO telegram_users (telegram_user_id, user_id, username, is_active)
        VALUES (465963651, d_user_id, 'dbunakns', TRUE);
    ELSE
        RAISE NOTICE 'telegram_users with telegram_user_id 465963651 already exists — not inserting';
    END IF;

    -- Получаем внутренний PK telegram_users.id для внешнего telegram_user_id
    SELECT id INTO tu_id FROM telegram_users WHERE telegram_user_id = 465963651 LIMIT 1;

    -- Если по какой-то причине не нашли — сообщим и выйдем
    IF tu_id IS NULL THEN
        RAISE NOTICE 'Не удалось получить internal id для telegram_user_id 465963651 — пропускаем вставку telegram_chats';
        RETURN;
    END IF;

    -- Вставляем telegram_chat только если нет chat с таким внешним telegram_chat_id
    IF NOT EXISTS (SELECT 1 FROM telegram_chats WHERE telegram_chat_id = 465963651) THEN
        INSERT INTO telegram_chats (telegram_chat_id, telegram_user_id, ui_element, ui_element_value, chat_state)
        VALUES (465963651, tu_id, 'command', '/authorize', 'enter_email');
    ELSE
        RAISE NOTICE 'telegram_chats with telegram_chat_id 465963651 already exists — not inserting';
    END IF;

END
$$;
