package app.infrastructure

import app.gateway.CsvAnalysisService
import zio.UIO

object CsvAnalysisConfig {

  def inMemoryService: UIO[CsvAnalysisService] = for {
    statsService <- ProductStatisticsConfig.inMemoryService
    analysisService = ProductAnalysisConfig.service(statsService)
    ratingService = RatingConfig.inMemoryService
  } yield new CsvAnalysisService(analysisService = analysisService, ratingService = ratingService)

}
