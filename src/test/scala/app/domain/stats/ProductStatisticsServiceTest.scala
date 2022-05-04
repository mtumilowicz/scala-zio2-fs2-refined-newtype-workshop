package app.domain.stats

import app.domain.common.PositiveLong
import app.domain.purchase.ProductId
import app.domain.stats.ProductStatisticsOrdering._
import app.domain.utils.ProductUtils
import app.infrastructure.ProductStatisticsConfig
import eu.timepit.refined.auto._
import zio.Scope
import zio.test.Assertion.{equalTo, hasSize, isEmpty}
import zio.test.{TestEnvironment, ZIOSpecDefault, ZSpec, assert}

object ProductStatisticsServiceTest extends ZIOSpecDefault {

  override def spec: ZSpec[TestEnvironment with Scope, Any] =
    suite("Find top n entries given specific ordering")(
      emptyDbTest,
      singleEntryDbTest,
      collisionsTest,
      top3HowManyRankedTest
    )

  private val emptyDbTest = test("when called with empty db, should return empty list") {
    for {
      //      Given("create empty stats")
      service <- ProductStatisticsConfig.inMemoryService

      //      Then("should return empty list")
      top <- service.findTop(5, averageRateDesc_productIdAsc)
    } yield assert(top)(isEmpty)
  }

  private val singleEntryDbTest = test("when called with db with single entry, should return single result") {
    for {
      //      Given("create empty stats")
      service <- ProductStatisticsConfig.inMemoryService

      //      And("ratings")
      productRating = ProductUtils.createProductRating("product1-11", 1L)
      expectedOutput = ProductStatistics(productRating.productId, Statistics.init(productRating.rating))

      //      And("add single entry")
      _ <- service.index(productRating)

      //      When("get top5")
      stats <- service.findTop(5, averageRateDesc_productIdAsc)

      //      Then("should return single result")
    } yield assert(stats)(equalTo(List(expectedOutput)))
  }

  private val collisionsTest = test("should return stats sorted by product id in case of collisions") {
    for {
      //      Given("create empty stats")
      service <- ProductStatisticsConfig.inMemoryService

      //      And("ratings")
      productRating1 = ProductUtils.createProductRating("product1-11", 1L)
      productRating2 = ProductUtils.createProductRating("product2-11", 2L)
      productRating3 = ProductUtils.createProductRating("product3-11", 3L)

      //      And("expected output")
      product3Stats = ProductStatistics(productRating3.productId, Statistics.init(productRating3.rating))
      product2Stats = ProductStatistics(productRating2.productId, Statistics.init(productRating2.rating))

      //      And("add entries")
      _ <- service.index(productRating1)
      _ <- service.index(productRating2)
      _ <- service.index(productRating3)

      //      When("get top2")
      stats <- service.findTop(2, howManyRatedDesc_productIdAsc)

      //      Then("should return sorted by name")
    } yield assert(stats)(equalTo(List(product2Stats, product3Stats)))
  }

  private val top3HowManyRankedTest = test("find top3 ordered by howManyRanked desc") {
    for {
      //      Given("create empty stats")
      service <- ProductStatisticsConfig.inMemoryService

      //      And("ratings")
      productRating1 = ProductUtils.createProductRating("product1-11", 1L)
      productRating2 = ProductUtils.createProductRating("product1-11", 1L)
      productRating3 = ProductUtils.createProductRating("product1-11", 1L)
      productRating4 = ProductUtils.createProductRating("product2-11", 2L)

      //      And("add entries")
      _ <- service.index(productRating1)
      _ <- service.index(productRating2)
      _ <- service.index(productRating3)
      _ <- service.index(productRating4)

      //      When("get top3")
      stats <- service.findTop(3, howManyRatedDesc_productIdAsc)

      //      Then("two stats returned")
      top1 = stats(1)
      top2 = stats(0)
    } yield assert(stats)(hasSize(equalTo(2))) &&
      assert(top1.productId)(equalTo(ProductId("product1-11"))) &&
      assert(top1.statistics.howManyRated)(equalTo(PositiveLong(3L))) &&
      assert(top2.productId)(equalTo(ProductId("product2-11"))) &&
      assert(top2.statistics.howManyRated)(equalTo(PositiveLong(1L)))
  }
}
