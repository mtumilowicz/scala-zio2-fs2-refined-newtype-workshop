package app.infrastructure.environment

import app.gateway.CsvAnalysisService
import app.infrastructure.module.CsvAnalysisModule
import zio.{UIO, ZEnvironment}

object EnvironmentConfig {

  val inMemory: UIO[ZEnvironment[CsvAnalysisService]] = for {
    csvAnalysis <- CsvAnalysisModule.inMemoryService
  } yield ZEnvironment(csvAnalysis)

}
