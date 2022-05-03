package app.domain.analysis

import app.domain.purchase.ProductRating
import app.domain.stats.ProductStatisticsOrdering._
import app.domain.stats.ProductStatisticsService
import zio.UIO

class ProductAnalysisService(statsService: ProductStatisticsService) {

  def analyse(): UIO[ProductRatingAnalysis] = for {
    bestRatedProducts <- findBestRatedProducts()
    worstRatedProducts <- findWorstRatedProducts()
    mostRatedProduct <- findMostRatedProduct()
    lessRatedProduct <- findLessRatedProduct()
  } yield ProductRatingAnalysis(
    bestRatedProducts,
    worstRatedProducts,
    mostRatedProduct,
    lessRatedProduct
  )

  def addToStatistics(rating: ProductRating): UIO[Unit] =
    statsService.index(rating)

  private def findBestRatedProducts(): UIO[BestRatedProducts] = for {
    top <- statsService.findTop(3, averageRateDesc_productIdAsc).map(_.map(_.productId))
  } yield BestRatedProducts(top)

  private def findWorstRatedProducts(): UIO[WorstRatedProducts] = for {
    top <- statsService.findTop(3, averageRateAsc_productIdAsc).map(_.map(_.productId))
  } yield WorstRatedProducts(top)

  private def findMostRatedProduct(): UIO[MostRatedProduct] = for {
    top <- statsService.findTop(1, howManyRatedDesc_productIdAsc).map(_.map(_.productId))
  } yield MostRatedProduct(top.headOption)

  private def findLessRatedProduct(): UIO[LessRatedProduct] = for {
    top <- statsService.findTop(1, howManyRatedAsc_productIdAsc).map(_.map(_.productId))
  } yield LessRatedProduct(top.headOption)
}
