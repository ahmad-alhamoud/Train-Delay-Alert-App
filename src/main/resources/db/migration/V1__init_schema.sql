--  (Users)
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       telegram_chat_id VARCHAR(255) NOT NULL UNIQUE,
                       username VARCHAR(255),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--  (Subscriptions)
CREATE TABLE subscriptions (
                               id BIGSERIAL PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               train_number VARCHAR(50) NOT NULL,
                               departure_station VARCHAR(100) NOT NULL,
                               arrival_station VARCHAR(100) NOT NULL,
                               is_active BOOLEAN DEFAULT TRUE,
                               CONSTRAINT fk_subscription_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);