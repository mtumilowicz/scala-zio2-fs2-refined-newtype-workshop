package app.infrastructure

import app.domain.analysis.ProductAnalysisService
import app.domain.stats.ProductStatisticsService
import zio.UIO

object ProductAnalysisConfig {

  def inMemoryService: UIO[ProductAnalysisService] =
    ProductStatisticsConfig.inMemoryService.map(service)

  def service(service: ProductStatisticsService): ProductAnalysisService =
    new ProductAnalysisService(service)
}
