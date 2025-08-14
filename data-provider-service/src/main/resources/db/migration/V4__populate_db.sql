-- Вставляем пользователей
INSERT INTO users (email, is_active) VALUES ('xisi926@ukr.net', TRUE);

-- Получаем ID пользователя, чтобы использовать его в других таблицах
-- (Предполагается, что email уникален и используется для поиска)
-- ПРИМЕЧАНИЕ: В production-среде используйте параметры prepared statements, чтобы избежать SQL-инъекций!
DO $$
DECLARE
    user_id BIGINT;
BEGIN
    SELECT id INTO user_id FROM users WHERE email = 'xisi926@ukr.net';

    -- Вставляем данные о пользователе Telegram
    INSERT INTO telegram_users (id, user_id, first_name, last_name, username) VALUES (465963651, user_id, 'Denis', 'Kachur', 'Freeman');

    -- Вставляем данные о чате Telegram
    INSERT INTO telegram_chats (id, telegram_user_id, ui_element, ui_element_value, chat_state)
    VALUES (465963651, 465963651, 'command', '/auth', 'enter_email');

    -- Вставляем данные о сессии Telegram
    INSERT INTO telegram_sessions (telegram_user_id, chat_id, expiration_time) VALUES (465963651, 465963651, '2026-08-07 10:23:54+02');

END $$;