package ru.tinkoff.example.web.sd

import akka.http.scaladsl.model.StatusCodes

import ru.tinkoff.example.sd.service.SDService.errors._
import ru.tinkoff.example.typedschema.swaggererror._

import ru.tinkoff.example.typedschema.swaggererror.ToApiErrorWithCode._

object Errors {

  implicit val appNotFound: SwaggerError[AppNotFound] = SwaggerErrorVal(
    "Заявка не найдена",
    StatusCodes.NotFound
  )

  implicit val appCreateError: SwaggerError[AppCreateError] = SwaggerErrorVal(
    "Ошибка создания заявки",
    StatusCodes.BadRequest
  )

  implicit val appCreateErrorApiErrorWithCode: ToApiErrorWithCode[AppCreateError] =
    toApiErrorWithCodeFromSwaggerErrorAndInstanceT { (se, t) =>
      ApiErrorResponse(s"${se.description}: ${t.issues.toList.mkString(", ")}", se.apiErrorCode)
    }

  implicit val wrongStatusError: SwaggerError[WrongStatusError] = SwaggerErrorVal(
    "Неверный статус",
    StatusCodes.BadRequest
  )

  implicit val wrongStatusApiErrorWithCode: ToApiErrorWithCode[WrongStatusError] =
    toApiErrorWithCodeFromSwaggerErrorAndInstanceT { (se, t) =>
      ApiErrorResponse(s"${se.description}: '${t.status}'", se.apiErrorCode)
    }

  implicit val appUpdateError: SwaggerError[AppUpdateError] = SwaggerErrorVal(
    "Ошибка обновления заявки",
    StatusCodes.InternalServerError
  )
}
