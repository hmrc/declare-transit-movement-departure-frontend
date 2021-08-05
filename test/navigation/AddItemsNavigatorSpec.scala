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
import controllers.{routes => mainRoutes}
import generators.Generators
import models.{Index, NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.ItemDescriptionPage
import pages.addItems._
import queries.ItemsQuery

class AddItemsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersSpecHelper {
  // format: off
    val navigator = new AddItemsNavigator


    "Add Items section" - {

      "must go from ConfirmRemoveItem page to " - {

        "AddAnotherItem page when 'No' is selected and there are more than one item" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(ItemDescriptionPage(index), "item1").success.value
                .set(ItemDescriptionPage(Index(1)), "item2").success.value
                .set(AddAnotherItemPage, true).success.value
                .set(ConfirmRemoveItemPage, false).success.value
              navigator
                .nextPage(ConfirmRemoveItemPage, NormalMode, updatedAnswers)
                .mustBe(routes.AddAnotherItemController.onPageLoad(updatedAnswers.lrn))
          }
        }

        "AddAnotherItem page when 'Yes' is selected and there are more than one item" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(ItemDescriptionPage(index), "item1").success.value
                .set(ItemDescriptionPage(Index(1)), "item2").success.value
                .set(ConfirmRemoveItemPage, true).success.value
              navigator
                .nextPage(ConfirmRemoveItemPage, NormalMode, updatedAnswers)
                .mustBe(routes.AddAnotherItemController.onPageLoad(updatedAnswers.lrn))
          }
        }

        "ItemDescription page when 'Yes' is selected and when all the items are removed" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = emptyUserAnswers
                .remove(ItemsQuery(index)).success.value
                .set(ConfirmRemoveItemPage, true).success.value
              navigator
                .nextPage(ConfirmRemoveItemPage, NormalMode, updatedAnswers)
                .mustBe(routes.ItemDescriptionController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
          }
        }
      }


      "in check mode" - {


        "must go from AddAnotherItem page to" - {
          "ItemDescription page if the answer is 'Yes'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswer = answers.set(AddAnotherItemPage, false).success.value
                navigator
                  .nextPage(AddAnotherItemPage, NormalMode, updatedAnswer)
                  .mustBe(mainRoutes.DeclarationSummaryController.onPageLoad(answers.lrn))
            }
          }
          "task list page if the answer is 'No'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswer = answers
                  .set(AddAnotherItemPage, true).success.value
                  .set(ItemDescriptionPage(index), "test").success.value
                navigator
                  .nextPage(AddAnotherItemPage, NormalMode, updatedAnswer)
                  .mustBe(routes.ItemDescriptionController.onPageLoad(answers.lrn, Index(1), NormalMode))
            }
          }
        }
        // format: on
    }
  }
}
