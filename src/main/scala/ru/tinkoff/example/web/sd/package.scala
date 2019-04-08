package ru.tinkoff.example.web

import java.util.UUID
import scala.language.higherKinds

import shapeless.{:+:, CNil}

import ru.tinkoff.example.sd.model.{AppStatus, Application, ApplicationCreate, ApplicationPreview}
import ru.tinkoff.example.sd.service.SDService.errors._
import ru.tinkoff.example.typedschema.typedsl.handleErrors
import ru.tinkoff.tschema.syntax._

package object sd {

  // format: off
  type AppListResponse = Seq[ApplicationPreview]

  type AppDetailsResponseError = AppNotFound :+: CNil
  type AppDetailsResponse = Either[AppDetailsResponseError, Application]

  type CreateAppResponseError = AppCreateError :+: CNil
  type CreateAppResponse = Either[CreateAppResponseError, UUID]

  type UpdateStatusResponseError = AppNotFound :+: WrongStatusError :+: AppUpdateError :+: CNil
  type UpdateStatusResponse = Either[UpdateStatusResponseError, Unit]

  val route = tag('Заявки) :> prefix('api) :> prefix('sd) :> prefix('v1) :> prefix('applications) :> (handleErrors() :> (
    (key('list) :> get.! :> complete[AppListResponse]) ~
      (key('create) :> post.! :> reqBody[ApplicationCreate] :> complete[CreateAppResponse]) ~
      (key('details) :> capture[UUID]('appId) :> get.! :> complete[AppDetailsResponse]) ~
      (capture[UUID]('appId) :> operation('update) :> post.! :> reqBody[AppStatus] :> complete[UpdateStatusResponse])
  ))
  // format: on

}
