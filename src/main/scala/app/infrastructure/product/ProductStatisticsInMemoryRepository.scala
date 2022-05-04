package app.infrastructure.product

import app.domain.purchase.ProductId
import app.domain.rating.ProductRating
import app.domain.stats.{ProductStatistics, ProductStatisticsRepository}
import zio.{Ref, UIO}

class ProductStatisticsInMemoryRepository(ref: Ref[Map[ProductId, ProductStatistics]]) extends ProductStatisticsRepository {

  override def findTop(top: Int, ordering: Ordering[ProductStatistics]): UIO[List[ProductStatistics]] =
    ref.get.map(_.values.toList.sorted(ordering).takeRight(top))

  override def index(rate: ProductRating): UIO[Unit] =
    ref.update(_.updatedWith(rate.productId) {
      case Some(stats) => Some(stats.addRating(rate.rating))
      case None => Some(ProductStatistics.init(rate.productId, rate.rating))
    })

  override def findMax(ordering: Ordering[ProductStatistics]): UIO[Option[ProductStatistics]] =
    ref.get.map(_.values.toList.maxOption(ordering))
}
