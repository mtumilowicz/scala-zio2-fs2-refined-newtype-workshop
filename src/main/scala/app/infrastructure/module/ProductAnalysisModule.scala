package app.infrastructure.module

import app.domain.analysis.ProductAnalysisService
import app.domain.stats.ProductStatisticsService
import zio.UIO

object ProductAnalysisModule {

  def inMemoryService: UIO[ProductAnalysisService] =
    ProductStatisticsModule.inMemoryService.map(service)

  def service(service: ProductStatisticsService): ProductAnalysisService =
    new ProductAnalysisService(service)
}
