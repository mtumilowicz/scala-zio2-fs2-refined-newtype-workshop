package app.domain.stats

import app.domain.purchase.ProductRating
import zio.UIO

trait ProductStatisticsRepository {

  def index(rate: ProductRating): UIO[Unit]

  def findTop(top: Int, ordering: Ordering[ProductStatistics]): UIO[List[ProductStatistics]]
}
