package app.infrastructure

import app.domain.rating.RatingService

object RatingConfig {

  def inMemoryService =
    new RatingService(PurchaseConfig.inMemoryService)

}
