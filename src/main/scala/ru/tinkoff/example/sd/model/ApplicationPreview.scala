package ru.tinkoff.example.sd.model

import java.time.Instant

import io.circe.generic.JsonCodec
import org.manatki.derevo.derive
import org.manatki.derevo.tschemaInstances.swagger
import ru.tinkoff.example.typedschema.typeable._

@JsonCodec
@derive(swagger)
case class ApplicationCreate(title: String , description: String)

object ApplicationCreate

@JsonCodec
@derive(swagger)
case class ApplicationPreview(meta: AppMeta, title: String)

object ApplicationPreview

@JsonCodec
@derive(swagger)
case class Application(
  meta: AppMeta,
  title: String,
  description: String
)

object Application

@JsonCodec
@derive(swagger)
case class AppMeta(
  id: AppId,
  lastModified: Instant,
  status: AppStatus
)

object AppMeta

