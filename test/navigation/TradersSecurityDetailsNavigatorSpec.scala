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
import controllers.addItems.traderSecurityDetails.routes
import generators.Generators
import models.{CheckMode, CommonAddress, NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.CommonAddItemsAddressPage
import pages.addItems.traderSecurityDetails._
import pages.safetyAndSecurity.{AddCircumstanceIndicatorPage, AddSafetyAndSecurityConsigneePage, CircumstanceIndicatorPage}

class TradersSecurityDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new TradersSecurityDetailsNavigator

  "In Normal mode" - {

    "Must go from AddSecurityConsignorEoriPage to SecurityConsignorEoriPage when user selects yes" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSecurityConsignorsEoriPage(index), true)
            .success
            .value
          navigator
            .nextPage(AddSecurityConsignorsEoriPage(index), NormalMode, updatedAnswers)
            .mustBe(routes.SecurityConsignorEoriController.onPageLoad(updatedAnswers.id, index, NormalMode))
      }
    }

    "Must go from AddSecurityConsignorEoriPage to SecurityConsignorNamePage when user selects no" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSecurityConsignorsEoriPage(index), false)
            .success
            .value
          navigator
            .nextPage(AddSecurityConsignorsEoriPage(index), NormalMode, updatedAnswers)
            .mustBe(routes.SecurityConsignorNameController.onPageLoad(updatedAnswers.id, index, NormalMode))
      }
    }

    "Must go from SecurityConsignorNamePage to SecurityConsignorAddressPage" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(SecurityConsignorNamePage(index), NormalMode, answers)
            .mustBe(routes.SecurityConsignorAddressController.onPageLoad(answers.id, index, NormalMode))
      }
    }

    "Must go from SecurityConsignorAddressPage to AddSecurityConsigneeEoriPage if there is not 1 consignee for all items" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSafetyAndSecurityConsigneePage, false)
            .success
            .value
          navigator
            .nextPage(CommonAddItemsAddressPage(index, "securityConsignorAddress"), NormalMode, updatedAnswers)
            .mustBe(routes.AddSecurityConsigneesEoriController.onPageLoad(updatedAnswers.id, index, NormalMode))
      }
    }

    "Must go from SecurityConsignorAddressPage to " - {

      "ItemsCheckYourAnswer page if there is 1 consignee for all items" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsigneePage, true)
              .success
              .value
            navigator
              .nextPage(CommonAddItemsAddressPage(index, "securityConsignorAddress"), NormalMode, updatedAnswers)
              .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
        }
      }

      "SecurityConsigneeEoriPage if there is not 1 consignee for all items and user selects 'E' for Circumstance Indicator" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsigneePage, false)
              .success
              .value
              .set(CircumstanceIndicatorPage, "E")
              .success
              .value
            navigator
              .nextPage(CommonAddItemsAddressPage(index, "securityConsignorAddress"), NormalMode, updatedAnswers)
              .mustBe(routes.SecurityConsigneeEoriController.onPageLoad(updatedAnswers.id, index, NormalMode))
        }
      }

      "AddSecurityConsigneeEoriPage if there is not 1 consignee for all items and user does not select 'E' for Circumstance Indicator" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsigneePage, false)
              .success
              .value
              .set(CircumstanceIndicatorPage, "B")
              .success
              .value
            navigator
              .nextPage(CommonAddItemsAddressPage(index, "securityConsignorAddress"), NormalMode, updatedAnswers)
              .mustBe(routes.AddSecurityConsigneesEoriController.onPageLoad(updatedAnswers.id, index, NormalMode))
        }
      }

      "AddSecurityConsigneeEoriPage if there is not 1 consignee for all items and user selects 'No' for Add Circumstance Indicator" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsigneePage, false)
              .success
              .value
              .set(AddCircumstanceIndicatorPage, false)
              .success
              .value
            navigator
              .nextPage(CommonAddItemsAddressPage(index, "securityConsignorAddress"), NormalMode, updatedAnswers)
              .mustBe(routes.AddSecurityConsigneesEoriController.onPageLoad(updatedAnswers.id, index, NormalMode))
        }
      }
    }

    "Must go from SecurityConsignorEoriPage to " - {
      "SecurityConsigneeEoriPage if there is not 1 consignee for all items and user selects 'E' for Circumstance Indicator" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsigneePage, false)
              .success
              .value
              .set(CircumstanceIndicatorPage, "E")
              .success
              .value
            navigator
              .nextPage(SecurityConsignorEoriPage(index), NormalMode, updatedAnswers)
              .mustBe(routes.SecurityConsigneeEoriController.onPageLoad(updatedAnswers.id, index, NormalMode))
        }
      }

      "AddSecurityConsigneeEoriPage if there is not 1 consignee for all items and user does not select 'E' for Circumstance Indicator" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsigneePage, false)
              .success
              .value
              .set(CircumstanceIndicatorPage, "B")
              .success
              .value
            navigator
              .nextPage(SecurityConsignorEoriPage(index), NormalMode, updatedAnswers)
              .mustBe(routes.AddSecurityConsigneesEoriController.onPageLoad(updatedAnswers.id, index, NormalMode))
        }
      }

      "AddSecurityConsigneeEoriPage if there is not 1 consignee for all items and user selects 'No' for Add Circumstance Indicator" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsigneePage, false)
              .success
              .value
              .set(AddCircumstanceIndicatorPage, false)
              .success
              .value
            navigator
              .nextPage(SecurityConsignorEoriPage(index), NormalMode, updatedAnswers)
              .mustBe(routes.AddSecurityConsigneesEoriController.onPageLoad(updatedAnswers.id, index, NormalMode))
        }
      }

      "SecurityConsigneeEoriPage when user selects yes" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSecurityConsigneesEoriPage(index), true)
              .success
              .value
            navigator
              .nextPage(AddSecurityConsigneesEoriPage(index), NormalMode, updatedAnswers)
              .mustBe(routes.SecurityConsigneeEoriController.onPageLoad(updatedAnswers.id, index, NormalMode))
        }
      }
    }

    "Must go from AddSecurityConsigneeEoriPage to SecurityConsigneeNamePage when user selects no" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSecurityConsigneesEoriPage(index), false)
            .success
            .value
          navigator
            .nextPage(AddSecurityConsigneesEoriPage(index), NormalMode, updatedAnswers)
            .mustBe(routes.SecurityConsigneeNameController.onPageLoad(updatedAnswers.id, index, NormalMode))
      }
    }

    "Must go from SecurityConsigneeNamePage to SecurityConsigneeAddressPage" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(SecurityConsigneeNamePage(index), NormalMode, answers)
            .mustBe(routes.SecurityConsigneeAddressController.onPageLoad(answers.id, index, NormalMode))
      }
    }

    "Must go from SecurityConsigneeAddressPage to AddItemsCheckYourAnswers page" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(CommonAddItemsAddressPage(index, "securityConsigneeAddress"), NormalMode, answers)
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
      }
    }

    "Must go from SecurityConsigneeEoriPage to AddItemsCheckYourAnswers page" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(SecurityConsigneeEoriPage(index), NormalMode, answers)
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
      }
    }

  }

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
              .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
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
              .mustBe(routes.SecurityConsignorEoriController.onPageLoad(updatedAnswers.id, index, CheckMode))
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
              .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
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
              .mustBe(routes.SecurityConsignorNameController.onPageLoad(updatedAnswers.id, index, CheckMode))
        }
      }
    }

    "From SecurityConsignorEoriPage to AddItemsCheckYourAnswer page" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(SecurityConsignorEoriPage(index), CheckMode, answers)
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
      }
    }

    "From SecurityConsignorNamePage to AddItemsCheckYourAnswer when an answer already exists for SecurityConsignorAddress page" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val consignorAddress = arbitrary[CommonAddress].sample.value
          val updatedAnswers = answers
            .set(CommonAddItemsAddressPage(index, "securityConsignorAddress"), consignorAddress)
            .success
            .value
          navigator
            .nextPage(SecurityConsignorNamePage(index), CheckMode, updatedAnswers)
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
      }
    }

    "From SecurityConsignorNamePage to SecurityConsignorAddressPage when no answer already exists for SecurityConsignorAddressPage" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .remove(CommonAddItemsAddressPage(index, "securityConsignorAddress"))
            .success
            .value
          navigator
            .nextPage(SecurityConsignorNamePage(index), CheckMode, updatedAnswers)
            .mustBe(routes.SecurityConsignorAddressController.onPageLoad(updatedAnswers.id, index, CheckMode))
      }
    }
  }

  "From SecurityConsignorAddressPage to AddItemsCheckYourAnswer page" in {
    forAll(arbitrary[UserAnswers]) {
      answers =>
        navigator
          .nextPage(CommonAddItemsAddressPage(index, "securityConsignorAddress"), CheckMode, answers)
          .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
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
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
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
            .mustBe(routes.SecurityConsigneeEoriController.onPageLoad(updatedAnswers.id, index, CheckMode))
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
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
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
            .mustBe(routes.SecurityConsigneeNameController.onPageLoad(updatedAnswers.id, index, CheckMode))
      }
    }
  }

  "From SecurityConsigneeEoriPage to AddItemsCheckYourAnswersPage" in {
    forAll(arbitrary[UserAnswers]) {
      answers =>
        navigator
          .nextPage(SecurityConsigneeEoriPage(index), CheckMode, answers)
          .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
    }
  }

  "From SecurityConsigneeAddressPage to AddItemsCheckYourAnswersPage" in {
    forAll(arbitrary[UserAnswers]) {
      answers =>
        navigator
          .nextPage(CommonAddItemsAddressPage(index, "securityConsigneeAddress"), CheckMode, answers)
          .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
    }
  }

}
