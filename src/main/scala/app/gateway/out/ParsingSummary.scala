package app.gateway.out

case class ParsingSummary(validLines: Int, invalidLines: Int) {

  def validLineSpotted(): ParsingSummary =
    copy(validLines = validLines + 1)

  def invalidLineSpotted(): ParsingSummary =
    copy(invalidLines = invalidLines + 1)
}

object ParsingSummary {
  def zero(): ParsingSummary =
    ParsingSummary(0, 0)
}
