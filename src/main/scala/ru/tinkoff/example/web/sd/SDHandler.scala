package ru.tinkoff.example.web.sd

import java.util.UUID

import scala.language.higherKinds

import cats.Monad
import cats.syntax.functor._
import cats.syntax.either._
import shapeless.Coproduct

import ru.tinkoff.example.sd.model.{AppStatus, ApplicationCreate}
import ru.tinkoff.example.sd.service.SDService

class SDHandler[F[_] : Monad](service: SDService[F]) {

  def list: F[AppListResponse] = service.list

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
