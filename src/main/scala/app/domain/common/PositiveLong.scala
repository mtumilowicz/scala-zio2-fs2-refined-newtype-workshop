package app.domain.common

import cats.data._
import cats.implicits._

case class PositiveLong private(raw: Long) {
  def increment(): PositiveLong =
    copy(raw = raw + 1)

  def +(i: PositiveLong): PositiveLong =
    new PositiveLong(raw + i.raw)

  def /(i: PositiveLong): BigDecimal =
    BigDecimal(raw) / BigDecimal(i.raw)

  def compare(t: PositiveLong): Int = raw compare t.raw
}

object PositiveLong {
  def apply(long: Long): ValidatedNec[String, PositiveLong] =
    if (long > 0) new PositiveLong(long).validNec
    else "Number should be > 0!".invalidNec

  def ONE: PositiveLong =
    new PositiveLong(1)

  implicit val ordering: Ordering[PositiveLong] =
    Ordering.by[PositiveLong, Long](_.raw)
}