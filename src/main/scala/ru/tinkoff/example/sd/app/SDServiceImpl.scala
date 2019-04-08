package ru.tinkoff.example.sd.app

import java.time.Instant
import scala.language.higherKinds

import ru.tinkoff.example.sd.app.SDService.{AppDetailsError, StatusUpdateError, errors}
import ru.tinkoff.example.sd.model.{AppId, AppStatus, Application, ApplicationCreate, ApplicationPreview}

class SDServiceImpl[F[_]] extends SDService[F] {
  override def list(from: Instant): F[Seq[ApplicationPreview]] = ???
  override def details(appId: AppId): F[Either[AppDetailsError, Application]] = ???
  override def create(create: ApplicationCreate): F[Either[errors.AppCreateError, AppId]] = ???
  override def update(appId: AppId, status: AppStatus): F[Either[StatusUpdateError, Unit]] = ???
}
