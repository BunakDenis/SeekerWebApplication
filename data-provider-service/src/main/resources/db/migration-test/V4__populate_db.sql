-- V4__populate_db_test.sql
-- Идемпотентное заполнение тестовой БД (только данные, без правки схемы)
-- Выполнять в PostgreSQL.

-- ============================
-- 1) Добавляем пользователей (по email). Если уже есть — ничего не делаем.
-- ============================
INSERT INTO users (username, password, email, role, is_active)
VALUES
  ('dbunak', '$2a$12$7aAytGGjXfLWyZuP.82HH.vKGoOwB2/CyAIN24EB5gG6mbTnVOCj6', 'xisi926@ukr.net', 'ADMIN', TRUE)
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (username, password, email, role, is_active)
VALUES
  ('tourist', '$2a$12$nIJtokJ7mstpyw.p/dQgSuNK9NLzo8bfOdIe8cSxjJOVhAKuskfq.', 'tourist@gmail.com', 'GUEST', TRUE)
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (username, password, email, role, is_active)
VALUES
  ('telegram-bot-service', '$2a$12$BDBWx0rCdFlBdfoJ9XCd/OpX3m4zNnBTpFmOUMkFJPG0G/D9LBmue', 'benzzin123@gmail.com', 'ADMIN', TRUE)
ON CONFLICT (email) DO NOTHING;


-- ============================
-- 2) Для dbunak: вставляем user_details, telegram_user и telegram_chat только если их ещё нет
-- ============================

-- user_details для dbunak (вставится только при отсутствии записи для данного user_id)
INSERT INTO user_details (
    user_id, first_name, last_name, birthday, phone_number, gender, location, date_start_studying_school, curator
)
SELECT u.id,
       'Денис',
       'Бунак',
       '1990-10-14'::date,
       '+380664988869',
       'MALE',
       'Украина, г. Чернигов',
       '2020-02-11'::date,
       'Алексей Киселёв'
FROM users u
WHERE u.email = 'xisi926@ukr.net'
  AND NOT EXISTS (
      SELECT 1 FROM user_details ud WHERE ud.user_id = u.id
  );

-- telegram_users для dbunak
INSERT INTO telegram_users (telegram_user_id, user_id, username, is_active)
SELECT 465963651, u.id, 'dbunakns', TRUE
FROM users u
WHERE u.email = 'xisi926@ukr.net'
  AND NOT EXISTS (
      SELECT 1 FROM telegram_users t WHERE t.telegram_user_id = 465963651
  );

-- telegram_chats для dbunak: используем PK из telegram_users
INSERT INTO telegram_chats (telegram_chat_id, telegram_user_id, ui_element, ui_element_value, chat_state)
SELECT 465963651, tu.id, '', '', ''
FROM telegram_users tu
JOIN users u ON tu.user_id = u.id
WHERE u.email = 'xisi926@ukr.net'
  AND tu.telegram_user_id = 465963651
  AND NOT EXISTS (
      SELECT 1 FROM telegram_chats tc WHERE tc.telegram_chat_id = 465963651
  );

-- telegram_session для dbunak: используем PK из telegram_users
INSERT INTO telegram_sessions (session_data, telegram_user_id)
SELECT '', tu.id
FROM telegram_users tu
JOIN users u ON tu.user_id = u.id
WHERE u.email = 'xisi926@ukr.net'
  AND tu.telegram_user_id = 465963651
  AND NOT EXISTS (
      SELECT 1 FROM telegram_sessions tc WHERE tc.telegram_user_id = 465963651
  );


-- ============================
-- 3) Для tourist: аналогично
-- ============================

-- user_details для tourist
INSERT INTO user_details (
    user_id, first_name, last_name, birthday, phone_number, gender, location, date_start_studying_school, curator
)
SELECT u.id,
       'Василий',
       'Тёркин',
       '1980-09-20'::date,
       '+380555555555',
       'MALE',
       'Украина, г. Киев',
       '2020-02-11'::date,
       'Руслан Жуковец'
FROM users u
WHERE u.email = 'tourist@gmail.com'
  AND NOT EXISTS (
      SELECT 1 FROM user_details ud WHERE ud.user_id = u.id
  );

-- telegram_users для tourist
INSERT INTO telegram_users (telegram_user_id, user_id, username, is_active)
SELECT 55555, u.id, 'vterk', TRUE
FROM users u
WHERE u.email = 'tourist@gmail.com'
  AND NOT EXISTS (
      SELECT 1 FROM telegram_users t WHERE t.telegram_user_id = 55555
  );

-- telegram_chats для tourist
INSERT INTO telegram_chats (telegram_chat_id, telegram_user_id, ui_element, ui_element_value, chat_state)
SELECT 55555, tu.id, '', '', ''
FROM telegram_users tu
JOIN users u ON tu.user_id = u.id
WHERE u.email = 'tourist@gmail.com'
  AND tu.telegram_user_id = 55555
  AND NOT EXISTS (
      SELECT 1 FROM telegram_chats tc WHERE tc.telegram_chat_id = 55555
  );

-- telegram_sessions для tourist
INSERT INTO telegram_sessions (session_data, telegram_user_id)
SELECT '', tu.id
FROM telegram_users tu
JOIN users u ON tu.user_id = u.id
WHERE u.email = 'tourist@gmail.com'
  AND tu.telegram_user_id = 55555
  AND NOT EXISTS (
      SELECT 1 FROM telegram_sessions tc WHERE tc.telegram_user_id = 55555
  );