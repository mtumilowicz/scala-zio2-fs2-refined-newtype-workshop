package app.gateway

import app.domain.analysis.ProductAnalysisService
import app.domain.rating.{ProductRating, RatingService}
import app.gateway.out.{ParsingSummary, ProductRatingAnalysisApiOutput}
import cats.data.Validated.{Invalid, Valid}
import cats.data.ValidatedNec
import fs2.io.file.Path
import zio.interop.catz._
import zio.{Task, UIO}

class CsvAnalysisService(analysisService: ProductAnalysisService,
                         ratingService: RatingService) {

  def calculate(path: Path): Task[ProductRatingAnalysisApiOutput] = for {
    parsingSummary <- ratingService.findAll(path)
      .evalTap(addToStatistics)
      .compile
      .fold(ParsingSummary.zero()) {
        case (summary, Invalid(_)) => summary.invalidLineSpotted()
        case (summary, Valid(_)) => summary.validLineSpotted()
      }
    analysis <- analysisService.analyse()
  } yield ProductRatingAnalysisApiOutput.fromDomain(analysis, parsingSummary)

  private def addToStatistics(validatedPurchase: ValidatedNec[String, ProductRating]): UIO[ValidatedNec[String, ProductRating]] =
    validatedPurchase match {
      case Valid(a) =>
        analysisService.addToStatistics(a) *> UIO.succeed(validatedPurchase)
      case Invalid(_) => UIO.succeed(validatedPurchase)
    }
}
