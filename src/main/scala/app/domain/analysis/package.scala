package app.domain

import app.domain.purchase.ProductId
import io.estatico.newtype.macros.newtype

package object analysis {
  @newtype case class BestRatedProducts(raw: List[ProductId])
}
