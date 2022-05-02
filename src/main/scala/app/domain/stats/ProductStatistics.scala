package app.domain.stats

import app.domain.purchase.{ProductId, Rating}

case class ProductStatistics(productId: ProductId, statistics: Statistics) {

  def addRating(rating: Rating): ProductStatistics =
    copy(statistics = statistics.addRating(rating))
}

object ProductStatistics {
  def init(productId: ProductId, rating: Rating): ProductStatistics =
    ProductStatistics(productId = productId, statistics = Statistics.init(rating))
}