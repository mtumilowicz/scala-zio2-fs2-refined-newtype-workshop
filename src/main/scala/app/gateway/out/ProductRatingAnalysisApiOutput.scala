package app.gateway.out

import app.domain.analysis.ProductRatingAnalysis

case class ProductRatingAnalysisApiOutput(
                                           validLines: Int,
                                           invalidLine: Int,
                                           bestRatedProducts: List[String],
                                           worstRatedProducts: List[String],
                                           mostRatedProduct: String,
                                           lessRatedProduct: String
                                         )

object ProductRatingAnalysisApiOutput {
  def fromDomain(analysis: ProductRatingAnalysis, summary: ParsingSummary): ProductRatingAnalysisApiOutput =
    ProductRatingAnalysisApiOutput(
      validLines = summary.validLines,
      invalidLine = summary.invalidLines,
      bestRatedProducts = analysis.bestRatedProducts.raw.map(_.raw.value),
      worstRatedProducts = analysis.worstRatedProducts.raw.map(_.raw.value),
      mostRatedProduct = analysis.mostRatedProduct.raw.map(_.raw.value).orNull,
      lessRatedProduct = analysis.lessRatedProduct.raw.map(_.raw.value).orNull
    )
}
