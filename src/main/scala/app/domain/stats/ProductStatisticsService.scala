package app.domain.stats

import app.domain.rating.ProductRating
import zio.UIO


class ProductStatisticsService(repository: ProductStatisticsRepository) {

  def index(productRating: ProductRating): UIO[Unit] =
    repository.index(productRating)

  def findTop(top: Int, ordering: Ordering[ProductStatistics]): UIO[List[ProductStatistics]] =
    repository.findTop(top, ordering)

  def findTail(top: Int, ordering: Ordering[ProductStatistics]): UIO[List[ProductStatistics]] =
    repository.findTop(top, ordering.reverse)

  def findMax(ordering: Ordering[ProductStatistics]): UIO[Option[ProductStatistics]] =
    repository.findMax(ordering)

  def findMin(ordering: Ordering[ProductStatistics]): UIO[Option[ProductStatistics]] =
    repository.findMax(ordering.reverse)
}
