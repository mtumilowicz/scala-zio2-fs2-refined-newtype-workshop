package app.infrastructure.module

import app.domain.purchase.ProductId
import app.domain.stats.{ProductStatistics, ProductStatisticsRepository, ProductStatisticsService}
import app.infrastructure.product.ProductStatisticsInMemoryRepository
import zio.{Ref, UIO, ULayer, URLayer, ZLayer}

object ProductStatisticsModule {


  def service: URLayer[ProductStatisticsRepository, ProductStatisticsService] =
    ZLayer.fromFunction(ProductStatisticsService.apply _)

  def inMemoryService: UIO[ProductStatisticsService] =
    inMemoryRepository.map(new ProductStatisticsService(_))


  def inMemoryRepositoryLayer: ULayer[ProductStatisticsRepository] = ZLayer.fromZIO {
    for {
      ref <- Ref.make(Map.empty[ProductId, ProductStatistics])
    } yield ProductStatisticsInMemoryRepository(ref)
  }

  def inMemoryRepository: UIO[ProductStatisticsRepository] = for {
    ref <- Ref.make(Map.empty[ProductId, ProductStatistics])
  } yield ProductStatisticsInMemoryRepository(ref)
}
