package app.domain.purchase

import cats.data.ValidatedNec
import fs2.io.file.Path
import zio.Task

class PurchaseService(repository: PurchaseRepository) {
  def findAll(path: Path): fs2.Stream[Task, ValidatedNec[String, Purchase]] =
    repository.findAll(path)
}
