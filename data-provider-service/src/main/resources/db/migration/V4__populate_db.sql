-- Вставляем пользователя
INSERT INTO users (username, password, email, role, is_active) VALUES ('dbunak', '$2a$12$7aAytGGjXfLWyZuP.82HH.vKGoOwB2/CyAIN24EB5gG6mbTnVOCj6', 'xisi926@ukr.net', 'ADMIN', TRUE);

-- Вставляем общего пользователя для туристов
INSERT INTO users (username, password, email, role, is_active) VALUES ('tourist', '$2a$12$nIJtokJ7mstpyw.p/dQgSuNK9NLzo8bfOdIe8cSxjJOVhAKuskfq.', 'tourist@gmail.com', 'GUEST', TRUE);

-- Получаем ID пользователя, чтобы использовать его в других таблицах
-- (Предполагается, что email уникален и используется для поиска)
-- ПРИМЕЧАНИЕ: В production-среде используйте параметры prepared statements, чтобы избежать SQL-инъекций!
DO $$
DECLARE
    user_id BIGINT;
BEGIN
    SELECT id INTO user_id FROM users WHERE email = 'xisi926@ukr.net';

    -- Вставляем данные дополнительные данные о пользователе
    INSERT INTO user_details (user_id, first_name, last_name, birthday, phone_number, gender, location, date_start_studying_school, curator)
    VALUES (user_id, 'Денис', 'Бунак', '1990-10-14 00:00:00+02', '+380664988869', 'MALE', 'Украина, г. Чернигов', '2020-02-11 00:00:00+02', 'Алексей Киселёв');

    -- Вставляем данные о пользователе Telegram
    INSERT INTO telegram_users (id, user_id, username, is_active) VALUES (465963651, user_id, 'dbunakns', 'TRUE');

    -- Вставляем данные о чате Telegram
    INSERT INTO telegram_chats (id, telegram_user_id, ui_element, ui_element_value, chat_state)
    VALUES (465963651, 465963651, '', '', '');

    -- Вставляем данные о сессии Telegram
    INSERT INTO telegram_sessions (telegram_user_id, telegram_chat_id) VALUES (465963651, 465963651);

END $$;

-- Получаем ID сессии, чтобы использовать его в других таблицах
DO $$
DECLARE
    session_id BIGINT;
BEGIN
    SELECT id INTO session_id FROM telegram_sessions WHERE telegram_user_id = 465963651;

    -- Вставляем данные о долгосрочной сессии
    INSERT INTO persistent_sessions (telegram_session_id, persistent_session_data, persistent_expiration_time)
    VALUES (session_id, 'persistent_session_data', '2026-08-29 00:00:00+02');

    -- Вставляем данные о краткосрочной сессии
    INSERT INTO transient_sessions (telegram_session_id, transient_session_data, transient_expiration_time)
    VALUES (session_id, 'transient_session_data', '2025-09-29 00:00:00+02');

END $$;
