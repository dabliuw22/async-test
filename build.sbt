import Dependencies._

lazy val commonSettings = Seq(
  name := "async-test",
  version := "0.1",
  organization := "com.leysoft",
  scalaVersion := "2.13.0",
  scalafmtOnCompile in ThisBuild := true,
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case _ => MergeStrategy.first
  }
)

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  Libraries.catsCore,
  Libraries.catsKernel,
  Libraries.catsMacros,
  Libraries.catsEffect,
  Libraries.scalaLogging,
  Libraries.logbackClassic,
  Libraries.log4CatsCore,
  Libraries.log4CatsSlf4j
)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:postfixOps",
  "-language:higherKinds" // or import scala.language.higherKinds
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    mainClass in assembly := Some("com.leysoft.ApiCats"),
    assemblyJarName in assembly := "api-cats.jar"
  )