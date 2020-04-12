package com.leysoft

import java.util.concurrent.CompletableFuture

import cats.effect.{ExitCode, IO, IOApp}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

import scala.concurrent.Future

object App extends IOApp {
  import scala.concurrent.ExecutionContext.Implicits.global
  import cats.syntax.apply._

  implicit val logger: Logger[IO] = Slf4jLogger.getLoggerFromName[IO]("App")

  override def run(args: List[String]): IO[ExitCode] =
    runCompletableFuture("Your Name") *> IO { ExitCode.Success }

  def completableFuture(name: String): CompletableFuture[String] =
    CompletableFuture
      .supplyAsync { () =>
        Thread.sleep(5000)
        name
      }

  def future(name: String): Future[String] =
    Future {
      Thread.sleep(5000)
      name
    }

  def runCompletableFuture(name: String): IO[Unit] =
    AsyncTask
      .fromCompletableFuture[IO, String] { IO.delay(completableFuture(name)) }
      .flatMap { name =>
        logger.info(name)
      }

  def runFuture(name: String): IO[Unit] =
    AsyncTask
      .fromFuture[IO, String] { IO.delay(future(name)) }
      .flatMap { name =>
        logger.info(name)
      }
}
