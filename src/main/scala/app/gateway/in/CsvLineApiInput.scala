package app.gateway.in

import app.domain.purchase._
import cats.data._
import cats.implicits._

case class CsvLineApiInput(raw: String) {
  def toDomain: ValidatedNec[String, Purchase] = {
    val arr = raw.split(",")
    if (arr.length == 4)
      fromArray(arr)
    else "Line should be of type: BuyerId,ShopId,ProductId,Rating".invalidNec

  }

  private def fromArray(array: Array[String]): ValidatedNec[String, Purchase] = (
    BuyerId(array(0)),
    ShopId(array(1)),
    ProductId.make(array(2)),
    Rating.make(array(3))
    ).mapN(Purchase)
}
