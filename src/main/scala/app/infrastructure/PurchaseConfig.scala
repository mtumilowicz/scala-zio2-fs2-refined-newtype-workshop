package app.infrastructure

import app.domain.purchase.PurchaseService

object PurchaseConfig {

  def inMemoryService =
    new PurchaseService(new PurchaseFileRepository())

}
