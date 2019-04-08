package ru.tinkoff.example.web.openapi

import akka.http.scaladsl.server.{Directives, Route}
import cats.instances.vector._
import cats.syntax.foldable._
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import io.circe.Printer

import ru.tinkoff.example.web.Module
import ru.tinkoff.tschema.swagger.OpenApiInfo

class SwaggerSchema(
    info: OpenApiInfo,
    modules: Vector[Module],
    servers: Seq[(String, Option[String])]
) extends Directives {

  private val swagger = {
    val openApi = modules
      .foldMap(_.swagger)
      .make(info)
      .copy(tags = modules.foldMap(_.tagInfo))

    servers.foldLeft(openApi) {
      case (oa, server) =>
        oa.addServer(server._1, server._2)
    }
  }

  private implicit val swaggerCircePrinter: Printer =
    Printer.noSpaces.copy(dropNullValues = true)

  val route: Route = complete(swagger)
}
