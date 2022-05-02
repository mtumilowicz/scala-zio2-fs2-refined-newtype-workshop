package app.domain.stats

import app.domain.common.PositiveLong
import app.domain.common.PositiveLong._
import app.domain.purchase.ProductId
import app.domain.purchase.ProductId._

import scala.math.Ordering

object ProductStatisticsOrdering {

  private val byHowManyRated = Ordering.by[ProductStatistics, PositiveLong](_.statistics.howManyRated)
  private val byAverage = Ordering.by[ProductStatistics, BigDecimal](_.statistics.average())
  private val byProductId =  Ordering.by[ProductStatistics, ProductId](_.productId)

  val howManyRatedAsc_productIdAsc: Ordering[ProductStatistics] =
    createOrdering(byHowManyRated, byProductId)

  val howManyRatedDesc_productIdAsc: Ordering[ProductStatistics] =
    createOrdering(byHowManyRated.reverse, byProductId)

  val averageRateAsc_productIdAsc: Ordering[ProductStatistics] =
    createOrdering(byAverage, byProductId)

  val averageRateDesc_productIdAsc: Ordering[ProductStatistics] =
    createOrdering(byAverage.reverse, byProductId)

  def createOrdering(by1: Ordering[ProductStatistics], by2: Ordering[ProductStatistics]): Ordering[ProductStatistics] =
    Ordering.by{ x: ProductStatistics => (x, x) }(Ordering.Tuple2(by1, by2))

}
