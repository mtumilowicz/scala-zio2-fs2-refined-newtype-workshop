package app.domain

import app.domain.common.NewtypeRefinedOps._
import cats.data.{NonEmptyChain, ValidatedNec}
import cats.implicits._
import eu.timepit.refined.api.{Refined, RefinedTypeOps}
import eu.timepit.refined.string.MatchesRegex
import io.estatico.newtype.macros.newtype

package object purchase {

  type ProductIdR = String Refined MatchesRegex["^[a-zA-Z][\\w-]+-\\d{2}$"]

  object ProductIdR extends RefinedTypeOps[ProductIdR, String]

  @newtype case class ProductId(raw: ProductIdR)

  object ProductId {

    def make(productId: String): ValidatedNec[String, ProductId] =
      validate[ProductId](productId)
        .leftMap(_ => NonEmptyChain("ProductId: should start with letter, ends with -dd, where d is digit, " +
          "and contains only digits, letters and hyphens!"))

    implicit val ordering: Ordering[ProductId] =
      Ordering.by[ProductId, String](_.raw.value)

  }

  type BuyerIdR = String Refined MatchesRegex["^[a-zA-Z][a-zA-Z0-9_.-]*$"]

  object BuyerIdR extends RefinedTypeOps[BuyerIdR, String]

  @newtype case class BuyerId(raw: BuyerIdR)

  object BuyerId {
    def make(buyerId: String): ValidatedNec[String, BuyerId] =
      validate[BuyerId](buyerId)
        .leftMap(_ => NonEmptyChain("BuyerId: should start with a letter and contain only digits or letters!"))
  }

  type ShopIdR = String Refined MatchesRegex["^[a-zA-Z][a-zA-Z0-9_.-]*$"]

  object ShopIdR extends RefinedTypeOps[ShopIdR, String]

  @newtype case class ShopId(raw: ShopIdR)

  object ShopId {
    def make(shopId: String): ValidatedNec[String, ShopId] =
      validate[ShopId](shopId)
        .leftMap(_ => NonEmptyChain("ShopId: should start with a letter and contain only digits or letters!"))

  }

}
