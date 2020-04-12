import sbt._

object Dependencies {

  object Versions {
    lazy val cats = "2.0.0"
    lazy val monix = "3.1.0"
    lazy val fs2 = "2.2.1"
    lazy val circe = "0.12.3"
    lazy val log4cats = "1.0.1"
    lazy val scalaLog = "3.9.2"
    lazy val logback = "1.2.3"
    lazy val logbackEncoder = "6.3"
    lazy val newType = "0.4.3"

    object Test {
      lazy val scalaTest = "3.0.8"
      lazy val scalaMock = "4.4.0"
      lazy val scalaCheck = "1.14.3"
      lazy val scalaCheckToolbox = "0.3.2"
      lazy val scalaTestPlus = "3.1.0.1"
    }
  }

  object Libraries {

    private def cats(artifact: String): ModuleID = "org.typelevel" %% artifact % Versions.cats
    private def monix(artifact: String): ModuleID = "io.monix" %% artifact % Versions.monix
    private def fs2(artifact: String): ModuleID = "co.fs2" %% artifact % Versions.fs2
    private def circe(artifact: String): ModuleID = "io.circe" %% artifact % Versions.circe
    private def log4cats(artifact: String): ModuleID = "io.chrisdavenport" %% artifact % Versions.log4cats
    private def scalaLog(artifact: String): ModuleID = "com.typesafe.scala-logging" %% artifact % Versions.scalaLog
    private def logback(artifact: String): ModuleID = "ch.qos.logback" % artifact % Versions.logback
    private def logbackEncoder(artifact: String): ModuleID = "net.logstash.logback" % artifact % Versions.logbackEncoder

    lazy val catsMacros = cats("cats-macros")
    lazy val catsKernel = cats("cats-kernel")
    lazy val catsCore = cats("cats-core")
    lazy val catsEffect = cats("cats-effect")
    lazy val monixEval = monix("monix-eval")
    lazy val monixExecution = monix("monix-execution")
    lazy val fs2Core = fs2("fs2-core")
    lazy val circeGeneric = circe("circe-generic")
    lazy val circeLiteral = circe("circe-literal")
    lazy val scalaLogging = scalaLog("scala-logging")
    lazy val logbackClassic = logback("logback-classic")
    lazy val logstashLogbackEncoder = logbackEncoder("logstash-logback-encoder")
    lazy val log4CatsCore = log4cats("log4cats-core")
    lazy val log4CatsSlf4j = log4cats("log4cats-slf4j")
    lazy val newType = "io.estatico" %% "newtype" % Versions.newType

    object Testing {
      private def scalaCheckToolbox(artifact: String): ModuleID = "com.47deg" %% artifact % Versions.Test.scalaCheckToolbox % Test

      lazy val scalaTest = "org.scalatest" %% "scalatest" % Versions.Test.scalaTest % Test
      lazy val scalaMock = "org.scalamock" %% "scalamock" % Versions.Test.scalaMock % Test
      lazy val scalaCheck = "org.scalacheck" %% "scalacheck" % Versions.Test.scalaCheck % Test
      lazy val scalaTestPlus = "org.scalatestplus" %% "scalacheck-1-14" % Versions.Test.scalaTestPlus % Test
      lazy val scalaCheckToolboxDatetime = scalaCheckToolbox("scalacheck-toolbox-datetime")
      lazy val scalaCheckToolboxMagic = scalaCheckToolbox("scalacheck-toolbox-magic")
      lazy val scalaCheckToolboxCombinators = scalaCheckToolbox("scalacheck-toolbox-combinators")
    }
  }
}
