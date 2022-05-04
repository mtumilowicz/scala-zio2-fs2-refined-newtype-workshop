package app.domain.purchase

import cats.data.ValidatedNec
import fs2.io.file.Path
import zio.Task

trait PurchaseRepository {
  def findAll(path: Path): fs2.Stream[Task, ValidatedNec[String, Purchase]]
}
