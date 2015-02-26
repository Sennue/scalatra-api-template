/*
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
*/

import com.sennue.api_template.ConfiguredPostgresDriver.simple._

//object Tables {
//  class Message(tag: Tag) extends Table[)](tag, "message")
//}

