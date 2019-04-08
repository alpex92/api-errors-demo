package ru.tinkoff.example.web

import akka.http.scaladsl.server.Route

import ru.tinkoff.tschema.swagger.{SwaggerBuilder, OpenApiTag => SwaggerTag}

trait Module {
  val routes: Route
  val swagger: SwaggerBuilder
  val tagInfo: Vector[SwaggerTag]
}
