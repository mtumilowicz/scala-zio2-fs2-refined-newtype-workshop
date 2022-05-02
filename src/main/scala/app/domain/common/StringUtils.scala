package app.domain.common

object StringUtils {

  implicit class StringChecker(str: String) {
    def lettersOrDigits(): Boolean =
      str.forall(_.isLetterOrDigit)

    def startsWithLetter(): Boolean =
      str.headOption.exists(_.isLetter)
  }

}
