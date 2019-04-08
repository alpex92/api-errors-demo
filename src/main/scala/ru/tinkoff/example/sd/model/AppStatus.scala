package ru.tinkoff.example.sd.model

import enumeratum.values.{StringCirceEnum, StringEnum, StringEnumEntry}

import ru.tinkoff.tschema.swagger.SwaggerTypeable.SwaggerTypeableStringEnum

sealed abstract class AppStatus(val value: String) extends StringEnumEntry {
    override def toString: String = value
}

object AppStatus
  extends StringEnum[AppStatus]
    with StringCirceEnum[AppStatus]
    with SwaggerTypeableStringEnum[AppStatus] {

  case object New extends AppStatus("new")
  case object InProgress extends AppStatus("in_progress")
  case object Resolved extends AppStatus("fixed")
  case object Rejected extends AppStatus("rejected")

  val values = findValues
}
