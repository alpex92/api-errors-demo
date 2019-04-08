package ru.tinkoff.example.web

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import cats.syntax.option._

import ru.tinkoff.example.web.openapi.{SwaggerSchema, SwaggerUI, SwaggerUISettings}
import ru.tinkoff.example.web.sd.BrokerAppModule
import ru.tinkoff.tschema.swagger.OpenApiInfo

object Server extends App with SwaggerUI {

  private val info = OpenApiInfo(
    "Service Desk",
    "API для работы с заявками".some,
    version = "1.0"
  )

  // url -> description
  private val servers: Seq[(String, Option[String])] = Seq("" -> None)

  private val modules: Vector[Module] = Vector(BrokerAppModule)

  private val swaggerSchema = new SwaggerSchema(info, modules, servers)
  private val swaggerUISettings = SwaggerUISettings(info.title, "/api/sd/v1", "/schema/swagger")

  val routes: Route = pathPrefix("api" / "sd" / "v1") {
      swaggerUIRoute(swaggerUISettings) ~ pathPrefix("schema" / "swagger")(swaggerSchema.route)
    } ~ modules.map(_.routes).reduce(_ ~ _)

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  Http().bindAndHandle(routes, "0.0.0.0", 9991)
}
