package app.infrastructure.module

import app.domain.purchase.{PurchaseRepository, PurchaseService}
import app.infrastructure.purchase.PurchaseCsvFileRepository
import zio.{URLayer, ZLayer}

object PurchaseModule {

  def serviceLayer: URLayer[PurchaseRepository, PurchaseService] =
    ZLayer.fromFunction(PurchaseService.apply _)

  def csvRepository = ZLayer.succeed(new PurchaseCsvFileRepository())

  def inMemoryService =
    PurchaseService(new PurchaseCsvFileRepository())

}
