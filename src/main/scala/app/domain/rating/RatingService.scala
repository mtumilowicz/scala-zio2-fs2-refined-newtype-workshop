package app.domain.rating

import app.domain.purchase.PurchaseService
import cats.data.{NonEmptyChain, Validated}
import fs2.io.file.Path
import zio.Task

case class RatingService(purchaseService: PurchaseService) {

  def findAll(path: Path): fs2.Stream[Task, Validated[NonEmptyChain[String], ProductRating]] =
    purchaseService.findAll(path)
      .map(_.map(ProductRating.from))

}
