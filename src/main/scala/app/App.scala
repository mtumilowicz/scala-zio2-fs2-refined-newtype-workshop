package app

import app.domain.analysis.ProductAnalysisService
import app.domain.stats.ProductStatisticsService
import app.gateway.CsvAnalysis
import app.gateway.out.ProductRatingAnalysisApiOutput
import app.infrastructure.{ProductAnalysisConfig, ProductStatisticsConfig}
import fs2.io.file.Path
import zio.{Console, ExitCode, UIO, ZEnvironment, ZIO, ZIOAppArgs, ZIOAppDefault}

object App extends ZIOAppDefault {

  override def run: ZIO[ZIOAppArgs, Any, Any] = for {
    environment <- prepareEnvironment()
    args <- ZIO.service[ZIOAppArgs].map(_.getArgs)
    path = Path(args.headOption.getOrElse("src/main/resources/file.csv"))
    result <- program(path).provideEnvironment(environment)
    _ <- Console.printLine(result.toString)
  } yield ExitCode.success

  def prepareEnvironment(): UIO[ZEnvironment[CsvAnalysis]] = for {
    statisticsService <- ProductStatisticsConfig.inMemoryService
    analysisService = ProductAnalysisConfig.service(statisticsService)
    analysis = new CsvAnalysis(
      analysisService = analysisService
    )
  } yield ZEnvironment(analysis)

  def program(path: Path): ZIO[CsvAnalysis, Throwable, ProductRatingAnalysisApiOutput]= for {
    analysis <- ZIO.service[CsvAnalysis]
    result <- analysis.calculate2(path)
  } yield result

}
