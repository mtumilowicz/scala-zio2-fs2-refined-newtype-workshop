package app.domain.purchase

import app.domain.common.PositiveLong
import cats.data._
import cats.implicits._

case class Rating private(raw: PositiveLong)

object Rating {

  private val regex = "^[1-5]$".r

  def apply(rating: String): ValidatedNec[String, Rating] =
    if (condition(rating))
      PositiveLong(rating.toInt).map(Rating(_))
    else
      "Rating: should be in range 1-5 inclusive".invalidNec


  private def condition(rating: String): Boolean =
    regex.matches(rating)
}