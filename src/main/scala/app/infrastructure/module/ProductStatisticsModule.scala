package app.infrastructure.module

import app.domain.purchase.ProductId
import app.domain.stats.{ProductStatistics, ProductStatisticsService}
import app.infrastructure.product.ProductStatisticsInMemoryRepository
import zio.{Ref, UIO}

object ProductStatisticsModule {

  def inMemoryService: UIO[ProductStatisticsService] =
    inMemoryRepository.map(new ProductStatisticsService(_))

  def inMemoryRepository: UIO[ProductStatisticsInMemoryRepository] = for {
    ref <- Ref.make(Map.empty[ProductId, ProductStatistics])
  } yield new ProductStatisticsInMemoryRepository(ref)
}
