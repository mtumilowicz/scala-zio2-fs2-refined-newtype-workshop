package app.infrastructure.module

import app.domain.purchase.PurchaseService
import app.infrastructure.purchase.PurchaseCsvFileRepository

object PurchaseModule {

  def inMemoryService =
    new PurchaseService(new PurchaseCsvFileRepository())

}
