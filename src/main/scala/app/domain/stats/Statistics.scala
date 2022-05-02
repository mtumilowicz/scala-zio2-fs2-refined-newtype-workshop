package app.domain.stats

import app.domain.common.PositiveLong
import app.domain.purchase.Rating

case class Statistics(howManyRated: PositiveLong, sumOfRates: PositiveLong) {

  def addRating(rating: Rating): Statistics =
    Statistics(
      howManyRated = howManyRated.increment(),
      sumOfRates = sumOfRates + rating.raw
    )

  def average(): BigDecimal =
    sumOfRates / howManyRated
}

object Statistics {
  def init(rating: Rating): Statistics =
    Statistics(
      howManyRated = PositiveLong.ONE,
      sumOfRates = rating.raw
    )
}