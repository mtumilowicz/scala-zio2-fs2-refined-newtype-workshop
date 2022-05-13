package app.infrastructure.purchase

import app.domain.purchase.{Purchase, PurchaseRepository}
import app.gateway.in.PurchaseApiInput
import cats.data.ValidatedNec
import fs2.io.file.{Files, Path}
import fs2.text
import zio.Task
import zio.interop.catz._

class PurchaseCsvFileRepository extends PurchaseRepository {
  override def findAll(path: Path): fs2.Stream[Task, ValidatedNec[String, Purchase]] =
    Files[Task].readAll(path)
      .through(text.utf8.decode)
      .through(text.lines)
      .drop(1)
      .map(PurchaseApiInput)
      .map(_.toDomain)
}