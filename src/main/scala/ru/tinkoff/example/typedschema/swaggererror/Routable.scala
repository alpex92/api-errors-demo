package ru.tinkoff.example.typedschema.swaggererror
import scala.concurrent.Future

import akka.http.scaladsl.marshalling.ToEntityMarshaller
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.{Directives, Route}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import shapeless.ops.coproduct.{Mapper, Unifier}
import shapeless.{Coproduct, Poly1}
import ru.tinkoff.tschema.akkaHttp.{Routable => TypedSchemaRoutable}

object Routable extends FailFastCirceSupport {

  implicit def mKErrorRouteable[L <: Coproduct, M <: Coproduct, R](
      implicit
      mapper: Mapper.Aux[ErrorMappingPoly.type, L, M],
      unifier: Unifier.Aux[M, Route],
      successToRoute: R => Route
  ): TypedSchemaRoutable[Future[Either[L, R]], Either[L, R]] = { res =>
    Directives.onSuccess(res) {
      _.fold(_.fold(ErrorMappingPoly), successToRoute)
    }
  }

  private[Routable] object ErrorMappingPoly extends Poly1 {
    implicit def default[T: ToApiErrorWithCode] = at[T](errorToRoute)
  }

  implicit def errorToRoute[R: ToApiErrorWithCode]: R => Route = { r =>
    complete(
      ToApiErrorWithCode[R].statusCode,
      ToApiErrorWithCode[R].toApiError(r)
    )
  }

  implicit def successToRoute[R: ToEntityMarshaller]: R => Route = { r =>
    complete(r)
  }
}
