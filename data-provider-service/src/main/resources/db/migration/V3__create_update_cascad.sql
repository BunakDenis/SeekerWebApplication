-- Обновляем внешний ключ в таблице telegram_users
ALTER TABLE telegram_users
DROP CONSTRAINT IF EXISTS telegram_users_user_id_fkey;

ALTER TABLE telegram_users
ADD CONSTRAINT telegram_users_user_id_fkey
FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE;

-- Обновляем внешний ключ в таблице telegram_chats
ALTER TABLE telegram_chats
DROP CONSTRAINT IF EXISTS telegram_chats_telegram_user_id_fkey;

ALTER TABLE telegram_chats
ADD CONSTRAINT telegram_chats_telegram_user_id_fkey
FOREIGN KEY (telegram_user_id) REFERENCES telegram_users(id) ON UPDATE CASCADE;

-- Обновляем внешний ключ в таблице telegram_sessions
ALTER TABLE telegram_sessions
DROP CONSTRAINT IF EXISTS telegram_sessions_telegram_user_id_fkey;

ALTER TABLE telegram_sessions
ADD CONSTRAINT telegram_sessions_telegram_user_id_fkey
FOREIGN KEY (telegram_user_id) REFERENCES telegram_users(id) ON UPDATE CASCADE;

ALTER TABLE telegram_sessions
DROP CONSTRAINT IF EXISTS telegram_sessions_chat_id_fkey;

ALTER TABLE telegram_sessions
ADD CONSTRAINT telegram_sessions_chat_id_fkey
FOREIGN KEY (chat_id) REFERENCES telegram_chats(id) ON UPDATE CASCADE;

-- Обновляем внешний ключ в таблице sub_level_menus
ALTER TABLE sub_level_menus
DROP CONSTRAINT IF EXISTS sub_level_menus_top_level_menu_id_fkey;

ALTER TABLE sub_level_menus
ADD CONSTRAINT sub_level_menus_top_level_menu_id_fkey
FOREIGN KEY (top_level_menu_id) REFERENCES top_level_menus(id) ON UPDATE CASCADE;

-- Обновляем внешний ключ в таблице leaf_level_menus
ALTER TABLE leaf_level_menus
DROP CONSTRAINT IF EXISTS leaf_level_menus_sub_level_menu_id_fkey;

ALTER TABLE leaf_level_menus
ADD CONSTRAINT leaf_level_menus_sub_level_menu_id_fkey
FOREIGN KEY (sub_level_menu_id) REFERENCES sub_level_menus(id) ON UPDATE CASCADE;