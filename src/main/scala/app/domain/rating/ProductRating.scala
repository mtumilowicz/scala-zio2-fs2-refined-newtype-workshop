package app.domain.rating

import app.domain.purchase.{ProductId, Purchase}

case class ProductRating(productId: ProductId, rating: Rating)

object ProductRating {
  def from(purchase: Purchase): ProductRating =
    ProductRating(productId = purchase.productId, rating = purchase.rating)
}
