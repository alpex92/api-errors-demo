package ru.tinkoff.example.sd.access

import scala.concurrent.{ExecutionContext, Future, blocking}
import scala.language.higherKinds

import cats.Id

import ru.tinkoff.example.db.SQLiteContext
import ru.tinkoff.example.sd.model.{AppId, AppStatus, Application, ApplicationPreview}
import ru.tinkoff.example.db.SQLiteContext._
import ru.tinkoff.example.db._

object Queries {

  implicit val appInsertMeta = insertMeta[Application]()

  val applications = quote(querySchema[Application](
    "applications",
    _.meta.id -> "id",
    _.meta.status -> "status",
    _.meta.lastModified -> "last_modified",
  ))

  val list = quote {
    applications.map(app => ApplicationPreview(app.meta, app.title))
  }
  val details = (appId: AppId) => quote(applications.filter(_.meta.id == lift(appId)))
  val create = (app: Application) => quote(applications.insert(lift(app)))
  val update = (appId: AppId, newStatus: AppStatus) => quote {
    applications
      .filter(_.meta.id == lift(appId))
      .update(_.meta.status -> lift(newStatus))
  }
}

class SyncJdbcAppAccess extends AppAccess[Id] {

  val ctx = SQLiteContext

  override def list: Id[Seq[ApplicationPreview]] = ctx.run(Queries.list)
  override def details(appId: AppId): Id[Option[Application]] = ctx.run(Queries.details(appId)).headOption
  override def create(app: Application): Id[Unit] = ctx.run(Queries.create(app))
  override def update(appId: AppId, newStatus: AppStatus): Id[Unit] = ctx.run(Queries.update(appId, newStatus))

  def initSchema() = ctx.executeAction("""CREATE TABLE IF NOT EXISTS applications (
             id TEXT NOT NULL PRIMARY KEY,
             title TEXT NOT NULL,
             description TEXT NOT NULL,
             last_modified TEXT NOT NULL,
             status TEXT NOT NULL
             );""")
}

class AsyncJdbcAppAccess
(syncContext: AppAccess[Id])
(implicit ex: ExecutionContext) extends AppAccess[Future] {
  private implicit def wrap[T](body: => T): Future[T] = Future(blocking(body))
  override def list: Future[Seq[ApplicationPreview]] = syncContext.list
  override def details(appId: AppId): Future[Option[Application]] = syncContext.details(appId)
  override def create(app: Application): Future[Unit] = syncContext.create(app)
  override def update(appId: AppId, newStatus: AppStatus): Future[Unit] = syncContext.update(appId, newStatus)
}
