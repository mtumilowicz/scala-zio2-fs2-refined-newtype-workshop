name := "scala-zio2-fs2-workshop"

version := "0.1"

scalaVersion := "2.13.8"

libraryDependencies += "dev.zio" %% "zio" % "2.0.0-RC5"
libraryDependencies += "dev.zio" %% "zio-interop-cats" % "3.3.0-RC6"
libraryDependencies += "co.fs2" %% "fs2-core" % "3.2.7"
libraryDependencies += "co.fs2" %% "fs2-io" % "3.2.7"
libraryDependencies += "dev.zio" %% "zio-test" % "2.0.0-RC5" % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.2" % "test"
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.1" % "test"