name := "tallsafe"

version := "1.0"

scalaVersion := "2.12.2"

lazy val `tallsafe` = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(guice, ws,
  "com.typesafe.play" %% "play-json" % "2.6.8",
  "com.typesafe.play" %% "play-slick" % "3.0.3",
  "mysql" % "mysql-connector-java" % "5.1.38")

doc in Compile := target.map(_ / "none").value




      