package app.gateway

import app.domain.analysis.ProductAnalysisService
import app.domain.purchase.Purchase
import app.gateway.in.CsvLineApiInput
import app.gateway.out.{ParsingSummary, ProductRatingAnalysisApiOutput}
import cats.data.Validated.{Invalid, Valid}
import fs2.io.file.{Files, Path}
import fs2.text
import zio.Task
import zio.interop.catz._

class CsvAnalysisService(analysisService: ProductAnalysisService) {

  def calculate(path: Path): Task[ProductRatingAnalysisApiOutput] = for {
    _ <- findAll(path)
      .map(_.toProductRating)
      .evalMap(analysisService.addToStatistics)
      .compile
      .drain
    parsingSummary <- parsingSummary(path)
    analysis <- analysisService.analyse()
  } yield ProductRatingAnalysisApiOutput.fromDomain(analysis, parsingSummary)

  private def findAll(path: Path): fs2.Stream[Task, Purchase] =
    Files[Task].readAll(path)
      .through(text.utf8.decode)
      .through(text.lines)
      .drop(1)
      .map(CsvLineApiInput)
      .map(_.toDomain)
      .collect {
        case Valid(a) => a
      }

  private def parsingSummary(path: Path): Task[ParsingSummary] =
    Files[Task].readAll(path)
      .through(text.utf8.decode)
      .through(text.lines)
      .drop(1)
      .map(CsvLineApiInput)
      .map(_.toDomain)
      .compile
      .fold(ParsingSummary.zero()) {
        case (summary, Invalid(_)) => summary.invalidLineSpotted()
        case (summary, Valid(_)) => summary.validLineSpotted()
      }
}
