package app.infrastructure.module

import app.domain.purchase.{PurchaseRepository, PurchaseService}
import app.infrastructure.purchase.PurchaseCsvFileRepository
import zio.{ULayer, URLayer, ZLayer}

object PurchaseModule {

  def service: URLayer[PurchaseRepository, PurchaseService] =
    ZLayer.fromFunction(PurchaseService.apply _)

  def csvRepository: ULayer[PurchaseRepository] = ZLayer.succeed(new PurchaseCsvFileRepository())

}
