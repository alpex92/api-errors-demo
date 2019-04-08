package ru.tinkoff.example.web

import java.time.Instant
import java.util.UUID
import scala.language.higherKinds

import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import shapeless.{:+:, CNil}

import ru.tinkoff.example.sd.model.{AppStatus, Application, ApplicationCreate, ApplicationPreview}
import ru.tinkoff.example.sd.service.SDService.errors._
import ru.tinkoff.example.typedschema.typedsl.handleErrors
import ru.tinkoff.tschema.syntax._

package object sd {

  // format: off
  type AppListResponseError = AuthorizationNeeded :+: CNil
  type AppListResponse = Either[AppListResponseError, Seq[ApplicationPreview]]

  type AppDetailsResponseError = AuthorizationNeeded :+: AppNotFound :+: CNil
  type AppDetailsResponse = Either[AppDetailsResponseError, Application]

  type CreateAppResponseError = AuthorizationNeeded :+: AppCreateError :+: CNil
  type CreateAppResponse = Either[CreateAppResponseError, UUID]

  type UpdateStatusResponseError = AuthorizationNeeded :+: AppNotFound :+: WrongStatusError :+: AppUpdateError :+: CNil
  type UpdateStatusResponse = Either[UpdateStatusResponseError, Unit]

  val route = tag('Заявки) :> prefix('api) :> prefix('sd) :> prefix('v1) :> prefix('applications) :> (handleErrors() :> (
    (key('list) :> queryParam[Instant]('from) :> get.! :> complete[AppListResponse]) ~
    (key('details) :> capture[UUID]('appId) :> get.! :> complete[AppDetailsResponse]) ~
    (operation('create) :> post.! :> reqBody[ApplicationCreate] :> complete[CreateAppResponse]) ~
    (capture[UUID]('appId) :> operation('update) :> post.! :> reqBody[AppStatus] :> complete[UpdateStatusResponse])
  ))
  // format: on

}
