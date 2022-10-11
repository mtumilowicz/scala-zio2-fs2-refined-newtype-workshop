package app.infrastructure.module

import app.domain.analysis.ProductAnalysisService
import app.domain.stats.ProductStatisticsService
import zio.{UIO, URLayer, ZLayer}

object ProductAnalysisModule {

  def serviceLayer: URLayer[ProductStatisticsService, ProductAnalysisService] =
    ZLayer.fromFunction(ProductAnalysisService.apply _)

  def inMemoryService: UIO[ProductAnalysisService] =
    ProductStatisticsModule.inMemoryService.map(service)

  def service(service: ProductStatisticsService): ProductAnalysisService =
    new ProductAnalysisService(service)
}
