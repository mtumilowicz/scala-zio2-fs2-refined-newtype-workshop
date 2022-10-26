name := "scala-zio2-fs2-refined-newtype-workshop"

version := "0.1"

scalaVersion := "2.13.8"

libraryDependencies += "dev.zio" %% "zio" % "2.0.2"
libraryDependencies += "dev.zio" %% "zio-interop-cats" % "3.3.0"
libraryDependencies += "co.fs2" %% "fs2-core" % "3.2.7"
libraryDependencies += "co.fs2" %% "fs2-io" % "3.2.7"
libraryDependencies += "eu.timepit" %% "refined" % "0.9.28"
libraryDependencies += "io.estatico" %% "newtype" % "0.4.4"
libraryDependencies += "tf.tofu" %% "derevo-cats" % "0.13.0"
libraryDependencies += "dev.zio" %% "zio-test" % "2.0.2" % Test
libraryDependencies += "dev.zio" %% "zio-test-sbt" % "2.0.2" % Test
libraryDependencies += "dev.zio" %% "zio-test-magnolia" % "2.0.2" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.2" % "test"
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.1" % "test"

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

scalacOptions += "-Ymacro-annotations"