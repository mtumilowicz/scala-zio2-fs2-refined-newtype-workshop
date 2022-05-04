package app.infrastructure.module

import app.domain.rating.RatingService

object RatingModule {

  def inMemoryService =
    new RatingService(PurchaseModule.inMemoryService)

}
