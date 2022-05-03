package app.infrastructure

import app.gateway.CsvAnalysisService
import zio.UIO

object CsvAnalysisConfig {

  def inMemoryAnalysis: UIO[CsvAnalysisService] = for {
    statsService <- ProductStatisticsConfig.inMemoryService
    analysisService = ProductAnalysisConfig.service(statsService)
  } yield new CsvAnalysisService(analysisService = analysisService)

}
