package app.domain

import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric.Positive
import io.estatico.newtype.macros.newtype

package object common {
  type PositiveLongR = Long Refined Positive
  @newtype case class PositiveLong(raw: PositiveLongR) {
    def increment(): PositiveLong =
      PositiveLong(Refined.unsafeApply(raw + 1))

    def +(i: PositiveLong): PositiveLong =
      PositiveLong(Refined.unsafeApply(raw + i.raw))

    def /(i: PositiveLong): BigDecimal =
      BigDecimal(raw) / BigDecimal(i.raw)
  }
  object PositiveLong {
    def ONE: PositiveLong =
      PositiveLong(1L)

    implicit val ordering: Ordering[PositiveLong] =
      Ordering.by[PositiveLong, Long](_.raw.value)
  }
}
