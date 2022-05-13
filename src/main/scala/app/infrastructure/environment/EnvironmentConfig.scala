package app.infrastructure.environment

import app.gateway.AnalysisService
import app.infrastructure.module.CsvAnalysisModule
import zio.{UIO, ZEnvironment}

object EnvironmentConfig {

  val inMemory: UIO[ZEnvironment[AnalysisService]] = for {
    csvAnalysis <- CsvAnalysisModule.inMemoryService
  } yield ZEnvironment(csvAnalysis)

}
