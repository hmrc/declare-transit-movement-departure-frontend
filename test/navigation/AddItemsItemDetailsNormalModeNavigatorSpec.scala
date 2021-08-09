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
import controllers.addItems.traderDetails.{routes => traderRoutes}
import generators.Generators
import models.{Index, NormalMode, UserAnswers}
import navigation.annotations.addItemsNavigators.AddItemsItemDetailsNavigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.addItems._
import pages.traderDetails.{AddConsigneePage, AddConsignorPage}

class AddItemsItemDetailsNormalModeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersSpecHelper {
  // format: off
  val navigator = new AddItemsItemDetailsNavigator

  "Add Items section" - {

    "in normal mode" - {
      "must go from Confirm start add items page to Item description page when selected 'Yes'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(ConfirmStartAddItemsPage, true).success.value
            navigator
              .nextPage(ConfirmStartAddItemsPage, NormalMode, updatedAnswers)
              .mustBe(controllers.addItems.itemDetails.routes.ItemDescriptionController.onPageLoad(updatedAnswers.lrn, Index(0), NormalMode))
        }
      }
      "must go from Confirm start add items page to Pre Task List page when selected 'Yes'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(ConfirmStartAddItemsPage, false).success.value
            navigator
              .nextPage(ConfirmStartAddItemsPage, NormalMode, updatedAnswers)
              .mustBe(controllers.routes.DeclarationSummaryController.onPageLoad(updatedAnswers.lrn))
        }
      }

      "must go from item description page to total gross mass page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ItemDescriptionPage(index), NormalMode, answers)
              .mustBe(controllers.addItems.itemDetails.routes.ItemTotalGrossMassController.onPageLoad(answers.lrn, index, NormalMode))
        }
      }

      "must go from total gross mass page to add total net mass page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ItemTotalGrossMassPage(index), NormalMode, answers)
              .mustBe(controllers.addItems.itemDetails.routes.AddTotalNetMassController.onPageLoad(answers.lrn, index, NormalMode))
        }
      }

      "must go from add total net mass page to total net mass page if the answer is 'Yes' and no answer exists" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddTotalNetMassPage(index), true).success.value
              .remove(TotalNetMassPage(index)).success.value
            navigator
              .nextPage(AddTotalNetMassPage(index), NormalMode, updatedAnswers)
              .mustBe(controllers.addItems.itemDetails.routes.TotalNetMassController.onPageLoad(answers.lrn, index, NormalMode))
        }
      }

      "must go from add total net mass page to IsCommodityCodeKnownPage if the answer is 'No'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddTotalNetMassPage(index), false).success.value
              .remove(TotalNetMassPage(index)).success.value
            navigator
              .nextPage(AddTotalNetMassPage(index), NormalMode, updatedAnswers)
              .mustBe(controllers.addItems.itemDetails.routes.IsCommodityCodeKnownController.onPageLoad(answers.lrn, index, NormalMode))
        }
      }

      "must go from IsCommodityCodeKnownPage" - {
        "when the answer is 'No' to" - {
          "Consignor's Eori when there is no Consignor for all items" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .unsafeSetVal(IsCommodityCodeKnownPage(index))(false)
                  .unsafeSetVal(AddConsignorPage)(false)

                navigator
                  .nextPage(IsCommodityCodeKnownPage(index), NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(answers.lrn, index, NormalMode))
            }
          }

          "Consignee's Eori when there is a Consignor for all items and no Consignee for all items" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .unsafeSetVal(IsCommodityCodeKnownPage(index))(false)
                  .unsafeSetVal(AddConsignorPage)(true)
                  .unsafeSetVal(AddConsigneePage)(false)

                navigator
                  .nextPage(IsCommodityCodeKnownPage(index), NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(answers.lrn, index, NormalMode))
            }
          }

          "Package type when there is  a Consignor for all items and a Consignee for all items" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .unsafeSetVal(IsCommodityCodeKnownPage(index))(false)
                  .unsafeSetVal(AddConsignorPage)(true)
                  .unsafeSetVal(AddConsigneePage)(true)

                navigator
                  .nextPage(IsCommodityCodeKnownPage(index), NormalMode, updatedAnswers)
                  .mustBe(controllers.addItems.packagesInformation.routes.PackageTypeController.onPageLoad(answers.lrn, index, Index(0), NormalMode))
            }
          }

        }

        "when the answer is 'Yes'" - {
          "to CommodityCode" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(IsCommodityCodeKnownPage(index), true).success.value
                navigator
                  .nextPage(IsCommodityCodeKnownPage(index), NormalMode, updatedAnswers)
                  .mustBe(controllers.addItems.itemDetails.routes.CommodityCodeController.onPageLoad(answers.lrn, index, NormalMode))
            }
          }
        }
      }


      "must go from CommodityCodePage to" - {
        "Consignor's Eori when there is no Consignor for all items" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(AddConsignorPage)(false)
                .unsafeSetVal(AddConsigneePage)(false)


              navigator
                .nextPage(CommodityCodePage(index), NormalMode, updatedAnswers)
                .mustBe(traderRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
          }
        }

        "Consignee's Eori when there is a Consignor for all items and no Consignee for all items" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(AddConsignorPage)(true)
                .unsafeSetVal(AddConsigneePage)(false)

              navigator
                .nextPage(CommodityCodePage(index), NormalMode, updatedAnswers)
                .mustBe(traderRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(answers.lrn, index, NormalMode))
          }
        }

        "Package type when there is  a Consignor for all items and a Consignee for all items" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsCommodityCodeKnownPage(index))(false)
                .unsafeSetVal(AddConsignorPage)(true)
                .unsafeSetVal(AddConsigneePage)(true)

              navigator
                .nextPage(CommodityCodePage(index), NormalMode, updatedAnswers)
                .mustBe(controllers.addItems.packagesInformation.routes.PackageTypeController.onPageLoad(answers.lrn, index, Index(0), NormalMode))
          }
        }
      }

    }
  }
}