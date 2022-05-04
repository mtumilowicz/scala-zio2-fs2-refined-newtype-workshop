package app.infrastructure.module

import app.domain.purchase.PurchaseService
import app.infrastructure.purchase.PurchaseFileRepository

object PurchaseModule {

  def inMemoryService =
    new PurchaseService(new PurchaseFileRepository())

}
