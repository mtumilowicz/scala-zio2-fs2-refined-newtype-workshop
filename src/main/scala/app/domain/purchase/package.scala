package app.domain

import app.domain.common.NewtypeRefinedOps._
import app.domain.common.PositiveLong
import cats.data.{NonEmptyChain, ValidatedNec}
import cats.implicits._
import eu.timepit.refined._
import eu.timepit.refined.api.{Refined, RefinedTypeOps}
import eu.timepit.refined.numeric.Interval
import eu.timepit.refined.string.MatchesRegex
import io.estatico.newtype.macros.newtype

import scala.util.{Failure, Success, Try}

package object purchase {
  type RatingR = Long Refined Interval.Closed[W.`1`.T, W.`5`.T]

  object RatingR extends RefinedTypeOps[RatingR, Long]

  @newtype case class Rating(raw: RatingR) {
    def toPositiveLong: PositiveLong =
      PositiveLong(Refined.unsafeApply(raw.value))
  }

  object Rating {
    def make(rating: String): ValidatedNec[String, Rating] =
      Try(rating.toLong) match {
        case Failure(_) => "Rating: should be in range 1-5 inclusive".invalidNec
        case Success(value) => validate[Rating](value)
          .leftMap(_ => NonEmptyChain("Rating: should be in range 1-5 inclusive"))
      }
  }

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
