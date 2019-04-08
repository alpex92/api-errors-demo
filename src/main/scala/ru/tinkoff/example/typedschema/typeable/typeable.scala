package ru.tinkoff.example.typedschema

import java.time.{Instant, LocalDate, Year}
import scala.util.Try

import cats.syntax.option._
import io.circe.Json

import ru.tinkoff.tschema.FromQueryParam
import ru.tinkoff.tschema.swagger.SwaggerTypeable.make
import ru.tinkoff.tschema.swagger.{SwaggerObject, SwaggerPrimitive, SwaggerStringValue, SwaggerTypeable}

package object typeable {

  case object localDate extends SwaggerPrimitive(
    SwaggerStringValue(pattern = "d{4}-d{2}-d{2}".some)
  )

  case object instant extends SwaggerPrimitive(
    SwaggerStringValue(pattern = "d{4}-d{2}-d{2}Td{2}:d{2}:d{2}.d{3}Z".some)
  )

  implicit val jsonTypeable: SwaggerTypeable[Json] = make[Json](SwaggerObject())
  implicit val instantTypeable: SwaggerTypeable[Instant] = make[Instant](instant)
  implicit val localDateTypeable: SwaggerTypeable[LocalDate] = make[LocalDate](localDate)
  implicit val yearTypeable: SwaggerTypeable[Year] = make[Year](SwaggerPrimitive.integer)
}
