package app.domain

import app.domain.common.NewtypeRefinedOps.validate
import app.domain.common.PositiveLong
import cats.data.{NonEmptyChain, ValidatedNec}
import eu.timepit.refined.W
import eu.timepit.refined.api.{Refined, RefinedTypeOps}
import eu.timepit.refined.numeric.Interval
import io.estatico.newtype.macros.newtype
import cats.implicits._

import scala.util.{Failure, Success, Try}

package object rating {

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

}
