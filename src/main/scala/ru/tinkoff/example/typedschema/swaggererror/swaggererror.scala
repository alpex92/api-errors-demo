package ru.tinkoff.example.typedschema

import akka.http.scaladsl.model.StatusCode
import cats.Show
import cats.syntax.show._
import shapeless.{:+:, CNil, Coproduct}

import ru.tinkoff.tschema.swagger.{MkSwagger, SwaggerTypeable}
import ru.tinkoff.tschema.typeDSL.Complete

package object swaggererror {

  object SwaggerError {
    def apply[T](implicit se: SwaggerError[T]): SwaggerError[T] = se
  }

  trait SwaggerError[T] {
    def statusCode: StatusCode
    def description: String
    def apiErrorCode: Option[Int]
  }

  final case class SwaggerErrorVal[T](
      override val description: String,
      override val statusCode: StatusCode,
      override val apiErrorCode: Option[Int] = None
  ) extends SwaggerError[T]

  import ru.tinkoff.example.typedschema.swaggererror.Iterate.SwaggerErrorResult

  trait Iterate[С <: Coproduct] { def list: List[SwaggerErrorResult] }
  object Iterate {

    object SwaggerErrorResult {
      def apply[T](implicit se: SwaggerError[T]): SwaggerErrorResult =
        SwaggerErrorResult(se.statusCode, se.description, se.apiErrorCode)
    }
    case class SwaggerErrorResult(
        statusCode: StatusCode,
        description: String,
        apiErrorCode: Option[Int]
    )

    def apply[C <: Coproduct](implicit it: Iterate[C]): Iterate[C] = it

    implicit def caseCNil: Iterate[CNil] = new Iterate[CNil] {
      val list: List[SwaggerErrorResult] = Nil
    }

    implicit def caseCCons[H, T <: Coproduct](
        implicit rec: Iterate[T],
        se: SwaggerError[H]
    ): Iterate[H :+: T] = new Iterate[H :+: T] {
      def list: List[SwaggerErrorResult] = SwaggerErrorResult(se) :: rec.list
    }
  }

  implicit def mkSwaggerCoProduct[L <: Coproduct: Iterate, R: SwaggerTypeable](
    implicit resultsShow: Show[List[SwaggerErrorResult]]
  ): MkSwagger[Complete[Either[L, R]]] =
    Iterate[L]
      .list
      .groupBy(_.statusCode)
      .map {
        case (statusCode, results) =>
          val sorted = results.sortBy(_.apiErrorCode)
          statusCode -> sorted
      }
      .foldLeft(MkSwagger[Complete[R]]) {
        case (mkSwagger, (statusCode, results)) =>
          val desc = results.show
          mkSwagger
            .addResponse[ApiErrorResponse](statusCode, Some(desc))
            .as[Complete[R]]
      }
      .as[Complete[Either[L, R]]]
}
