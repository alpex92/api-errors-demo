package ru.tinkoff.example.sd.service

import scala.language.higherKinds

import cats.data.{EitherT, NonEmptyList, Validated, ValidatedNel}
import cats.syntax.either._
import cats.syntax.functor._
import cats.{Apply, Monad}
import shapeless.Coproduct

import ru.tinkoff.example.sd.access.AppAccess
import ru.tinkoff.example.sd.model.AppStatus._
import ru.tinkoff.example.sd.model._
import ru.tinkoff.example.sd.service.SDService.errors.{AppCreateError, AppNotFound, WrongStatusError}
import ru.tinkoff.example.sd.service.SDService.{AppDetailsError, StatusUpdateError, errors}

object SDServiceImpl {

  type V[T] = ValidatedNel[String, T]

  val allowedSteps: PartialFunction[AppStatus, Seq[AppStatus]] = {
    case New =>
      Seq(InProgress, Resolved, Rejected)
    case InProgress =>
      Seq(Resolved, Rejected)
  }

  def checkStatus(old: AppStatus, `new`: AppStatus): Boolean = {
    allowedSteps
      .applyOrElse(old, (_: AppStatus) => Seq.empty)
      .contains(`new`)
  }
}

class SDServiceImpl[F[_] : Monad](access: AppAccess[F]) extends SDService[F] {

  import SDServiceImpl._

  override def list: F[Seq[ApplicationPreview]] = access.list

  override def details(appId: AppId): F[Either[AppDetailsError, Application]] = access
    .details(appId)
    .map {
      _.toRight(AppNotFound(appId))
    }

  override def create(create: ApplicationCreate): F[Either[errors.AppCreateError, AppId]] = {

    val titleV = Validated.cond(
      create.title.nonEmpty,
      create.title,
      NonEmptyList.of("пустой заголовок")
    )
    val descV = Validated.cond(
      create.description.nonEmpty,
      create.description,
      NonEmptyList.of("пустое описание")
    )

    Apply[V]
      .map2(titleV, descV) { (_, _) =>
        val app = Application.fromCreate(create)
        access.create(app).map(_ => app.meta.id)
      }
      .toEither
      .leftMap(AppCreateError)
      .traverse(identity)
  }

  override def update(appId: AppId, newStatus: AppStatus): F[Either[StatusUpdateError, Unit]] =
    EitherT(details(appId))
      .leftMap(Coproduct[StatusUpdateError](_))
      .flatMapF { currentApp =>
        val currentStatus = currentApp.meta.status
        Either.cond(
          checkStatus(currentStatus, newStatus),
          access.update(appId, newStatus),
          Coproduct[StatusUpdateError](WrongStatusError(currentStatus))
        ).traverse(identity)
      }.value
}
