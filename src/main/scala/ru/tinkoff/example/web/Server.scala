package ru.tinkoff.example.web

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import ru.tinkoff.tschema.swagger.OpenApiInfo
import ru.tinkoff.example.web.openapi.{
  SwaggerSchema,
  SwaggerUI,
  SwaggerUISettings
}

class Server(modules: Module*) extends SwaggerUI {
  import cats.syntax.option._

  private val info = OpenApiInfo(
    "Service Desk",
    "API для работы с заявками".some,
    version = "1.0"
  )

  private val swaggerSchema =
    new SwaggerSchema(info, modules.toVector, Seq.empty)
  private val swaggerUISettings =
    SwaggerUISettings(info.title, "/api/sd/v1", "/schema/swagger")

  val routes: Route = {
    pathEndOrSingleSlash {
      redirect("/api/sd/v1/swagger", StatusCodes.TemporaryRedirect)
    } ~ pathPrefix("api" / "sd" / "v1") {
      swaggerUIRoute(swaggerUISettings) ~ pathPrefix("schema" / "swagger")(
        swaggerSchema.route)
    } ~ modules.map(_.routes).reduce(_ ~ _)
  }
}
