package ru.tinkoff.example.sd.access

import java.time.Instant
import java.util.UUID
import scala.language.higherKinds

import ru.tinkoff.example.sd.model._

trait AppAccess[F[_]] {
  def list(from: Instant): F[Seq[ApplicationPreview]]
  def details(appId: AppId): F[Option[Application]]
  def create(create: ApplicationCreate): F[UUID]
  def update(appId: AppId, newStatus: AppStatus): F[Unit]
}
