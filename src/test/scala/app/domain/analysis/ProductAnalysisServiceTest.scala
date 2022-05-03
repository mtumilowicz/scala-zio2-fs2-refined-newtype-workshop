package app.domain.analysis

import app.domain.purchase.ProductId
import app.domain.utils.ProductUtils
import app.infrastructure.{ProductAnalysisConfig, ProductStatisticsConfig}
import eu.timepit.refined.auto._
import zio.Scope
import zio.test.Assertion._
import zio.test.{TestEnvironment, ZIOSpecDefault, ZSpec, assert}

object ProductAnalysisServiceTest extends ZIOSpecDefault {

  val emptyDbTest = test("analyzing empty db should result in empty object") {
    for {
      //    Given("create empty")
      service <- ProductAnalysisConfig.inMemoryService

      //    When("analyzing empty db")
      result <- service.analyse()

      //    Then("empty object")
    } yield assert(result.bestRatedProducts.raw)(isEmpty) &&
      assert(result.worstRatedProducts.raw)(isEmpty) &&
      assert(result.mostRatedProduct)(isNone) &&
      assert(result.lessRatedProduct)(isNone)
  }

  val nonEmptyDbTest = test("analysing full-packed db should result in correct object") {
    for {
      // Given("create empty")
      statsService <- ProductStatisticsConfig.inMemoryService
      service = ProductAnalysisConfig.service(statsService)

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

      //    When("analyzing empty db")
      result <- service.analyse()

      //    Then("correct result")
      bestRatedProducts = result.bestRatedProducts.raw
      worstRatedProducts = result.worstRatedProducts.raw
      mostRatedProduct = result.mostRatedProduct
      lessRatedProduct = result.lessRatedProduct
    } yield
      assert(bestRatedProducts)(equalTo(List(ProductId("product2-01"), ProductId("product4-01"), ProductId("product3-01")))) &&
        assert(worstRatedProducts)(equalTo(List(ProductId("product4-01"), ProductId("product2-01"), ProductId("product1-01")))) &&
        assert(mostRatedProduct)(isSome(equalTo(ProductId("product1-01")))) &&
        assert(lessRatedProduct)(isSome(equalTo(ProductId("product2-01"))))

  }

  val productAnalysisSuite = suite("analyze product stats")(
    emptyDbTest,
    nonEmptyDbTest,
  )

  override def spec: ZSpec[TestEnvironment with Scope, Any] = productAnalysisSuite
}
