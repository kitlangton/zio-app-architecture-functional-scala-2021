ThisBuild / scalaVersion     := "3.1.0"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

val zioVersion = "2.0.0-M6-2+24-66b6ec58+20211203-0242-SNAPSHOT"

val zioJsonVersion = "0.2.0-M2+4-61e5bc35+20211126-1515-SNAPSHOT"

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = (project in file("."))
  .settings(
    name := "simple-architecture",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio"         % zioVersion,
      "dev.zio" %% "zio-json"    % zioJsonVersion,
      "dev.zio" %% "zio-streams" % zioVersion,
      "dev.zio" %% "zio-test"    % zioVersion
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
