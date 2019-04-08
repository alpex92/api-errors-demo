package ru.tinkoff.example

import java.util.Locale

import scala.language.higherKinds

import cats.Show
import cats.syntax.show._

import ru.tinkoff.example.typedschema.swaggererror.Iterate.SwaggerErrorResult
import ru.tinkoff.tschema.swagger.PathDescription
import ru.tinkoff.tschema.swagger.PathDescription.DescriptionMap

package object web {

  private val ruLocale: Locale = java.util.Locale.forLanguageTag("ru")

  def mkDescription(bundleName: String): DescriptionMap = PathDescription.utf8I18n(
    "swaggerProperties." + bundleName,
    ruLocale
  )

  implicit val swaggerErrorShow: Show[SwaggerErrorResult] = (t: SwaggerErrorResult) =>
    t.apiErrorCode.foldLeft(t.description) {
      case (desc, code) => s"$code - $desc"
    }

  implicit def listShow[A: Show]: Show[List[A]] = { fa: List[A] =>
    fa.iterator.map(_.show).mkString("\n")
  }
}
