package app.infrastructure.module

import app.domain.purchase.PurchaseService
import app.domain.rating.RatingService
import zio.{URLayer, ZLayer}

object RatingModule {

  def serviceLayer: URLayer[PurchaseService, RatingService] = ZLayer.fromFunction(RatingService.apply _)


  def inMemoryService =
    RatingService(PurchaseModule.inMemoryService)

}
