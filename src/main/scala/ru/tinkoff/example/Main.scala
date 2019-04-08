package ru.tinkoff.example

import scala.concurrent.Future

import cats.instances.future._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import cats.arrow.FunctionK
import io.getquill.{SnakeCase, SqliteJdbcContext}

import ru.tinkoff.example.sd.access.{AsyncJdbcAppAccess, SyncJdbcAppAccess}
import ru.tinkoff.example.sd.service.SDServiceImpl
import ru.tinkoff.example.web.Server
import ru.tinkoff.example.web.sd.SDModule

object Main extends App {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  implicit val generalEx = system.dispatcher
  implicit val jdbcCtx: SqliteJdbcContext[SnakeCase] = new SqliteJdbcContext(SnakeCase, "ctx")
  implicit val futureF = FunctionK.id[Future]

  val syncDB = new SyncJdbcAppAccess
  val asyncDB = new AsyncJdbcAppAccess(syncDB)
  val service = new SDServiceImpl[Future](asyncDB)
  val module = new SDModule(service)
  val server = new Server(module)

  syncDB.initSchema()
  Http().bindAndHandle(server.routes, "0.0.0.0", 9991)
}
