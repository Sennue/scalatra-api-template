DROP TABLE IF EXISTS message;
CREATE TABLE IF NOT EXISTS message
(
  id SERIAL PRIMARY KEY,
  user_id TEXT NOT NULL,
  timestamp TIMESTAMP NOT NULL,
  active BOOLEAN DEFAULT TRUE,
  username TEXT DEFAULT 'anonymous',
  message TEXT NOT NULL
);

