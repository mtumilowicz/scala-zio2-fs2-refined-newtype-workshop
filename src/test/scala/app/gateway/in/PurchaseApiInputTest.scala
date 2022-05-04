package app.gateway.in

import app.domain.purchase.{BuyerId, ProductId, ShopId}
import app.domain.rating.Rating
import cats.data.Chain
import cats.implicits._
import eu.timepit.refined.auto._
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.prop.TableDrivenPropertyChecks

class PurchaseApiInputTest extends AnyFeatureSpec with GivenWhenThen with TableDrivenPropertyChecks {

  Feature("Parsing csv line into domain Purchase") {
    Scenario("parsing empty line should fail") {
      Given("create empty line")
      val line = PurchaseApiInput("")

      When("validated")
      val validated = line.toDomain

      Then("verify that error")
      validated shouldBe "Line should be of type: BuyerId,ShopId,ProductId,Rating".invalidNec
    }

    Scenario("parsing line with empty columns should fail") {
      Given("create line with empty columns")
      val line = PurchaseApiInput(",,,")

      When("validated")
      val validated = line.toDomain

      Then("verify that error")
      validated shouldBe "Line should be of type: BuyerId,ShopId,ProductId,Rating".invalidNec
    }

    Scenario("parsing not full line should fail") {
      Given("create line with empty columns")
      val line = PurchaseApiInput("buyer1,,product-00,3")

      When("validated")
      val validated = line.toDomain

      Then("verify that error")
      validated shouldBe "ShopId: should start with a letter and contain only digits or letters!".invalidNec
    }

    Scenario("parsing malformed line should fail - buyerId, shopId, productId should start with letter") {
      Given("create malformed line")
      val line = PurchaseApiInput("1buyer,2shop,3product-sd-11,0")

      When("validated")
      val validated = line.toDomain

      Then("verify that error")
      validated shouldBe Chain(
        "BuyerId: should start with a letter and contain only digits or letters!",
        "ShopId: should start with a letter and contain only digits or letters!",
        "ProductId: should start with letter, ends with -dd, where d is digit, and contains only digits, letters and hyphens!",
        "Rating: should be in range 1-5 inclusive"
      )
        .invalid
    }

    Scenario("parsing malformed line should fail - improper characters") {
      Given("create malformed line")
      val line = PurchaseApiInput("b@uyer,s!hop,pro#duct-sd-11,d")

      When("validated")
      val validated = line.toDomain

      Then("verify that error")
      validated shouldBe Chain(
        "BuyerId: should start with a letter and contain only digits or letters!",
        "ShopId: should start with a letter and contain only digits or letters!",
        "ProductId: should start with letter, ends with -dd, where d is digit, and contains only digits, letters and hyphens!",
        "Rating: should be in range 1-5 inclusive"
      )
        .invalid
    }

    Scenario("parsing malformed line should fail - rating should be in range 1-5 inclusive") {
      Given("create malformed line")
      val lines = Table(
        "buyer,shop,product-sd-11,0",
        "buyer,shop,product-sd-11,-1",
        "buyer,shop,product-sd-11,6"
      )

      Then("verify that error")
      forAll(lines) { line =>
        PurchaseApiInput(line).toDomain shouldBe "Rating: should be in range 1-5 inclusive".invalidNec

      }
    }

    Scenario("parsing malformed line should fail - productId should end with hyphen and 2 digits") {
      Given("create malformed line")
      val lines = Table(
        "buyer,shop,product-sd-1,1",
        "buyer,shop,product-sd-1d,1",
        "buyer,shop,product-sd-d1,1",
        "buyer,shop,product-sd-dd,1",
        "buyer,shop,productdd11,1"
      )

      Then("verify that error")
      forAll(lines) { line =>
        PurchaseApiInput(line).toDomain shouldBe "ProductId: should start with letter, ends with -dd, where d is digit, and contains only digits, letters and hyphens!".invalidNec
      }
    }

    Scenario("create purchase from correct line") {
      Given("create correct line")
      val line = PurchaseApiInput("buyer1,veloshop,chain-01,4")

      When("validated")
      val validated = line.toDomain

      Then("verify that purchase was created correctly")
      val purchase = validated.getOrElse(null)
      purchase.buyerId shouldBe BuyerId("buyer1")
      purchase.shopId shouldBe ShopId("veloshop")
      purchase.productId shouldBe ProductId("chain-01")
      purchase.rating shouldBe Rating(4L)
    }

  }

}
