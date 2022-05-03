package app.infrastructure

import app.domain.purchase.{ProductId, ProductRating}
import app.domain.stats.{ProductStatistics, ProductStatisticsRepository}
import zio.{Ref, UIO}

import scala.collection.mutable

class ProductStatisticsInMemoryRepository(ref: Ref[Map[ProductId, ProductStatistics]]) extends ProductStatisticsRepository {

  override def findTop(top: Int, ordering: Ordering[ProductStatistics]): UIO[List[ProductStatistics]] =
    ref.get.map(_.values.toList.sorted(ordering).take(top))

  override def index(rate: ProductRating): UIO[Unit] =
    ref.update(_.updatedWith(rate.productId) {
      case Some(stats) => Some(stats.addRating(rate.rating))
      case None => Some(ProductStatistics.init(rate.productId, rate.rating))
    })
}
