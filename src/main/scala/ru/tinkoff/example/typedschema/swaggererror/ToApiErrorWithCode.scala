package ru.tinkoff.example.typedschema.swaggererror

import akka.http.scaladsl.model.StatusCode
import simulacrum.typeclass

@typeclass
trait ToApiErrorWithCode[T] {
  def statusCode: StatusCode
  def toApiError(t: T): ApiErrorResponse // TODO: ToResponseMarshallable?
}

object ToApiErrorWithCode {
  implicit def toApiErrorWithCode[T](
    implicit se: SwaggerError[T]
  ): ToApiErrorWithCode[T] = new ToApiErrorWithCode[T] {
    override val statusCode: StatusCode = se.statusCode
    override def toApiError(t: T): ApiErrorResponse = ApiErrorResponse(se.description, se.apiErrorCode)
  }

  implicit def toApiErrorWithCode[T](
    mkResponse: (SwaggerError[T], T) => ApiErrorResponse
  )(implicit se: SwaggerError[T]): ToApiErrorWithCode[T] = new ToApiErrorWithCode[T] {
    override val statusCode: StatusCode = se.statusCode
    override def toApiError(t: T): ApiErrorResponse = mkResponse(se, t)
  }
}
