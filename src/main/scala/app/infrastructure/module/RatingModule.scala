package app.infrastructure.module

import app.domain.purchase.PurchaseService
import app.domain.rating.RatingService
import zio.{URLayer, ZLayer}

object RatingModule {

  def service: URLayer[PurchaseService, RatingService] = ZLayer.fromFunction(RatingService.apply _)

}
