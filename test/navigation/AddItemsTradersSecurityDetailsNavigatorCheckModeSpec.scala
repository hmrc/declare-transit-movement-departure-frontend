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
import controllers.addItems.traderSecurityDetails.routes
import generators.Generators
import models.{CheckMode, CommonAddress, UserAnswers}
import navigation.annotations.addItemsNavigators.AddItemsTradersSecurityDetailsNavigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.addItems.traderSecurityDetails._

class AddItemsTradersSecurityDetailsNavigatorCheckModeSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new AddItemsTradersSecurityDetailsNavigator

  "In CheckMode" - {

    "Must go from AddSecurityConsignorsEori page" - {

      "To CheckYourAnswers page when selects Yes and an answer already exists for SecurityConsignorEori" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(SecurityConsignorEoriPage(index), "GB123456")
              .success
              .value
              .set(AddSecurityConsignorsEoriPage(index), true)
              .success
              .value
            navigator
              .nextPage(AddSecurityConsignorsEoriPage(index), CheckMode, updatedAnswers)
              .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
        }
      }
      "To SecurityConsignorEoriPage when selects Yes and no answer already exists for SecurityConsignorEori" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSecurityConsignorsEoriPage(index), true)
              .success
              .value
              .remove(SecurityConsignorEoriPage(index))
              .success
              .value
            navigator
              .nextPage(AddSecurityConsignorsEoriPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.SecurityConsignorEoriController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
        }
      }

      "To CheckYourAnswers page when selects No and an answer already exists for SecurityConsignorName" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSecurityConsignorsEoriPage(index), false)
              .success
              .value
              .set(SecurityConsignorNamePage(index), "TestName")
              .success
              .value
            navigator
              .nextPage(AddSecurityConsignorsEoriPage(index), CheckMode, updatedAnswers)
              .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
        }
      }
      "To SecurityConsignorNamePage when selects No and no answer already exists for SecurityConsignorName" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSecurityConsignorsEoriPage(index), false)
              .success
              .value
              .remove(SecurityConsignorNamePage(index))
              .success
              .value
            navigator
              .nextPage(AddSecurityConsignorsEoriPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.SecurityConsignorNameController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
        }
      }
    }

    "From SecurityConsignorEoriPage to AddItemsCheckYourAnswer page" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(SecurityConsignorEoriPage(index), CheckMode, answers)
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(answers.lrn, index))
      }
    }

    "From SecurityConsignorNamePage to AddItemsCheckYourAnswer when an answer already exists for SecurityConsignorAddress page" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val consignorAddress = arbitrary[CommonAddress].sample.value
          val updatedAnswers = answers
            .set(SecurityConsignorAddressPage(index), consignorAddress)
            .success
            .value
          navigator
            .nextPage(SecurityConsignorNamePage(index), CheckMode, updatedAnswers)
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
      }
    }

    "From SecurityConsignorNamePage to SecurityConsignorAddressPage when no answer already exists for SecurityConsignorAddressPage" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .remove(SecurityConsignorAddressPage(index))
            .success
            .value
          navigator
            .nextPage(SecurityConsignorNamePage(index), CheckMode, updatedAnswers)
            .mustBe(routes.SecurityConsignorAddressController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
      }
    }
  }

  "From SecurityConsignorAddressPage to AddItemsCheckYourAnswer page" in {
    forAll(arbitrary[UserAnswers]) {
      answers =>
        navigator
          .nextPage(SecurityConsignorAddressPage(index), CheckMode, answers)
          .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(answers.lrn, index))
    }
  }

  "From AddSecurityConsigneesEoriPage" - {
    "To AddItemsCheckYours answers page if answer is Yes and an answer already exists for SecurityConsigneesEori" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(SecurityConsigneeEoriPage(index), "GB123456")
            .success
            .value
            .set(AddSecurityConsigneesEoriPage(index), true)
            .success
            .value
          navigator
            .nextPage(AddSecurityConsigneesEoriPage(index), CheckMode, updatedAnswers)
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
      }
    }

    "To SecurityConsigneesEoriPage answers page if answer is Yes and no answer already exists for SecurityConsigneesEori" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSecurityConsigneesEoriPage(index), true)
            .success
            .value
            .remove(SecurityConsigneeEoriPage(index))
            .success
            .value
          navigator
            .nextPage(AddSecurityConsigneesEoriPage(index), CheckMode, updatedAnswers)
            .mustBe(routes.SecurityConsigneeEoriController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
      }
    }
    "To AddItemsCheckYours answers page if answer is No and an answer already exists for SecurityConsigneesName" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSecurityConsigneesEoriPage(index), false)
            .success
            .value
            .set(SecurityConsigneeNamePage(index), "TestName")
            .success
            .value
          navigator
            .nextPage(AddSecurityConsigneesEoriPage(index), CheckMode, updatedAnswers)
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
      }
    }
    "To SecurityConsigneesNamePage  page if answer is No and no answer already exists for SecurityConsigneesName" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSecurityConsigneesEoriPage(index), false)
            .success
            .value
            .remove(SecurityConsigneeNamePage(index))
            .success
            .value
          navigator
            .nextPage(AddSecurityConsigneesEoriPage(index), CheckMode, updatedAnswers)
            .mustBe(routes.SecurityConsigneeNameController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
      }
    }
  }

  "From SecurityConsigneeEoriPage to AddItemsCheckYourAnswersPage" in {
    forAll(arbitrary[UserAnswers]) {
      answers =>
        navigator
          .nextPage(SecurityConsigneeEoriPage(index), CheckMode, answers)
          .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(answers.lrn, index))
    }
  }

  "From SecurityConsigneeAddressPage to AddItemsCheckYourAnswersPage" in {
    forAll(arbitrary[UserAnswers]) {
      answers =>
        navigator
          .nextPage(SecurityConsigneeAddressPage(index), CheckMode, answers)
          .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(answers.lrn, index))
    }
  }

}
