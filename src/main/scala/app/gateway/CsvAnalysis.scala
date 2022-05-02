package app.gateway

import app.domain.analysis.ProductAnalysisService
import app.domain.purchase.{ProductRating, Purchase}
import app.domain.stats.{ProductStatistics, ProductStatisticsService}
import app.gateway.in.CsvLineApiInput
import app.gateway.out.{ParsingSummary, ProductRatingAnalysisApiOutput}
import cats.data.Validated.{Invalid, Valid}
import cats.data.ValidatedNec
import fs2.io.file.{Files, Path}
import fs2.text
import zio.interop.catz._
import zio.{Console, IO, Task}

import scala.io.Source
import scala.util.{Try, Using}

class CsvAnalysis(analysisService: ProductAnalysisService) {

  def calculate2(path: Path): Task[ProductRatingAnalysisApiOutput] = for {
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
