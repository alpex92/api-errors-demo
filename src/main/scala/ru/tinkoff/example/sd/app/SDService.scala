package ru.tinkoff.example.sd.app

import java.time.Instant
import java.util.UUID
import scala.language.higherKinds

import shapeless.{:+:, CNil}

import ru.tinkoff.example.sd.app.SDService.errors._
import ru.tinkoff.example.sd.app.SDService.{AppDetailsError, StatusUpdateError}
import ru.tinkoff.example.sd.model._

object SDService {

  object errors {
    case class AppNotFound(id: AppId)
    case class WrongStatusError(status: AppStatus)
    trait AppUpdateError
    trait AuthorizationNeeded
    trait AppCreateError
  }

  type AppDetailsError = AppNotFound
  type StatusUpdateError = AppNotFound :+: WrongStatusError :+: AppUpdateError :+: CNil
}

trait SDService[F[_]] {
  def list(from: Instant): F[Seq[ApplicationPreview]]
  def details(appId: AppId): F[Either[AppDetailsError, Application]]
  def create(create: ApplicationCreate): F[Either[AppCreateError, UUID]]
  def update(appId: AppId, status: AppStatus): F[Either[StatusUpdateError, Unit]]
}
