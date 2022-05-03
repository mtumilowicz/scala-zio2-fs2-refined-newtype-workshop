package app.domain.analysis

import app.domain.purchase.ProductId

case class ProductRatingAnalysis(
                                  bestRatedProducts: BestRatedProducts,
                                  worstRatedProducts: WorstRatedProducts,
                                  mostRatedProduct: Option[ProductId],
                                  lessRatedProduct: Option[ProductId]
                                )
