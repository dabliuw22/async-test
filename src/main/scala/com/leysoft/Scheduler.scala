package com.leysoft

import cats.effect.{Concurrent, Timer}
import cats.effect.syntax.concurrent._
import cats.syntax.apply._
import cats.syntax.functor._
import io.chrisdavenport.log4cats.Logger

import scala.concurrent.duration.FiniteDuration

trait Scheduler[F[_]] {

  def schedule[A](
    task: F[A],
    duration: FiniteDuration
  ): F[Unit]
}

object Scheduler {

  def apply[F[_]](implicit sc: Scheduler[F]): Scheduler[F] = sc

  implicit def concurrentTimer[F[_]: Concurrent: Timer: Logger]: Scheduler[F] =
    new Scheduler[F] {
      override def schedule[A](
        task: F[A],
        duration: FiniteDuration
      ): F[Unit] =
        Timer[F].sleep(duration) *> Logger[F].info("Scheduler Start...") *>
          task.start.void <* Logger[F].info("Scheduler End...")
    }
}
