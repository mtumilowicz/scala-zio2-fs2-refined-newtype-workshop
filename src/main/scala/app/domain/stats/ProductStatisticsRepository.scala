package app.domain.stats

import app.domain.purchase.ProductRating
import zio.UIO

trait ProductStatisticsRepository {

  def index(rate: ProductRating): Option[ProductStatistics]

  def findTop(top: Int, ordering: Ordering[ProductStatistics]): List[ProductStatistics]

  def index2(rate: ProductRating): UIO[Unit]

  def findTop2(top: Int, ordering: Ordering[ProductStatistics]): UIO[List[ProductStatistics]]
}
