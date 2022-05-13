package app

import app.gateway.AnalysisService
import app.gateway.out.ProductRatingAnalysisApiOutput
import app.infrastructure.environment.EnvironmentConfig
import fs2.io.file.Path
import zio.{Console, ExitCode, ZIO, ZIOAppArgs, ZIOAppDefault}

object App extends ZIOAppDefault {

  val run = for {
    environment <- EnvironmentConfig.inMemory
    args <- ZIO.service[ZIOAppArgs]
    path = pathFromArgsOrDefault(args)
    result <- program(path).provideEnvironment(environment)
    _ <- Console.printLine(result.toString)
  } yield ExitCode.success

  def program(path: Path): ZIO[AnalysisService, Throwable, ProductRatingAnalysisApiOutput] = for {
    analysisService <- ZIO.service[AnalysisService]
    result <- analysisService.calculate(path)
  } yield result

  private def pathFromArgsOrDefault(zioAppArgs: ZIOAppArgs): Path =
    Path(zioAppArgs.getArgs.headOption.getOrElse("src/main/resources/file.csv"))

}
