package ru.tinkoff.example.typedschema.typedsl

import akka.http.scaladsl.server.{Directives, Route}
import shapeless.HList

import ru.tinkoff.tschema.akkaHttp.Serve
import ru.tinkoff.tschema.swagger.SwaggerMapper
import ru.tinkoff.tschema.typeDSL.DSLAtom

final class handleErrors extends DSLAtom

object handleErrors {
  def apply(): handleErrors = new handleErrors
  implicit def handleErrorsServe[In <: HList](implicit handler: PartialFunction[Throwable, Route]) =
    Serve.serveCheck[handleErrors, In](Directives.handleExceptions(handler))
  implicit def handleErrorsMapper: SwaggerMapper[handleErrors] = SwaggerMapper.empty
}
