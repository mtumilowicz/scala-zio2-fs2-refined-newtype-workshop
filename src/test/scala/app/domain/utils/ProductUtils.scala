package app.domain.utils

import app.domain
import app.domain.purchase._
import app.domain.rating.{ProductRating, Rating, RatingR}

object ProductUtils {

  def createProductRating(productId: ProductIdR, rating: RatingR): ProductRating =
    domain.rating.ProductRating(productId = ProductId(productId), rating = Rating(rating))

}
