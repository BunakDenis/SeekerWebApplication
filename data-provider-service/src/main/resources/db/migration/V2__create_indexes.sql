-- Уникальный индекс для email в таблице users
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email ON users (email);

-- Индекс для user_id в таблице user_details
CREATE INDEX IF NOT EXISTS idx_user_details_user_id ON user_details (user_id);

-- Индекс для user_id в таблице verification_codes
CREATE INDEX IF NOT EXISTS idx_verification_codes_user_id ON verification_codes (user_id);

-- Индекс для user_id в таблице telegram_users
CREATE INDEX IF NOT EXISTS idx_telegram_users_user_id ON telegram_users (user_id);

-- Индекс для telegram_user_id в таблице telegram_users
CREATE INDEX IF NOT EXISTS idx_telegram_users_telegram_user_id ON telegram_users (telegram_user_id);

-- Индекс для telegram_chat_id в таблице telegram_chats
CREATE INDEX IF NOT EXISTS idx_telegram_chats_telegram_chat_id ON telegram_chats (telegram_chat_id);

-- Индекс для telegram_user_id в таблице telegram_chats
CREATE INDEX IF NOT EXISTS idx_telegram_chats_telegram_user_id ON telegram_chats (telegram_user_id);

-- Индекс для telegram_user_id в таблице telegram_sessions
CREATE INDEX IF NOT EXISTS idx_telegram_sessions_telegram_user_id ON telegram_sessions (telegram_user_id);

-- Индекс для telegram_session_id в таблице persistent_sessions
CREATE INDEX IF NOT EXISTS idx_persistent_sessions_telegram_session_id ON persistent_sessions (telegram_session_id);

-- Индекс для telegram_session_id в таблице transient_sessions
CREATE INDEX IF NOT EXISTS idx_transient_sessions_telegram_session_id ON transient_sessions (telegram_session_id);

-- Индекс для top_level_menu_id в таблице sub_level_menus
CREATE INDEX IF NOT EXISTS idx_sub_level_menus_top_level_menu_id ON sub_level_menus (top_level_menu_id);

-- Индекс для sub_level_menu_id в таблице leaf_level_menus
CREATE INDEX IF NOT EXISTS idx_leaf_level_menus_sub_level_menu_id ON leaf_level_menus (sub_level_menu_id);