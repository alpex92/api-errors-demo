package ru.tinkoff.example.web

import java.time.Instant
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.higherKinds

import akka.http.scaladsl.server.Route
import cats.Monad
import cats.instances.future._
import cats.syntax.either._
import cats.syntax.functor._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import shapeless.{:+:, CNil, Coproduct}

import ru.tinkoff.example.sd.app.SDService.errors.{AppCreateError, AppNotFound, AppUpdateError, AuthorizationNeeded, WrongStatusError}
import ru.tinkoff.example.sd.app.{SDService, SDServiceImpl}
import ru.tinkoff.example.sd.model.{AppStatus, Application, ApplicationCreate, ApplicationPreview}
import ru.tinkoff.example.typedschema.typedsl.handleErrors
import ru.tinkoff.tschema.akkaHttp.MkRoute
import ru.tinkoff.tschema.swagger.{MkSwagger, OpenApiTag, _}
import ru.tinkoff.tschema.syntax._

import ru.tinkoff.example.web._
import ru.tinkoff.example.web.sd.Errors._
import ru.tinkoff.example.typedschema.typeable._
import ru.tinkoff.example.typedschema.swaggererror._
import ru.tinkoff.example.typedschema.swaggererror.Routable._

package object sd extends FailFastCirceSupport {

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

  class BrokerAppHandler[F[_]: Monad](service: SDService[F]) {

    def list(from: Instant): F[AppListResponse] =
      service
        .list(from)
        .map {
          _.asRight[AppListResponseError]
        }

    def details(appId: UUID): F[AppDetailsResponse] =
      service
        .details(appId)
        .map {
          _.leftMap(Coproduct[AppDetailsResponseError](_))
        }

    def create(body: ApplicationCreate): F[CreateAppResponse] =
      service
        .create(body)
        .map {
          _.leftMap(Coproduct[CreateAppResponseError](_))
        }

    def update(appId: UUID, body: AppStatus): F[UpdateStatusResponse] =
      service
        .update(appId, body)
        .map {
          _.leftMap(_.embed[UpdateStatusResponseError])
        }
  }

  object BrokerAppModule extends Module {
    private val service = new SDServiceImpl[Future]
    private val handler = new BrokerAppHandler[Future](service)
    private val descriptions = mkDescription("sd")
    implicit val exceptionHandler: PartialFunction[Throwable, Route] = PartialFunction.empty
    override val routes: Route = MkRoute(sd.route)(handler)
    override val swagger: MkSwagger[_] = sd.route.mkSwagger.describe(descriptions)
    override val tagInfo: Vector[OpenApiTag] = Vector.empty
  }
}
