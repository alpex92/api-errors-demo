package ru.tinkoff.example

import akka.http.scaladsl.server.Directives.{complete => akkaComplete}
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.server.Route
import cats.~>
import ru.tinkoff.tschema.akkaHttp.Routable

import scala.concurrent.Future
import scala.language.higherKinds

package object typedschema {
  //noinspection ConvertExpressionToSAM
  implicit def taglessRoutable[F[_], A: ToResponseMarshaller](
      implicit fToFuture: F ~> Future
  ): Routable[F[A], A] =
    new Routable[F[A], A] {
      override def route(res: => F[A]): Route = akkaComplete(fToFuture(res))
    }
}
