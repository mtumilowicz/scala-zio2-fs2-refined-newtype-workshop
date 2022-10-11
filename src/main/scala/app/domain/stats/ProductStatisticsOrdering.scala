package app.domain.stats

import app.domain.common.PositiveLong
import app.domain.purchase.ProductId
import app.domain.purchase.ProductId._

object ProductStatisticsOrdering {

  private val byHowManyRated = Ordering.by[ProductStatistics, PositiveLong](_.statistics.howManyRated)
  private val byAverage = Ordering.by[ProductStatistics, BigDecimal](_.statistics.average())
  private val byProductId = Ordering.by[ProductStatistics, ProductId](_.productId)

  val howManyRatedDesc_productIdAsc: Ordering[ProductStatistics] =
    createOrdering(byHowManyRated, byProductId)

  val averageRateDesc_productIdAsc: Ordering[ProductStatistics] =
    createOrdering(byAverage, byProductId)

  def createOrdering(by1: Ordering[ProductStatistics], by2: Ordering[ProductStatistics]): Ordering[ProductStatistics] =
    Ordering.by { x: ProductStatistics => (x, x) }(Ordering.Tuple2(by1, by2))

}
