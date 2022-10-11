package app.domain.analysis

import app.domain.purchase.ProductId
import app.domain.rating.ProductRating
import app.domain.stats.ProductStatisticsOrdering._
import app.domain.stats.ProductStatisticsService
import zio.UIO

case class ProductAnalysisService(statsService: ProductStatisticsService) {

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
    top <- statsService.findTail(3, averageRateDesc_productIdAsc).map(_.map(_.productId))
  } yield WorstRatedProducts(top)

  private def findMostRatedProduct(): UIO[Option[ProductId]] =
    statsService.findMax(howManyRatedDesc_productIdAsc)
      .map(_.map(_.productId))

  private def findLessRatedProduct(): UIO[Option[ProductId]] =
    statsService.findMin(howManyRatedDesc_productIdAsc)
      .map(_.map(_.productId))
}
