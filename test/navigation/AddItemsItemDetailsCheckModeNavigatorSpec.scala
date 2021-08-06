/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package navigation

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import controllers.addItems.routes
import controllers.addItems.traderDetails.{routes => traderRoutes}
import generators.Generators
import models.{CheckMode, Index, NormalMode, UserAnswers}
import navigation.annotations.addItemsNavigators.AddItemsItemDetailsNavigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.addItems._

class AddItemsItemDetailsCheckModeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersSpecHelper {
  // format: off
  val navigator = new AddItemsItemDetailsNavigator

  "Add Items section" - {

    "in check mode" - {

      "must go from item description page to Check Your Answers" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ItemDescriptionPage(index), CheckMode, answers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.lrn, index))
        }
      }

      "must go from total grass mass page to Check Your Answers" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ItemTotalGrossMassPage(index), 100.123).success.value
            navigator
              .nextPage(ItemTotalGrossMassPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.lrn, index))
        }
      }

      "must go from total net mass page to Check Your Answers" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(TotalNetMassPage(index), "100").success.value
            navigator
              .nextPage(TotalNetMassPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
        }
      }

      "must go from add total net mass page to total net mass page if the answer is 'Yes' and no previous answer exists" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddTotalNetMassPage(index), true).success.value
              .remove(TotalNetMassPage(index)).success.value
            navigator
              .nextPage(AddTotalNetMassPage(index), CheckMode, updatedAnswers)
              .mustBe(controllers.addItems.itemDetails.routes.TotalNetMassController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
        }
      }

      "must go from add total net mass page to CYA page if the answer is 'Yes' and previous answer exists" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddTotalNetMassPage(index), true).success.value
              .set(TotalNetMassPage(index), "100.123").success.value
            navigator
              .nextPage(AddTotalNetMassPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
        }
      }

      "must go from add total net mass page to CYA page if the answer is 'No' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddTotalNetMassPage(index), false).success.value
            navigator
              .nextPage(AddTotalNetMassPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
        }
      }

      "must go from commodity code page page to Check Your Answers" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(CommodityCodePage(index), CheckMode, answers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.lrn, index))
        }
      }

      "must go from IsCommodityCodeKnownPage to CYA page if the answer is 'Yes' and previous answer exists" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(IsCommodityCodeKnownPage(index), true).success.value
              .set(CommodityCodePage(index), "111111").success.value
            navigator
              .nextPage(IsCommodityCodeKnownPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
        }
      }

      "must go from IsCommodityCodeKnownPage to CommodityCodePage if the answer is 'Yes' and no previous answer exists" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(IsCommodityCodeKnownPage(index), true).success.value
              .remove(CommodityCodePage(index)).success.value
            navigator
              .nextPage(IsCommodityCodeKnownPage(index), CheckMode, updatedAnswers)
              .mustBe(controllers.addItems.itemDetails.routes.CommodityCodeController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
        }
      }

      "must go from IsCommodityCodeKnownPage to CYA if the answer is 'No' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(IsCommodityCodeKnownPage(index), false).success.value
              .remove(CommodityCodePage(index)).success.value
            navigator
              .nextPage(IsCommodityCodeKnownPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
        }
      }
    }
  }
}