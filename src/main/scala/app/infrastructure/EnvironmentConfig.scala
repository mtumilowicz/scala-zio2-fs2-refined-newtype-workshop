package app.infrastructure

import app.gateway.CsvAnalysisService
import zio.{UIO, ZEnvironment}

object EnvironmentConfig {

  val inMemory: UIO[ZEnvironment[CsvAnalysisService]] = for {
    csvAnalysis <- CsvAnalysisConfig.inMemoryAnalysis
  } yield ZEnvironment(csvAnalysis)

}
