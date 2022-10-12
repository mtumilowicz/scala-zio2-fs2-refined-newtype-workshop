package app.infrastructure.module

import app.domain.analysis.ProductAnalysisService
import app.domain.rating.RatingService
import app.gateway.AnalysisService
import zio.{URLayer, ZLayer}

object CsvAnalysisModule {

  def service: URLayer[ProductAnalysisService with RatingService, AnalysisService] = ZLayer.fromFunction(AnalysisService.apply _)

}
