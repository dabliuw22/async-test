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
    liftJFuture[F, CompletableFuture[A], A](fa)

  def fromFuture[F[_]: Async: ContextShift: Logger, A](
    fa: F[Future[A]]
  )(implicit ctx: ExecutionContext): F[A] =
    liftFuture[F, Future[A], A](fa)

  private[leysoft] def liftJFuture[F[_], G <: AsyncTask[A], A](
    fa: F[G]
  )(implicit F: Async[F], cs: ContextShift[F], log: Logger[F]): F[A] =
    log.info("Start...") *> fa.flatMap { future =>
      F.async { fb: (Either[Throwable, A] => Unit) =>
          future.handle[Unit] { (v: A, t: Throwable) =>
            if (t != null) fb(Left(t))
            else fb(Right(v))
          }
          ()
        }
        .guarantee(cs.shift)
    } <* log.info("End...")

  private[leysoft] def liftFuture[F[_]: Async: ContextShift: Logger, G <: Future[
    A
  ], A](fa: F[G])(implicit ctx: ExecutionContext): F[A] =
    for {
      _ <- Logger[F].info("Start...")
      future <- fa
      async <- Async[F]
                .async { cb: (Either[Throwable, A] => Unit) =>
                  future.onComplete {
                    case Success(v) => cb(Right(v))
                    case Failure(t) => cb(Left(t))
                  }
                }
                .guarantee(ContextShift[F].shift)
      _ <- Logger[F].info("End...")
    } yield async
}
