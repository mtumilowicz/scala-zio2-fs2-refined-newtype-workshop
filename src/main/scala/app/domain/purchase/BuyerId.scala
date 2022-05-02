package app.domain.purchase

import app.domain.common.StringUtils._
import cats.data.ValidatedNec
import cats.implicits._

case class BuyerId private(raw: String)

object BuyerId {
  def apply(buyerId: String): ValidatedNec[String, BuyerId] =
    if (condition(buyerId))
      new BuyerId(buyerId).validNec
    else
      "BuyerId: should start with a letter and contain only digits or letters!".invalidNec

  private def condition(buyerId: String): Boolean =
    buyerId.startsWithLetter() && buyerId.lettersOrDigits()
}