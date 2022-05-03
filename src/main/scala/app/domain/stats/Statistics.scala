package app.domain.stats

import app.domain.common.PositiveLong
import app.domain.purchase.Rating
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._

case class Statistics(howManyRated: PositiveLong, sumOfRates: PositiveLong) {

  def addRating(rating: Rating): Statistics = {
    Statistics(
      howManyRated = howManyRated.increment(),
      sumOfRates = PositiveLong(Refined.unsafeApply(sumOfRates.raw.value + rating.raw.value))
    )
  }

  def average(): BigDecimal =
    sumOfRates / howManyRated
}

object Statistics {
  def init(rating: Rating): Statistics =
    Statistics(
      howManyRated = PositiveLong.ONE,
      sumOfRates = rating.toPositiveLong
    )
}