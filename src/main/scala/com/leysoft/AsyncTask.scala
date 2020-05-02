package com.leysoft

import java.util.concurrent.{CompletableFuture, CompletionStage, Future => JvmFuture}

import cats.effect.{Async, ContextShift}
import io.chrisdavenport.log4cats.Logger

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object AsyncTask {
  import cats.effect.syntax.bracket._
  import cats.syntax.apply._
  import cats.syntax.flatMap._
  import cats.syntax.functor._

  private[leysoft] type AsyncTask[A] = CompletionStage[A] with JvmFuture[A]

  def fromCompletableFuture[F[_]: Async: ContextShift: Logger, A](
    fa: F[CompletableFuture[A]]
  ): F[A] =
    liftAsyncTask[F, CompletableFuture[A], A](fa)

  def fromCompletionStage[F[_]: Async: ContextShift: Logger, A](
    fa: F[CompletionStage[A]]
  ): F[A] =
    liftCompletionStage[F, CompletionStage[A], A](fa)

  def fromFuture[F[_]: Async: ContextShift: Logger, A](
    fa: F[Future[A]]
  )(implicit ctx: ExecutionContext): F[A] =
    liftFuture[F, Future[A], A](fa)

  private def liftAsyncTask[F[_], G <: AsyncTask[A], A](
    fa: F[G]
  )(implicit F: Async[F], cs: ContextShift[F], log: Logger[F]): F[A] =
    log.info("AsyncTask Start...") *> fa.flatMap { future =>
      F.async[A] { cb =>
          future.handle[Unit] { (v: A, t: Throwable) =>
            if (t != null) cb(Left(t))
            else cb(Right(v))
          }
          ()
        }
        .guarantee(cs.shift)
    } <* log.info("AsyncTask End...")

  private def liftCompletionStage[F[_], G <: CompletionStage[A], A](
    fa: F[G]
  )(implicit F: Async[F], cs: ContextShift[F], log: Logger[F]): F[A] =
    log.info("AsyncTask Start...") *> fa.flatMap { future =>
      F.async[A] { cb =>
          future.handle[Unit] { (v: A, t: Throwable) =>
            if (t != null) cb(Left(t))
            else cb(Right(v))
          }
          ()
        }
        .guarantee(cs.shift)
    } <* log.info("AsyncTask End...")

  private def liftFuture[F[_]: Async: ContextShift: Logger, G <: Future[
    A
  ], A](fa: F[G])(implicit ctx: ExecutionContext): F[A] =
    for {
      _ <- Logger[F].info("AsyncTask Start...")
      future <- fa
      async <- Async[F]
                .async[A] { cb =>
                  future.onComplete {
                    case Success(v) => cb(Right(v))
                    case Failure(t) => cb(Left(t))
                  }
                }
                .guarantee(ContextShift[F].shift)
      _ <- Logger[F].info("AsyncTask End...")
    } yield async
}
