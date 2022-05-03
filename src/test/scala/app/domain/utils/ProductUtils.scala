package app.domain.utils

import app.domain.purchase._

object ProductUtils {

  def createProductRating(productId: ProductIdR, rating: RatingR): ProductRating =
    ProductRating(productId = ProductId(productId), rating = Rating(rating))

}
