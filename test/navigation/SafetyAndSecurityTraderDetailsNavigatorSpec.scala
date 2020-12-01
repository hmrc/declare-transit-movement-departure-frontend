/*
 * Copyright 2020 HM Revenue & Customs
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
import controllers.safetyAndSecurity.routes
import generators.Generators
import models.{NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.safetyAndSecurity._

class SafetyAndSecurityTraderDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new SafetyAndSecurityTraderDetailsNavigator

  "in Normal Mode" - {

    "must go from AddSafetyAndSecurityConsignor to AddSafetyAndSecurityConsignee if 'false'" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSafetyAndSecurityConsignorPage, false)
            .success
            .value

          navigator
            .nextPage(AddSafetyAndSecurityConsignorPage, NormalMode, updatedAnswers)
            .mustBe(routes.AddSafetyAndSecurityConsigneeController.onPageLoad(answers.id, NormalMode))
      }
    }

    "must go from AddSafetyAndSecurityConsignorEori to SafetyAndSecurityConsignorEori if 'true'" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSafetyAndSecurityConsignorEoriPage, true)
            .success
            .value

          navigator
            .nextPage(AddSafetyAndSecurityConsignorEoriPage, NormalMode, updatedAnswers)
            .mustBe(routes.SafetyAndSecurityConsignorEoriController.onPageLoad(answers.id, NormalMode))
      }
    }

    "must go from AddSafetyAndSecurityConsignorEori to SafetyAndSecurityConsignorName if 'false'" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSafetyAndSecurityConsignorEoriPage, false)
            .success
            .value

          navigator
            .nextPage(AddSafetyAndSecurityConsignorEoriPage, NormalMode, updatedAnswers)
            .mustBe(routes.SafetyAndSecurityConsignorNameController.onPageLoad(answers.id, NormalMode))
      }
    }

    "must go from SafetyAndSecurityConsignorName to SafetyAndSecurityConsignorAddress" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(SafetyAndSecurityConsignorNamePage, NormalMode, answers)
            .mustBe(routes.SafetyAndSecurityConsignorAddressController.onPageLoad(answers.id, NormalMode))
      }
    }

    "must go from SafetyAndSecurityConsignorEori to AddSafetyAndSecurityConsignee" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(SafetyAndSecurityConsignorEoriPage, NormalMode, answers)
            .mustBe(routes.AddSafetyAndSecurityConsigneeController.onPageLoad(answers.id, NormalMode))
      }
    }

    "must go from AddSafetyAndSecurityConsignee to AddSafetyAndSecurityConsigneeEori if 'true'" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSafetyAndSecurityConsigneePage, true)
            .success
            .value

          navigator
            .nextPage(AddSafetyAndSecurityConsigneePage, NormalMode, updatedAnswers)
            .mustBe(routes.AddSafetyAndSecurityConsigneeEoriController.onPageLoad(answers.id, NormalMode))
      }
    }

    "must go from AddSafetyAndSecurityConsignee to AddCarrier if 'false'" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSafetyAndSecurityConsigneePage, false)
            .success
            .value

          navigator
            .nextPage(AddSafetyAndSecurityConsigneePage, NormalMode, updatedAnswers)
            .mustBe(routes.AddCarrierController.onPageLoad(answers.id, NormalMode))
      }
    }

    "must go from AddSafetyAndSecurityConsigneeEori to SafetyAndSecurityConsigneeEori if 'true'" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSafetyAndSecurityConsigneeEoriPage, true)
            .success
            .value

          navigator
            .nextPage(AddSafetyAndSecurityConsigneeEoriPage, NormalMode, updatedAnswers)
            .mustBe(routes.SafetyAndSecurityConsigneeEoriController.onPageLoad(answers.id, NormalMode))
      }
    }

    "must go from AddSafetyAndSecurityConsigneeEori to SafetyAndSecurityConsigneeName if 'false'" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSafetyAndSecurityConsigneeEoriPage, false)
            .success
            .value

          navigator
            .nextPage(AddSafetyAndSecurityConsigneeEoriPage, NormalMode, updatedAnswers)
            .mustBe(routes.SafetyAndSecurityConsigneeNameController.onPageLoad(answers.id, NormalMode))
      }
    }

    "must go from SafetyAndSecurityConsigneeName to SafetyAndSecurityConsigneeAddress" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(SafetyAndSecurityConsigneeNamePage, NormalMode, answers)
            .mustBe(routes.SafetyAndSecurityConsigneeAddressController.onPageLoad(answers.id, NormalMode))
      }
    }

    "must go from SafetyAndSecurityConsigneeEori to AddCarrier" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(SafetyAndSecurityConsigneeEoriPage, NormalMode, answers)
            .mustBe(routes.AddCarrierController.onPageLoad(answers.id, NormalMode))
      }
    }

    "must go from AddCarrier to AddCarrierEori if 'true'" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddCarrierPage, true)
            .success
            .value

          navigator
            .nextPage(AddCarrierPage, NormalMode, updatedAnswers)
            .mustBe(routes.AddCarrierEoriController.onPageLoad(answers.id, NormalMode))
      }
    }

    "must go from AddCarrier to CheckYourAnswers if 'false'" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddCarrierPage, false)
            .success
            .value

          navigator
            .nextPage(AddCarrierPage, NormalMode, updatedAnswers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(answers.id))
      }
    }

    "must go from AddCarrierEori to CarrierEori if 'true'" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddCarrierEoriPage, true)
            .success
            .value

          navigator
            .nextPage(AddCarrierEoriPage, NormalMode, updatedAnswers)
            .mustBe(routes.CarrierEoriController.onPageLoad(answers.id, NormalMode))
      }
    }

    "must go from AddCarrierEori to CarrierName if 'false'" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddCarrierEoriPage, false)
            .success
            .value

          navigator
            .nextPage(AddCarrierEoriPage, NormalMode, updatedAnswers)
            .mustBe(routes.CarrierNameController.onPageLoad(answers.id, NormalMode))
      }
    }

    "must go from CarrierName to CarrierAddress" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(CarrierNamePage, NormalMode, answers)
            .mustBe(routes.CarrierAddressController.onPageLoad(answers.id, NormalMode))
      }
    }

    "must go from CarrierEori to CheckYourAnswers" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(CarrierEoriPage, NormalMode, answers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(answers.id))
      }
    }

    "must go from AddSafetyAndSecurityConsignor to AddSafetyAndSecurityConsignorEori if 'true'" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSafetyAndSecurityConsignorPage, true)
            .success
            .value

          navigator
            .nextPage(AddSafetyAndSecurityConsignorPage, NormalMode, updatedAnswers)
            .mustBe(routes.AddSafetyAndSecurityConsignorEoriController.onPageLoad(answers.id, NormalMode))
      }
    }
  }
}
