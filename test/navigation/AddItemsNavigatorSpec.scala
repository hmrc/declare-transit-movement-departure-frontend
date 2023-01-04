/*
 * Copyright 2023 HM Revenue & Customs
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
import generators.Generators
import models.{Index, NormalMode, UserAnswers}
import navigation.annotations.addItemsNavigators.AddItemsNavigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.addItems.AddAnotherItemPage

class AddItemsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with GuiceOneAppPerSuite {

  val navigator = new AddItemsNavigator()
  // format: off
  "AddItemsNavigator" - {

    "in Normal Mode" - {

      "must go from AddAnotherItemPage to ItemDescriptionController when answer is Yes" in {
        val updatedAnswers = emptyUserAnswers.set(AddAnotherItemPage, true).toOption.value

        navigator
          .nextPage(AddAnotherItemPage, NormalMode, updatedAnswers)
          .mustBe(controllers.addItems.itemDetails.routes.ItemDescriptionController.onPageLoad(updatedAnswers.lrn, Index(0), NormalMode))

      }

      "must go from AddAnotherItemPage to DeclarationSummaryController when answer is No" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(AddAnotherItemPage, false).toOption.value

            navigator
              .nextPage(AddAnotherItemPage, NormalMode, updatedAnswers)
              .mustBe(controllers.routes.DeclarationSummaryController.onPageLoad(updatedAnswers.lrn))
        }
      }
    }
  }
  // format: on
}
