package app

import app.gateway.AnalysisService
import app.gateway.out.ProductRatingAnalysisApiOutput
import app.infrastructure.module._
import fs2.io.file.Path
import zio.{Console, ZIO, ZIOAppArgs, ZIOAppDefault}

object App extends ZIOAppDefault {

  val run = (for {
    args <- pathFromArgsOrDefault
    result <- program(args)
    _ <- Console.printLine(result)
  } yield ())
    .provideSome(
      CsvAnalysisModule.serviceLayer,
      ProductAnalysisModule.serviceLayer,
      ProductStatisticsModule.service,
      ProductStatisticsModule.inMemoryRepositoryLayer,
      PurchaseModule.serviceLayer,
      PurchaseModule.csvRepository,
      RatingModule.serviceLayer
    )

  def program(path: Path): ZIO[AnalysisService, Throwable, ProductRatingAnalysisApiOutput] =
    ZIO.serviceWithZIO[AnalysisService](_.calculate(path))

  private def pathFromArgsOrDefault: ZIO[ZIOAppArgs, Nothing, Path] =
    ZIO.serviceWith[ZIOAppArgs](_.getArgs.headOption.getOrElse("src/main/resources/file.csv"))
      .map(Path(_))

}
