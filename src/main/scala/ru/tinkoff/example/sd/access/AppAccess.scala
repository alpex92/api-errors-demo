package ru.tinkoff.example.sd.access

import scala.language.higherKinds

import ru.tinkoff.example.sd.model._

trait AppAccess[F[_]] {
  def list: F[Seq[ApplicationPreview]]
  def details(appId: AppId): F[Option[Application]]
  def create(app: Application): F[Unit]
  def update(appId: AppId, newStatus: AppStatus): F[Unit]
}
