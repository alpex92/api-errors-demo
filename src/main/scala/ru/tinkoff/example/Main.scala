package ru.tinkoff.example

import scala.concurrent.Future
import cats.instances.future._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import cats.arrow.FunctionK
import ru.tinkoff.example.sd.access.{AsyncJdbcAppAccess, SyncJdbcAppAccess}
import ru.tinkoff.example.sd.service.SDServiceImpl
import ru.tinkoff.example.web.Server
import ru.tinkoff.example.web.sd.SDModule

import scala.util.Try

object Main extends App {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  implicit val generalEx = system.dispatcher
  implicit val futureF = FunctionK.id[Future]

  val asyncDB = new AsyncJdbcAppAccess(SyncJdbcAppAccess)
  val service = new SDServiceImpl[Future](asyncDB)
  val module = new SDModule(service)
  val server = new Server(module)

  SyncJdbcAppAccess.initSchema()

  val port = (for {
    rawPort <- Option(System.getenv("PORT"))
    portFromEnv <- Try(rawPort.toInt).toOption
  } yield portFromEnv) getOrElse 9991

  Http().bindAndHandle(server.routes, "0.0.0.0", port)
}
