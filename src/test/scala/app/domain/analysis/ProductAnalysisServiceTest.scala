package app.domain.analysis

import app.domain.purchase.ProductId
import app.domain.stats.ProductStatisticsService
import app.domain.utils.ProductUtils
import app.infrastructure.module.{CsvAnalysisModule, ProductAnalysisModule, ProductStatisticsModule, PurchaseModule, RatingModule}
import eu.timepit.refined.auto._
import zio.{Scope, ZIO}
import zio.test.Assertion._
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assert}

object ProductAnalysisServiceTest extends ZIOSpecDefault {

  override def spec: Spec[Any, Any] =
    suite("analyze product stats")(
      emptyDbTest,
      nonEmptyDbTest,
    ).provide(
      ProductAnalysisModule.service,
      ProductStatisticsModule.service,
      ProductStatisticsModule.inMemoryRepository,
    )

  private val emptyDbTest = test("analyzing empty db should result in empty object") {
    for {
      //    Given("create empty")
      service <- ZIO.service[ProductAnalysisService]

      //    When("analyzing empty db")
      result <- service.analyse()

      //    Then("empty object")
    } yield assert(result.bestRatedProducts.raw)(isEmpty) &&
      assert(result.worstRatedProducts.raw)(isEmpty) &&
      assert(result.mostRatedProduct)(isNone) &&
      assert(result.lessRatedProduct)(isNone)
  }

  private val nonEmptyDbTest = test("analysing full-packed db should result in correct object") {
    for {
      // Given("create empty")
      statsService <- ZIO.service[ProductStatisticsService]
      service <- ZIO.service[ProductAnalysisService]

      // And("add ratings with 5 x 1, avg = 1")
      _ <- statsService.index(ProductUtils.createProductRating("product1-01", 1L))
      _ <- statsService.index(ProductUtils.createProductRating("product1-01", 1L))
      _ <- statsService.index(ProductUtils.createProductRating("product1-01", 1L))
      _ <- statsService.index(ProductUtils.createProductRating("product1-01", 1L))
      _ <- statsService.index(ProductUtils.createProductRating("product1-01", 1L))

      // And("add ratings with 3 x 2, avg = 2")
      _ <- statsService.index(ProductUtils.createProductRating("product2-01", 2L))
      _ <- statsService.index(ProductUtils.createProductRating("product2-01", 2L))
      _ <- statsService.index(ProductUtils.createProductRating("product2-01", 2L))

      // And("add ratings with 2 x 2, 2 x 3, avg = 2.5")
      _ <- statsService.index(ProductUtils.createProductRating("product4-01", 2L))
      _ <- statsService.index(ProductUtils.createProductRating("product4-01", 2L))
      _ <- statsService.index(ProductUtils.createProductRating("product4-01", 3L))
      _ <- statsService.index(ProductUtils.createProductRating("product4-01", 3L))

      //  And("add ratings with 4 x 5, avg = 5")
      _ <- statsService.index(ProductUtils.createProductRating("product3-01", 5L))
      _ <- statsService.index(ProductUtils.createProductRating("product3-01", 5L))
      _ <- statsService.index(ProductUtils.createProductRating("product3-01", 5L))
      _ <- statsService.index(ProductUtils.createProductRating("product3-01", 5L))

      // And("expected results")
      expectedBestRatedProducts = List(ProductId("product2-01"), ProductId("product4-01"), ProductId("product3-01"))
      expectedWorstRatedProducts = List(ProductId("product4-01"), ProductId("product2-01"), ProductId("product1-01"))
      expectedMostRatedProduct = ProductId("product1-01")
      expectedLessRatedProduct = ProductId("product2-01")

      //    When("analyzing empty db")
      result <- service.analyse()

      //    Then("correct result")
      bestRatedProducts = result.bestRatedProducts.raw
      worstRatedProducts = result.worstRatedProducts.raw
      mostRatedProduct = result.mostRatedProduct
      lessRatedProduct = result.lessRatedProduct
    } yield
      assert(bestRatedProducts)(equalTo(expectedBestRatedProducts)) &&
        assert(worstRatedProducts)(equalTo(expectedWorstRatedProducts)) &&
        assert(mostRatedProduct)(isSome(equalTo(expectedMostRatedProduct))) &&
        assert(lessRatedProduct)(isSome(equalTo(expectedLessRatedProduct)))

  }
}
