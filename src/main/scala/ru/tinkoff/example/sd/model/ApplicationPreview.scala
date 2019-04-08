package ru.tinkoff.example.sd.model

import java.time.Instant
import java.util.UUID

import io.circe.generic.JsonCodec
import io.getquill.Embedded
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

object ApplicationPreview {
  def fromApp(app: Application) = ApplicationPreview(app.meta, app.title)
}

@JsonCodec
@derive(swagger)
case class Application(
  meta: AppMeta,
  title: String,
  description: String
)

object Application {
  def fromCreate(create: ApplicationCreate) = Application(
    AppMeta(UUID.randomUUID(), Instant.now(), AppStatus.New),
    create.title,
    create.description
  )
}

@JsonCodec
@derive(swagger)
case class AppMeta(
  id: AppId,
  lastModified: Instant,
  status: AppStatus
) extends Embedded

object AppMeta

