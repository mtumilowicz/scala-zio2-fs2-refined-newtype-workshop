package app.infrastructure

import app.gateway.CsvAnalysis
import zio.UIO

object CsvAnalysisConfig {

  def inMemoryAnalysis: UIO[CsvAnalysis] = for {
    statsService <- ProductStatisticsConfig.inMemoryService
    analysisService = ProductAnalysisConfig.service(statsService)
  } yield new CsvAnalysis(analysisService = analysisService)

}
