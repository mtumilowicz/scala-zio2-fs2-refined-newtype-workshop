package app.domain.purchase

import app.domain.common.StringUtils._
import cats.data._
import cats.implicits._

case class ShopId private(raw: String)

object ShopId {
  def apply(shopId: String): ValidatedNec[String, ShopId] =
    if (condition(shopId))
      new ShopId(shopId).validNec
    else
      s"ShopId: should start with a letter and contain only digits or letters!".invalidNec

  private def condition(shopId: String): Boolean =
    shopId.startsWithLetter() && shopId.lettersOrDigits()
}
