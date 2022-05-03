package app.domain.analysis

import app.domain.purchase.ProductId
import app.domain.utils.ProductUtils
import app.infrastructure.{ProductAnalysisConfig, ProductStatisticsConfig}
import zio.Scope
import zio.test.Assertion._
import zio.test.{TestEnvironment, ZIOSpecDefault, ZSpec, assert}
import eu.timepit.refined.auto._

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
      assert(result.mostRatedProduct.raw)(isNone) &&
      assert(result.lessRatedProduct.raw)(isNone)
  }

  val nonEmptyDbTest = test("analysing full-packed db should result in correct object") {
    for {
      // Given("create empty")
      statsService <- ProductStatisticsConfig.inMemoryService
      service = ProductAnalysisConfig.service(statsService)

      // And("add ratings with 5 x 1, avg = 1")
      _ <- statsService.index(ProductUtils.createProductRating("product1-01", "1"))
      _ <- statsService.index(ProductUtils.createProductRating("product1-01", "1"))
      _ <- statsService.index(ProductUtils.createProductRating("product1-01", "1"))
      _ <- statsService.index(ProductUtils.createProductRating("product1-01", "1"))
      _ <- statsService.index(ProductUtils.createProductRating("product1-01", "1"))

      // And("add ratings with 3 x 2, avg = 2")
      _ <- statsService.index(ProductUtils.createProductRating("product2-01", "2"))
      _ <- statsService.index(ProductUtils.createProductRating("product2-01", "2"))
      _ <- statsService.index(ProductUtils.createProductRating("product2-01", "2"))

      // And("add ratings with 2 x 2, 2 x 3, avg = 2.5")
      _ <- statsService.index(ProductUtils.createProductRating("product4-01", "2"))
      _ <- statsService.index(ProductUtils.createProductRating("product4-01", "2"))
      _ <- statsService.index(ProductUtils.createProductRating("product4-01", "3"))
      _ <- statsService.index(ProductUtils.createProductRating("product4-01", "3"))

      //  And("add ratings with 4 x 5, avg = 5")
      _ <- statsService.index(ProductUtils.createProductRating("product3-01", "5"))
      _ <- statsService.index(ProductUtils.createProductRating("product3-01", "5"))
      _ <- statsService.index(ProductUtils.createProductRating("product3-01", "5"))
      _ <- statsService.index(ProductUtils.createProductRating("product3-01", "5"))

      //    When("analyzing empty db")
      result <- service.analyse()

      //    Then("correct result")
      bestRatedProducts = result.bestRatedProducts.raw
      worstRatedProducts = result.worstRatedProducts.raw
      mostRatedProduct = result.mostRatedProduct.raw
      lessRatedProduct = result.lessRatedProduct.raw
    } yield
      //      And("best rated products")
      assert(bestRatedProducts)(hasSize(equalTo(3))) &&
        assert(bestRatedProducts(0).raw.value)(equalTo("product3-01")) &&
        assert(bestRatedProducts(1).raw.value)(equalTo("product4-01")) &&
        assert(bestRatedProducts(2).raw.value)(equalTo("product2-01")) &&
        //        And("worst rated products")
        assert(worstRatedProducts)(hasSize(equalTo(3))) &&
        assert(worstRatedProducts(0).raw.value)(equalTo("product1-01")) &&
        assert(worstRatedProducts(1).raw.value)(equalTo("product2-01")) &&
        assert(worstRatedProducts(2).raw.value)(equalTo("product4-01")) &&
        //        And("most rated product")
        assert(mostRatedProduct)(isSome(equalTo(ProductId("product1-01")))) &&
        //        And("less rated product")
        assert(lessRatedProduct)(isSome(equalTo(ProductId("product2-01"))))

  }

  val productAnalysisSuite = suite("analyze product stats")(
    emptyDbTest,
    nonEmptyDbTest,
  )

  override def spec: ZSpec[TestEnvironment with Scope, Any] = productAnalysisSuite
}
