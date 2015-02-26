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

package com.sennue.api_template

import com.sennue.api_template.ConfiguredPostgresDriver.simple._
import java.sql.Timestamp

package object models {
  case class Message(
    id: Int,
    userId: String,
    timestamp: Timestamp,
    active: Boolean,
    username: String,
    message: String
  )

  case class MessagePost(
    //id: Option[Int],
    userId: String,
    //timestamp: Option[Timestamp],
    //active: Option[Boolean],
    username: String,
    message: String
  )

  class MessageTable(tag: Tag) extends Table[Message](tag, "") {
    def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
    def userId = column[String]("user_id")
    def timestamp = column[Timestamp]("timestamp")
    def active = column[Boolean]("active")
    def username = column[String]("username")
    def message = column[String]("message")

    def * = (id, userId, timestamp, active, username, message) <> (Message.tupled, Message.unapply)
  }

  object messages extends TableQuery(new MessageTable(_)) {
    def byId(ids: Int*) = messages
      .filter(_.id inSetBind ids)
      .map(t => t)
  }
}

