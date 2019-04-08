package ru.tinkoff.example

import scala.concurrent.Future

import cats.instances.future._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import cats.arrow.FunctionK
import io.getquill.{SnakeCase, SqliteJdbcContext}

import ru.tinkoff.example.sd.access.DBAppAccess
import ru.tinkoff.example.sd.service.SDServiceImpl
import ru.tinkoff.example.web.Server
import ru.tinkoff.example.web.sd.SDModule

object Main extends App {

  lazy val ctx = new SqliteJdbcContext(SnakeCase, "ctx")

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  implicit val ex = system.dispatcher
  implicit val futureF = FunctionK.id[Future]

  val dbAccess = new DBAppAccess[Future]
  val service = new SDServiceImpl[Future](dbAccess)
  val module = new SDModule(service)
  val server = new Server(module)
  Http().bindAndHandle(server.routes, "0.0.0.0", 9991)
}
