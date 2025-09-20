-- V4__populate_db.sql (idempotent)

-- 1) Добавляем пользователей (если пользователь с таким email уже есть — ничего не делаем)
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


-- 2) Вставляем дополнительные данные, используя SELECT ... WHERE NOT EXISTS — не требует уникальных ограничений

-- Для пользователя dbunak: user_details
INSERT INTO user_details (user_id, first_name, last_name, birthday, phone_number, gender, location, date_start_studying_school, curator)
SELECT u.id,
       'Денис',
       'Бунак',
       '1990-10-14 00:00:00+02',
       '+380664988869',
       'MALE',
       'Украина, г. Чернигов',
       '2020-02-11 00:00:00+02',
       'Алексей Киселёв'
FROM users u
WHERE u.email = 'xisi926@ukr.net'
  AND NOT EXISTS (
      SELECT 1 FROM user_details ud WHERE ud.user_id = u.id
  );

-- telegram_users для dbunak (id — PK, поэтому проверяем по id)
INSERT INTO telegram_users (id, user_id, username, is_active)
SELECT 465963651, u.id, 'dbunakns', TRUE
FROM users u
WHERE u.email = 'xisi926@ukr.net'
  AND NOT EXISTS (
      SELECT 1 FROM telegram_users t WHERE t.id = 465963651
  );

-- telegram_chats для dbunak (проверяем по telegram_chat_id, не требуем уникального индекса)
INSERT INTO telegram_chats (telegram_chat_id, telegram_user_id, ui_element, ui_element_value, chat_state)
SELECT 465963651, 465963651, '', '', ''
WHERE NOT EXISTS (
    SELECT 1 FROM telegram_chats tc WHERE tc.telegram_chat_id = 465963651
);


-- Для пользователя tourist: user_details
INSERT INTO user_details (user_id, first_name, last_name, birthday, phone_number, gender, location, date_start_studying_school, curator)
SELECT u.id,
       'Василий',
       'Тёркин',
       '1980-09-20 00:00:00+02',
       '+380555555555',
       'MALE',
       'Украина, г. Киев',
       '2020-02-11 00:00:00+02',
       'Руслан Жуковец'
FROM users u
WHERE u.email = 'tourist@gmail.com'
  AND NOT EXISTS (
      SELECT 1 FROM user_details ud WHERE ud.user_id = u.id
  );

-- telegram_users для tourist
INSERT INTO telegram_users (id, user_id, username, is_active)
SELECT 55555, u.id, 'vterk', TRUE
FROM users u
WHERE u.email = 'tourist@gmail.com'
  AND NOT EXISTS (
      SELECT 1 FROM telegram_users t WHERE t.id = 55555
  );

-- telegram_chats для tourist
INSERT INTO telegram_chats (telegram_chat_id, telegram_user_id, ui_element, ui_element_value, chat_state)
SELECT 55555, 55555, '', '', ''
WHERE NOT EXISTS (
    SELECT 1 FROM telegram_chats tc WHERE tc.telegram_chat_id = 55555
);