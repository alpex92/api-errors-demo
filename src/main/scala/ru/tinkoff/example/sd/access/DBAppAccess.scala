package ru.tinkoff.example.sd.access

import java.time.Instant
import java.util.UUID
import scala.language.higherKinds

import ru.tinkoff.example.sd.model.{AppId, AppStatus, Application, ApplicationCreate, ApplicationPreview}

class DBAppAccess[F[_]] extends AppAccess[F] {
  override def list(from: Instant): F[Seq[ApplicationPreview]] = ???

  override def details(appId: AppId): F[Option[Application]] = ???

  override def create(create: ApplicationCreate): F[UUID] = ???

  override def update(appId: AppId, newStatus: AppStatus): F[Unit] = ???
}
