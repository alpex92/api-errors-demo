package ru.tinkoff.example

import java.time.Instant

import io.getquill._

import ru.tinkoff.example.sd.model.AppStatus

package object db {

  implicit val instantEncoding = MappedEncoding[Instant, String](_.toString)
  implicit val instantDecoding = MappedEncoding[String, Instant](txt => Instant.parse(txt))

  implicit val appStatusEncoding = MappedEncoding[AppStatus, String](_.value)
  implicit val appStatusDecoding = MappedEncoding[String, AppStatus](name => AppStatus.valuesToEntriesMap(name))
}
