package ru.tinkoff.example.web.sd

import scala.concurrent.Future
import scala.language.higherKinds

import akka.http.scaladsl.server.Route
import cats.{Monad, ~>}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport

import ru.tinkoff.example.sd.service.SDService
import ru.tinkoff.example.web.{Module, mkDescription, sd}
import ru.tinkoff.tschema.akkaHttp.MkRoute
import ru.tinkoff.tschema.swagger.{MkSwagger, OpenApiTag}

import ru.tinkoff.example.typedschema.typeable._
import ru.tinkoff.example.typedschema.swaggererror._
import ru.tinkoff.example.typedschema.swaggererror.Routable._
import ru.tinkoff.example.typedschema.swaggererror.ToApiErrorWithCode._
import ru.tinkoff.example.web._
import ru.tinkoff.example.web.sd.Errors._

class SDModule[F[_]: Monad]
(service: SDService[F])
(implicit fToFuture: F ~> Future) extends Module with FailFastCirceSupport {
  private val handler = new SDHandler[F](service)
  private val descriptions = mkDescription("sd")
  implicit val exceptionHandler: PartialFunction[Throwable, Route] = PartialFunction.empty
  override val routes: Route = MkRoute(sd.route)(handler)
  override val swagger: MkSwagger[_] = sd.route.mkSwagger.describe(descriptions)
  override val tagInfo: Vector[OpenApiTag] = Vector.empty
}
