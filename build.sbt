val `scalaVersion_3` = "3.3.1"

ThisBuild / scalaVersion := `scalaVersion_3`

val targetJdk = "11"

ThisBuild / scalacOptions ++= Seq("-Dquill.macro.log=false", "-language:higherKinds")

def init(): Unit = {
  sys.props.put("quill.macro.log", false.toString)
  sys.props.put("quill.binds.log", true.toString)
}

val fake: Unit = init()

resolvers ++= Resolver.sonatypeOssRepos("releases")

//ThisBuild / Test / fork := true

ThisBuild / organization := "com.github.ajozwik"

ThisBuild / scalacOptions ++= Seq(
  "-encoding",
  "UTF-8",
  "-deprecation", // warning and location for usages of deprecated APIs
  "-feature",     // warning and location for usages of features that should be imported explicitly
  "-unchecked",   // additional warnings where generated code depends on assumptions
  "-language:reflectiveCalls",
  "-language:implicitConversions",
  "-Wunused:imports",
  "-Wunused:linted",
  "-Wunused:locals",
  "-Wunused:params",
  s"-release:$targetJdk"
)

ThisBuild / javacOptions ++= Seq("-Xlint:deprecation", "-Xdiags:verbose", "-source", targetJdk, "-target", targetJdk)

val quillVersion = scala.util.Properties.propOrElse("quill.version", "4.8.0")

val scalaTestVersion = "3.2.17"

val `ch.qos.logback_logback-classic`                 = "ch.qos.logback"              % "logback-classic"         % "1.2.12"
val `com.datastax.cassandra_cassandra-driver-extras` = "com.datastax.cassandra"      % "cassandra-driver-extras" % "3.11.5"
val `com.h2database_h2`                              = "com.h2database"              % "h2"                      % "2.2.224"
val `com.typesafe.scala-logging_scala-logging`       = "com.typesafe.scala-logging" %% "scala-logging"           % "3.9.5"
val `dev.zio_zio-interop-cats`                       = "dev.zio"                    %% "zio-interop-cats"        % "23.0.0.6"
val `io.getquill_quill-cassandra`                    = "io.getquill"                %% "quill-cassandra"         % quillVersion
val `io.getquill_quill-doobie`                       = "io.getquill"                %% "quill-doobie"            % quillVersion
val `io.getquill_quill-jdbc-zio`                     = "io.getquill"                %% "quill-jdbc-zio"          % quillVersion
val `io.getquill_quill-jdbc`                         = "io.getquill"                %% "quill-jdbc"              % quillVersion
val `io.getquill_quill-sql`                          = "io.getquill"                %% "quill-sql"               % quillVersion
val `org.cassandraunit_cassandra-unit`               = "org.cassandraunit"           % "cassandra-unit"          % "4.3.1.0"
val `org.scalacheck_scalacheck`                      = "org.scalacheck"             %% "scalacheck"              % "1.17.0"
val `org.scalatest_scalatest`                        = "org.scalatest"              %% "scalatest"               % scalaTestVersion
val `org.scalatestplus_scalacheck`                   = "org.scalatestplus"          %% "scalacheck-1-17"         % s"$scalaTestVersion.0"
val `org.tpolecat_doobie-h2`                         = "org.tpolecat"               %% "doobie-h2"               % "1.0.0-RC4"
val `org.typelevel_cats-core`                        = "org.typelevel"              %% "cats-core"               % "2.10.0"

publish / skip := true

ThisBuild / libraryDependencies ++= Seq(
  `ch.qos.logback_logback-classic`           % Test,
  `com.typesafe.scala-logging_scala-logging` % Test,
  `com.h2database_h2`                        % Test,
  `org.scalatest_scalatest`                  % Test,
  `org.scalacheck_scalacheck`                % Test,
  `org.scalatestplus_scalacheck`             % Test
)

lazy val `repository` = projectWithName("repository", file("repository")).settings(
  libraryDependencies ++= Seq(`io.getquill_quill-sql`)
)

lazy val `repository-monad` = projectWithName("repository-monad", file("repository-monad"))
  .settings(
    libraryDependencies ++= Seq(`org.typelevel_cats-core`)
  )
  .dependsOn(`repository`)
  .dependsOn(`repository` % "test->test")

lazy val `repository-doobie` = projectWithName("repository-doobie", file("repository-doobie"))
  .settings(libraryDependencies ++= Seq(`io.getquill_quill-doobie`, `org.tpolecat_doobie-h2` % Test))
  .dependsOn(`repository-jdbc-monad`)
  .dependsOn(`repository` % "test->test")

lazy val `repository-cassandra` = projectWithName("repository-cassandra", file("repository-cassandra"))
  .settings(
    libraryDependencies ++= Seq(
      `io.getquill_quill-cassandra`,
      `org.cassandraunit_cassandra-unit`               % Test,
      `com.datastax.cassandra_cassandra-driver-extras` % Test
    )
  )
  .dependsOn(`repository-monad`)
  .dependsOn(`repository` % "test->test")

lazy val `repository-jdbc-monad` = projectWithName("repository-jdbc-monad", file("repository-jdbc-monad"))
  .settings(
    libraryDependencies ++= Seq(`io.getquill_quill-jdbc`)
  )
  .dependsOn(`repository-monad`)
  .dependsOn(`repository` % "test->test")

lazy val `quill-jdbc-zio` = projectWithName("quill-jdbc-zio", file("quill-jdbc-zio"))
  .settings(libraryDependencies ++= Seq(`io.getquill_quill-jdbc-zio`, `dev.zio_zio-interop-cats`))
  .dependsOn(`repository-jdbc-monad`)
  .dependsOn(Seq(`repository`, `repository-jdbc-monad`).map(_ % "test->test")*)

def projectWithName(name: String, file: File): Project =
  Project(name, file).settings(
    licenseReportTitle      := s"Copyright (c) ${java.time.LocalDate.now.getYear} Andrzej Jozwik",
    licenseSelection        := Seq(LicenseCategory.MIT),
    Compile / doc / sources := Seq.empty,
    Compile / compile / wartremoverWarnings ++= Warts.allBut(Wart.ImplicitParameter, Wart.DefaultArguments)
  )
