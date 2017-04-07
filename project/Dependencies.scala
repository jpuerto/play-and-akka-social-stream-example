import sbt._

object Dependencies {
  lazy val akkaSocialStreamVersion = "0.1.4"
  lazy val sprayJsonVersion = "1.3.3"

  val akkaSocialStream = "ch.becompany" %% "akka-social-stream" % akkaSocialStreamVersion
  val sprayJson = "io.spray" %%"spray-json" % sprayJsonVersion
  val scalaTest = "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test

  val backendDepts = Seq(akkaSocialStream, sprayJson, scalaTest)
}