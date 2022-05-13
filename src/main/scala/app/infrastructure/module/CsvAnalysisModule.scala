package app.infrastructure.module

import app.gateway.AnalysisService
import zio.UIO

object CsvAnalysisModule {

  def inMemoryService: UIO[AnalysisService] = for {
    statsService <- ProductStatisticsModule.inMemoryService
    analysisService = ProductAnalysisModule.service(statsService)
    ratingService = RatingModule.inMemoryService
  } yield new AnalysisService(analysisService = analysisService, ratingService = ratingService)

}
