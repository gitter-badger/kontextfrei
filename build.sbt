import com.lightbend.paradox.sbt.ParadoxPlugin.autoImport.paradoxTheme

name := "kontextfrei"

val common = Seq(
  organization := "com.danielwestheide",
  version := "0.4.1-SNAPSHOT",
  scalaVersion := "2.10.6",
  crossScalaVersions := Seq("2.10.6", "2.11.8"),
  licenses += ("Apache-2.0", url(
    "https://opensource.org/licenses/Apache-2.0")),
  bintrayPackageLabels := Seq("scala", "spark", "testing"),
  scalacOptions ++= Seq("-feature",
                        "-language:higherKinds",
                        "-language:implicitConversions")
)

def spark(scalaVersion: String) = {
  val sparkVersion = scalaVersion match {
    case "2.10" => "1.4.1"
    case "2.11" => "2.0.0"
    case other  => fail(s"Unsupported Scala version: $other")
  }
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided"
}
val scalatest  = "org.scalatest"  %% "scalatest"  % "3.0.1"
val scalacheck = "org.scalacheck" %% "scalacheck" % "1.13.4"

lazy val core = Project(id = "kontextfrei-core", base = file("core"))
  .settings(common)
  .settings(
    libraryDependencies ++= Seq(spark(scalaBinaryVersion.value),
                                scalatest  % Test,
                                scalacheck % Test))

lazy val scalaTest =
  Project(id = "kontextfrei-scalatest", base = file("scalatest"))
    .settings(common)
    .settings(
      libraryDependencies ++= Seq(spark(scalaBinaryVersion.value), scalatest))
    .dependsOn(core)

lazy val root = Project(id = "kontextfrei", base = file("."))
  .settings(common)
  .settings(
    sourceDirectory in Paradox := sourceDirectory.value / "main" / "paradox",
    paradoxTheme := Some(builtinParadoxTheme("generic"))
  )
  .settings(ghpages.settings)
  .settings(
    git.remoteRepo := "git@github.com:dwestheide/kontextfrei.git"
  )
  .aggregate(core, scalaTest)
  .enablePlugins(ParadoxSitePlugin)

publishArtifact in root := false

publish in root := {}

publishLocal in root := {}
