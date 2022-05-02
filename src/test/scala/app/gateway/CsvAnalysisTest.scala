package app.gateway

import app.App
import app.gateway.out.ProductRatingAnalysisApiOutput
import app.infrastructure.CsvAnalysisConfig
import fs2.io.file.{NoSuchFileException, Path}
import zio.test.Assertion._
import zio.test.{assert, _}
import zio.{IO, Scope, ZEnvironment}

object CsvAnalysisTest extends ZIOSpecDefault {

  val t1 = test("analyse is invoked on non-existing file") {
    for {
      //      Given("create in memory analysis")
      csvAnalysis <- CsvAnalysisConfig.inMemoryAnalysis

      //      When("analyze empty file")
      executable: IO[Throwable, ProductRatingAnalysisApiOutput] = App
        .program(Path("src/test/resources/csv/nonexistingFile.csv"))
        .provideEnvironment(ZEnvironment(csvAnalysis))

      //      Then("verify output")
      result <- assertM(executable.exit)(fails(isSubtype[NoSuchFileException](anything)))
    } yield result
  }

  val t2 = test("analyse is invoked on an empty file") {
    for {
      //      Given("create in memory analysis")
      csvAnalysis <- CsvAnalysisConfig.inMemoryAnalysis

      //      When("analyze empty file")
      executable: IO[Throwable, ProductRatingAnalysisApiOutput] = App
        .program(Path("src/test/resources/csv/emptyFile.csv"))
        .provideEnvironment(ZEnvironment(csvAnalysis))

      //      Then("verify output")
      analysis <- executable
    } yield assert(analysis.validLines)(isZero) &&
      assert(analysis.invalidLine)(isZero) &&
      assert(analysis.bestRatedProducts)(isEmpty) &&
      assert(analysis.worstRatedProducts)(isEmpty) &&
      assert(analysis.lessRatedProduct)(isNull) &&
      assert(analysis.mostRatedProduct)(isNull)
  }

  val t3 = test("analyse is invoked on an non-empty file") {
    for {
      //      Given("create in memory analysis")
      csvAnalysis <- CsvAnalysisConfig.inMemoryAnalysis

      //      When("analyze non-empty file")
      executable: IO[Throwable, ProductRatingAnalysisApiOutput] = App
        .program(Path("src/test/resources/csv/nonEmptyFile.csv"))
        .provideEnvironment(ZEnvironment(csvAnalysis))

      //      Then("verify output")
      analysis <- executable
    } yield assert(analysis.validLines)(equalTo(49)) &&
      assert(analysis.invalidLine)(equalTo(6)) &&
      assert(analysis.bestRatedProducts)(equalTo(List("blu-ray-01", "fixie-01", "widetv-03"))) &&
      assert(analysis.worstRatedProducts)(equalTo(List("endura-01", "smarttv-01", "patagonia-01"))) &&
      assert(analysis.lessRatedProduct)(equalTo("saddle-01")) &&
      assert(analysis.mostRatedProduct)(equalTo("wifi-projector-01"))
  }

  val x = suite("The user can analyse the csv file")(
    t1,
    t2,
    t3
  )

  override def spec: ZSpec[TestEnvironment with Scope, Any] = x
}
