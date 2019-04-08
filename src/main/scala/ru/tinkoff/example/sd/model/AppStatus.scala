package ru.tinkoff.example.sd.model

import io.circe.generic.JsonCodec
import org.manatki.derevo.derive
import org.manatki.derevo.tschemaInstances.swagger

@JsonCodec
@derive(swagger)
sealed abstract class AppStatus(val status: String)

object AppStatus {
  case object New extends AppStatus("new")
  case object InProgress extends AppStatus("in_progress")
  case object Fixed extends AppStatus("fixed")
  case class Rejected(cause: String) extends AppStatus("rejected")
}
