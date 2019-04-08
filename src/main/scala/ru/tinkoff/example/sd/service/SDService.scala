package ru.tinkoff.example.sd.service

import java.time.Instant
import java.util.UUID
import scala.language.higherKinds

import cats.data.NonEmptyList
import shapeless.{:+:, CNil}

import ru.tinkoff.example.sd.service.SDService.errors._
import ru.tinkoff.example.sd.service.SDService.{AppDetailsError, StatusUpdateError}
import ru.tinkoff.example.sd.model._

object SDService {

  object errors {
    case class AppNotFound(id: AppId)
    case class WrongStatusError(status: AppStatus)
    trait AppUpdateError
    trait AuthorizationNeeded
    case class AppCreateError(issues: NonEmptyList[String])
  }

  type AppDetailsError = AppNotFound
  type StatusUpdateError = AppNotFound :+: WrongStatusError :+: AppUpdateError :+: CNil
}

trait SDService[F[_]] {
  def list: F[Seq[ApplicationPreview]]
  def details(appId: AppId): F[Either[AppDetailsError, Application]]
  def create(create: ApplicationCreate): F[Either[AppCreateError, UUID]]
  def update(appId: AppId, newStatus: AppStatus): F[Either[StatusUpdateError, Unit]]
}
