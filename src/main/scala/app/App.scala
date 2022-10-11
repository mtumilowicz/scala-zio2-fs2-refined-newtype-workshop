package app

import app.gateway.AnalysisService
import app.gateway.out.ProductRatingAnalysisApiOutput
import app.infrastructure.environment.EnvironmentConfig
import fs2.io.file.Path
import zio.{Console, ExitCode, ZIO, ZIOAppArgs, ZIOAppDefault}

object App extends ZIOAppDefault {

  val run = for {
    environment <- EnvironmentConfig.inMemory
    path <- pathFromArgsOrDefault
    result <- program(path).provideEnvironment(environment)
    _ <- Console.printLine(result.toString)
  } yield ExitCode.success

  def program(path: Path): ZIO[AnalysisService, Throwable, ProductRatingAnalysisApiOutput] =
    ZIO.serviceWithZIO[AnalysisService](_.calculate(path))

  private def pathFromArgsOrDefault: ZIO[ZIOAppArgs, Nothing, Path] =
    ZIO.serviceWith[ZIOAppArgs](_.getArgs.headOption.getOrElse("src/main/resources/file.csv"))
      .map(Path(_))

}
