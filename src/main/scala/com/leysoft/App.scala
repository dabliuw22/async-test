package com.leysoft

import java.util.concurrent.{CompletableFuture, CompletionStage, Executors}

import cats.effect.{ExitCode, IO, IOApp}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

import scala.concurrent.{ExecutionContext, Future}

object App extends IOApp {
  import cats.syntax.apply._
  import cats.syntax.applicativeError._

  implicit val logger: Logger[IO] = Slf4jLogger.getLoggerFromName[IO]("App")

  implicit val ctx: ExecutionContext = ExecutionContext.Implicits.global

  val blocking: ExecutionContext =
    ExecutionContext.fromExecutor(Executors.newCachedThreadPool)

  override def run(args: List[String]): IO[ExitCode] =
    runCompletionStage("Name") *> IO { ExitCode.Success }

  def completableFuture(name: String): CompletableFuture[String] =
    CompletableFuture
      .supplyAsync { () =>
        Thread.sleep(5000)
        name
      }

  def completionStage(name: String): CompletionStage[String] =
    completableFuture(name)

  def future(name: String): Future[String] =
    Future {
      Thread.sleep(5000)
      name
    }

  def runCompletableFuture(name: String): IO[Unit] =
    logger.info("Start") *> AsyncTask
      .fromCompletableFuture[IO, String] { IO.delay(completableFuture(name)) }
      .handleError(_ => "Error")
      .flatMap { name =>
        logger.info(name)
      } *> logger.info("End")

  def runCompletionStage(name: String): IO[Unit] =
    logger.info("Start") *> AsyncTask
      .fromCompletionStage[IO, String] { IO.delay(completionStage(name)) }
      .handleError(_ => "Error")
      .flatMap { name =>
        logger.info(name)
      } *> logger.info("End")

  def runFuture(name: String): IO[Unit] =
    logger.info("Start") *> AsyncTask
      .fromFuture[IO, String] { IO.delay(future(name)) }
      .handleError(_ => "Error")
      .flatMap { name =>
        logger.info(name)
      } *> logger.info("End")

  def run: IO[Unit] =
    for {
      _ <- logger
            .info("Enter your name: ")
            .guarantee(IO.shift(blocking)) // execution context: contextShift
      name <- logger.info("Read") *> IO(scala.io.StdIn.readLine)
               .guarantee(IO.shift) // execution context: blocking
      _ <- logger.info(s"Welcome $name") // execution context: contextShift
    } yield ()
}
