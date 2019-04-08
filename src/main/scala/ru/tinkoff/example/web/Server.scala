package ru.tinkoff.example.web

import akka.http.scaladsl.server.Route
import cats.syntax.option._

import ru.tinkoff.example.web.openapi.{SwaggerSchema, SwaggerUI, SwaggerUISettings}
import ru.tinkoff.tschema.swagger.OpenApiInfo

class Server(modules: Module*) extends SwaggerUI {

  private val info = OpenApiInfo(
    "Service Desk",
    "API для работы с заявками".some,
    version = "1.0"
  )

  private val swaggerSchema = new SwaggerSchema(info, modules.toVector, Seq.empty)
  private val swaggerUISettings = SwaggerUISettings(info.title, "/api/sd/v1", "/schema/swagger")

  val routes: Route = pathPrefix("api" / "sd" / "v1") {
      swaggerUIRoute(swaggerUISettings) ~ pathPrefix("schema" / "swagger")(swaggerSchema.route)
    } ~ modules.map(_.routes).reduce(_ ~ _)
}
