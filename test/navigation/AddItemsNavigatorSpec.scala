/*
 * Copyright 2022 HM Revenue & Customs
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
import config.FrontendAppConfig
import generators.Generators
import models.domain.SealDomain
import models.{Index, NormalMode, UserAnswers}
import navigation.annotations.addItemsNavigators.AddItemsNavigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.addItems.AddAnotherItemPage

class AddItemsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with GuiceOneAppPerSuite {

  val frontendAppConfig = app.injector.instanceOf[FrontendAppConfig]
  val navigator         = new AddItemsNavigator(frontendAppConfig)
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


      "must go from AddAnotherItemPage to DeclarationSummaryController when answer we've reached the max no of Items" in {
        forAll(arbitrary[UserAnswers], arbitrary[SealDomain], arbitrary[SealDomain], arbitrary[SealDomain]) {
          (userAnswers, seal1, seal2, seal3) =>
            val updatedAnswers = userAnswers
              .set(AddAnotherItemPage, true).toOption.value
              .set(ItemDescriptionPage(index), "test")
              .success
              .value
              .set(ItemDescriptionPage(Index(1)), "test")
              .success
              .value
              .set(ItemDescriptionPage(Index(2)), "test")
              .success
              .value

            navigator
              .nextPage(AddAnotherItemPage, NormalMode, updatedAnswers)
              .mustBe(controllers.routes.DeclarationSummaryController.onPageLoad(updatedAnswers.lrn))
        }
      }
    }
  }
  // format: on
}
