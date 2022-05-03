package app.domain.stats

import app.domain.purchase.ProductRating
import zio.UIO


class ProductStatisticsService(repository: ProductStatisticsRepository) {

  def index(productRating: ProductRating): UIO[Unit] =
    repository.index(productRating)

  def findTop(top: Int, ordering: Ordering[ProductStatistics]): UIO[List[ProductStatistics]] =
    repository.findTop(top, ordering)
}
