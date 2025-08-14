-- Уникальный индекс для email в таблице users
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email ON users (email);

-- Индекс для telegram_user_id в таблице telegram_chats
CREATE INDEX IF NOT EXISTS idx_telegram_chats_telegram_user_id ON telegram_chats (telegram_user_id);

-- Индекс для top_level_menu_id в таблице sub_level_menus
CREATE INDEX IF NOT EXISTS idx_sub_level_menus_top_level_menu_id ON sub_level_menus (top_level_menu_id);

-- Индекс для sub_level_menu_id в таблице leaf_level_menus
CREATE INDEX IF NOT EXISTS idx_leaf_level_menus_sub_level_menu_id ON leaf_level_menus (sub_level_menu_id);

-- Индекс для telegram_user_id в таблице telegram_sessions
CREATE INDEX IF NOT EXISTS idx_telegram_sessions_telegram_user_id ON telegram_sessions (telegram_user_id);

-- Индекс для chat_id в таблице telegram_sessions
CREATE INDEX IF NOT EXISTS idx_telegram_sessions_chat_id ON telegram_sessions (chat_id);