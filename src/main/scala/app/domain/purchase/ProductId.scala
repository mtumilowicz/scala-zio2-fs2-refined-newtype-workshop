package app.domain.purchase

import cats.data._
import cats.implicits._

case class ProductId private(raw: String)

object ProductId {
  private val regex = "^[a-zA-Z][\\w-]+-\\d{2}$".r

  def apply(productId: String): ValidatedNec[String, ProductId] =
    if (condition(productId))
      new ProductId(productId).validNec
    else
      ("ProductId: should start with letter, ends with -dd, where d is digit, " +
        "and contains only digits, letters and hyphens!").invalidNec

  implicit val ordering: Ordering[ProductId] =
    Ordering.by[ProductId, String](_.raw)

  private def condition(productId: String): Boolean =
    regex.matches(productId)
}