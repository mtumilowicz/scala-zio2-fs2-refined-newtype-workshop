package app.domain

import app.domain.common.NewtypeRefinedOps._
import app.domain.common.PositiveLong
import cats.data.{NonEmptyChain, ValidatedNec}
import eu.timepit.refined._
import eu.timepit.refined.api.{Refined, RefinedTypeOps}
import eu.timepit.refined.numeric.Interval
import io.estatico.newtype.macros.newtype
import cats.implicits._
import eu.timepit.refined.string.MatchesRegex

import scala.util.{Failure, Success, Try}

package object purchase {
  type RatingR = Long Refined Interval.Closed[W.`1`.T, W.`5`.T]

  object RatingR extends RefinedTypeOps[RatingR, Long]

  @newtype case class Rating(raw: RatingR) {
    def toPositiveLong: PositiveLong =
      PositiveLong(Refined.unsafeApply(raw.value))
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

  object Rating {
    def make(rating: String): ValidatedNec[String, Rating] =
      Try(rating.toLong) match {
        case Failure(_) => "Rating: should be in range 1-5 inclusive".invalidNec
        case Success(value) => validate[Rating](value)
          .leftMap(_ => NonEmptyChain("Rating: should be in range 1-5 inclusive"))
      }
  }
}
