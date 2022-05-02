package app.domain.analysis

case class ProductRatingAnalysis(
                                  bestRatedProducts: BestRatedProducts,
                                  worstRatedProducts: WorstRatedProducts,
                                  mostRatedProduct: MostRatedProduct,
                                  lessRatedProduct: LessRatedProduct
                                )
