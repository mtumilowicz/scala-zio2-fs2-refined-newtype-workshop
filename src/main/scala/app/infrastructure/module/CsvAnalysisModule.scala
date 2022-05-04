package app.infrastructure.module

import app.gateway.CsvAnalysisService
import zio.UIO

object CsvAnalysisModule {

  def inMemoryService: UIO[CsvAnalysisService] = for {
    statsService <- ProductStatisticsModule.inMemoryService
    analysisService = ProductAnalysisModule.service(statsService)
    ratingService = RatingModule.inMemoryService
  } yield new CsvAnalysisService(analysisService = analysisService, ratingService = ratingService)

}
