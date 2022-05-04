package app.domain.stats

import app.domain.rating.ProductRating
import zio.UIO

trait ProductStatisticsRepository {

  def index(rate: ProductRating): UIO[Unit]

  def findTop(top: Int, ordering: Ordering[ProductStatistics]): UIO[List[ProductStatistics]]

  def findMax(ordering: Ordering[ProductStatistics]): UIO[Option[ProductStatistics]]
}
