package app.infrastructure.module

import app.domain.analysis.ProductAnalysisService
import app.domain.rating.RatingService
import app.gateway.AnalysisService
import zio.{UIO, URLayer, ZLayer}

object CsvAnalysisModule {


  def serviceLayer: URLayer[ProductAnalysisService with RatingService, AnalysisService] = ZLayer.fromFunction(AnalysisService.apply _)

  def inMemoryService: UIO[AnalysisService] = for {
    statsService <- ProductStatisticsModule.inMemoryService
    analysisService = ProductAnalysisModule.service(statsService)
    ratingService = RatingModule.inMemoryService
  } yield new AnalysisService(analysisService = analysisService, ratingService = ratingService)

}
