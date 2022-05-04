package app.gateway.in

import app.domain.purchase._
import app.domain.rating.Rating
import cats.data._
import cats.implicits._

case class PurchaseApiInput(raw: String) {
  def toDomain: ValidatedNec[String, Purchase] = {
    val arr = raw.split(",")
    if (arr.length == 4)
      fromArray(arr)
    else "Line should be of type: BuyerId,ShopId,ProductId,Rating".invalidNec

  }

  private def fromArray(array: Array[String]): ValidatedNec[String, Purchase] = (
    BuyerId.make(array(0)),
    ShopId.make(array(1)),
    ProductId.make(array(2)),
    Rating.make(array(3))
    ).mapN(Purchase)
}
