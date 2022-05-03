package app.infrastructure

import app.domain.purchase.{ProductId, ProductRating}
import app.domain.stats.{ProductStatistics, ProductStatisticsRepository}
import zio.{Ref, UIO}

import scala.collection.mutable

class ProductStatisticsInMemoryRepository(ref: Ref[Map[ProductId, ProductStatistics]]) extends ProductStatisticsRepository {

  private val map = mutable.Map[ProductId, ProductStatistics]()

  override def findTop(top: Int, ordering: Ordering[ProductStatistics]): List[ProductStatistics] =
    map.values.toList.sorted(ordering).take(top)

  override def findTop2(top: Int, ordering: Ordering[ProductStatistics]): UIO[List[ProductStatistics]] =
    ref.get.map(_.values.toList.sorted(ordering).take(top))

  override def index(rate: ProductRating): Option[ProductStatistics] =
    map.updateWith(rate.productId) {
      case Some(stats) => Some(stats.addRating(rate.rating))
      case None => Some(ProductStatistics.init(rate.productId, rate.rating))
    }

  override def index2(rate: ProductRating): UIO[Unit] =
    ref.update(_.updatedWith(rate.productId) {
      case Some(stats) => Some(stats.addRating(rate.rating))
      case None => Some(ProductStatistics.init(rate.productId, rate.rating))
    })
}
