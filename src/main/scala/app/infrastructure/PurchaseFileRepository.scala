package app.infrastructure

import app.domain.purchase.{Purchase, PurchaseRepository}
import app.gateway.in.PurchaseApiInput
import cats.data.ValidatedNec
import fs2.io.file.{Files, Path}
import fs2.text
import zio.interop.catz._
import zio.{Task, UIO}

class PurchaseFileRepository extends PurchaseRepository {
  override def findAll(path: Path): fs2.Stream[Task, ValidatedNec[String, Purchase]] =
    Files[Task].readAll(path)
      .through(text.utf8.decode)
      .through(text.lines)
      .drop(1)
      .map(PurchaseApiInput)
      .map(_.toDomain)
}