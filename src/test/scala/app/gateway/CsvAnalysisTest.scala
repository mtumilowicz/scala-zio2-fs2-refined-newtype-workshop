package app.gateway

import app.App
import app.infrastructure.module._
import fs2.io.file.{NoSuchFileException, Path}
import zio.test.Assertion._
import zio.test.{assert, _}

object CsvAnalysisTest extends ZIOSpecDefault {

  override def spec: Spec[Any, Throwable] =
    suite("The user can analyse the csv file")(
      nonExistingFileTest,
      emptyFileTest,
      nonEmptyFileTest
    ).provideSome(
      CsvAnalysisModule.service,
      ProductAnalysisModule.service,
      ProductStatisticsModule.service,
      ProductStatisticsModule.inMemoryRepository,
      PurchaseModule.service,
      PurchaseModule.csvRepository,
      RatingModule.service
    )

  private val nonExistingFileTest = test("analyse is invoked on non-existing file") {
    val path = Path("src/test/resources/csv/nonexistingFile.csv")
    val program = App.program(path)
    assertZIO(program.exit)(fails(isSubtype[NoSuchFileException](anything)))
  }

  private val emptyFileTest = test("analyse is invoked on an empty file") {
    val path = Path("src/test/resources/csv/emptyFile.csv")
    val program = App.program(path)
    for {
      analysis <- program
    } yield assert(analysis.validLines)(isZero) &&
      assert(analysis.invalidLine)(isZero) &&
      assert(analysis.bestRatedProducts)(isEmpty) &&
      assert(analysis.worstRatedProducts)(isEmpty) &&
      assert(analysis.lessRatedProduct)(isNull) &&
      assert(analysis.mostRatedProduct)(isNull)
  }

  private val nonEmptyFileTest = test("analyse is invoked on an non-empty file") {
    val path = Path("src/test/resources/csv/nonEmptyFile.csv")
    val program = App.program(path)
    for {
      analysis <- program
    } yield assert(analysis.validLines)(equalTo(49)) &&
      assert(analysis.invalidLine)(equalTo(6)) &&
      assert(analysis.bestRatedProducts)(equalTo(List("blu-ray-01", "fixie-01", "widetv-03"))) &&
      assert(analysis.worstRatedProducts)(equalTo(List("patagonia-01", "smarttv-01", "endura-01"))) &&
      assert(analysis.lessRatedProduct)(equalTo("saddle-01")) &&
      assert(analysis.mostRatedProduct)(equalTo("wifi-projector-01"))
  }


}
