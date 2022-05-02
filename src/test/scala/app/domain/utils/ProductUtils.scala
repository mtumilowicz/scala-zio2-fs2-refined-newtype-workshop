package app.domain.utils

import app.domain.purchase.{ProductId, ProductRating, Rating}
import cats.data.Validated
import cats.implicits.catsSyntaxTuple2Semigroupal

object ProductUtils {

  def createProductRating(productId: String, rating: String): ProductRating =
    (
      ProductId(productId),
      Rating(rating)
      ).mapN(ProductRating) match {
      case Validated.Valid(a) => a
      case Validated.Invalid(e) => throw new IllegalStateException(e.toString)
    }

}
