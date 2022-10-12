package app.infrastructure.module

import app.domain.analysis.ProductAnalysisService
import app.domain.stats.ProductStatisticsService
import zio.{URLayer, ZLayer}

object ProductAnalysisModule {

  def service: URLayer[ProductStatisticsService, ProductAnalysisService] =
    ZLayer.fromFunction(ProductAnalysisService.apply _)

}
