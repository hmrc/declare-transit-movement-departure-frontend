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
import controllers.safetyAndSecurity.routes
import generators.Generators
import models.reference.{CountryCode, CustomsOffice}
import models.{CheckMode, CommonAddress, NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.OfficeOfDeparturePage
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
            .mustBe(routes.AddSafetyAndSecurityConsigneeController.onPageLoad(answers.lrn, NormalMode))
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
            .mustBe(routes.SafetyAndSecurityConsignorEoriController.onPageLoad(answers.lrn, NormalMode))
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
            .mustBe(routes.SafetyAndSecurityConsignorNameController.onPageLoad(answers.lrn, NormalMode))
      }
    }

    "must go from SafetyAndSecurityConsignorName to SafetyAndSecurityConsignorAddress" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(SafetyAndSecurityConsignorNamePage, NormalMode, answers)
            .mustBe(routes.SafetyAndSecurityConsignorAddressController.onPageLoad(answers.lrn, NormalMode))
      }
    }

    "must go from SafetyAndSecurityConsignorEori to AddSafetyAndSecurityConsignee" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(SafetyAndSecurityConsignorEoriPage, NormalMode, answers)
            .mustBe(routes.AddSafetyAndSecurityConsigneeController.onPageLoad(answers.lrn, NormalMode))
      }
    }

    "must go from AddSafetyAndSecurityConsignee" - {

      "when office at departure is an XI country code" - {

        "to AddSafetyAndSecurityConsigneeEoriController if 'true' and selected 'E' for circumstance indicator" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(AddSafetyAndSecurityConsigneePage, true)
                .success
                .value
                .set(CircumstanceIndicatorPage, "E")
                .success
                .value
                .set(OfficeOfDeparturePage, CustomsOffice("id", "name", CountryCode("XI"), None))
                .success
                .value

              navigator
                .nextPage(AddSafetyAndSecurityConsigneePage, NormalMode, updatedAnswers)
                .mustBe(routes.AddSafetyAndSecurityConsigneeEoriController.onPageLoad(answers.lrn, NormalMode))
          }
        }

        "to AddSafetyAndSecurityConsigneeEoriController if 'true' and did not select 'E' for circumstance indicator" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(AddSafetyAndSecurityConsigneePage, true)
                .success
                .value
                .set(CircumstanceIndicatorPage, "B")
                .success
                .value
                .set(OfficeOfDeparturePage, CustomsOffice("id", "name", CountryCode("XI"), None))
                .success
                .value

              navigator
                .nextPage(AddSafetyAndSecurityConsigneePage, NormalMode, updatedAnswers)
                .mustBe(routes.AddSafetyAndSecurityConsigneeEoriController.onPageLoad(answers.lrn, NormalMode))
          }
        }

        "to AddSafetyAndSecurityConsigneeEoriController if 'true' and selected 'No' to add circumstance indicator" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(AddSafetyAndSecurityConsigneePage, true)
                .success
                .value
                .set(AddCircumstanceIndicatorPage, false)
                .success
                .value
                .set(OfficeOfDeparturePage, CustomsOffice("id", "name", CountryCode("XI"), None))
                .success
                .value

              navigator
                .nextPage(AddSafetyAndSecurityConsigneePage, NormalMode, updatedAnswers)
                .mustBe(routes.AddSafetyAndSecurityConsigneeEoriController.onPageLoad(answers.lrn, NormalMode))
          }
        }

        "to AddCarrier if 'false'" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(AddSafetyAndSecurityConsigneePage, false)
                .success
                .value
                .set(OfficeOfDeparturePage, CustomsOffice("id", "name", CountryCode("XI"), None))
                .success
                .value

              navigator
                .nextPage(AddSafetyAndSecurityConsigneePage, NormalMode, updatedAnswers)
                .mustBe(routes.AddCarrierController.onPageLoad(answers.lrn, NormalMode))
          }
        }
      }

      "when office at departure is anything else" - {

        "to SafetyAndSecurityConsigneeEoriController if 'true' and selected 'E' for circumstance indicator" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(AddSafetyAndSecurityConsigneePage, true)
                .success
                .value
                .set(CircumstanceIndicatorPage, "E")
                .success
                .value
                .set(OfficeOfDeparturePage, CustomsOffice("id", "name", CountryCode("GB"), None))
                .success
                .value

              navigator
                .nextPage(AddSafetyAndSecurityConsigneePage, NormalMode, updatedAnswers)
                .mustBe(routes.SafetyAndSecurityConsigneeEoriController.onPageLoad(answers.lrn, NormalMode))
          }
        }

        "to AddSafetyAndSecurityConsigneeEoriController if 'true' and did not select 'E' for circumstance indicator" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(AddSafetyAndSecurityConsigneePage, true)
                .success
                .value
                .set(CircumstanceIndicatorPage, "B")
                .success
                .value
                .set(OfficeOfDeparturePage, CustomsOffice("id", "name", CountryCode("GB"), None))
                .success
                .value

              navigator
                .nextPage(AddSafetyAndSecurityConsigneePage, NormalMode, updatedAnswers)
                .mustBe(routes.AddSafetyAndSecurityConsigneeEoriController.onPageLoad(answers.lrn, NormalMode))
          }
        }

        "to AddSafetyAndSecurityConsigneeEoriController if 'true' and selected 'No' to add circumstance indicator" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(AddSafetyAndSecurityConsigneePage, true)
                .success
                .value
                .set(AddCircumstanceIndicatorPage, false)
                .success
                .value
                .set(OfficeOfDeparturePage, CustomsOffice("id", "name", CountryCode("GB"), None))
                .success
                .value

              navigator
                .nextPage(AddSafetyAndSecurityConsigneePage, NormalMode, updatedAnswers)
                .mustBe(routes.AddSafetyAndSecurityConsigneeEoriController.onPageLoad(answers.lrn, NormalMode))
          }
        }

        "to AddCarrier if 'false'" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(AddSafetyAndSecurityConsigneePage, false)
                .success
                .value
                .set(OfficeOfDeparturePage, CustomsOffice("id", "name", CountryCode("GB"), None))
                .success
                .value

              navigator
                .nextPage(AddSafetyAndSecurityConsigneePage, NormalMode, updatedAnswers)
                .mustBe(routes.AddCarrierController.onPageLoad(answers.lrn, NormalMode))
          }
        }
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
            .mustBe(routes.SafetyAndSecurityConsigneeEoriController.onPageLoad(answers.lrn, NormalMode))
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
            .mustBe(routes.SafetyAndSecurityConsigneeNameController.onPageLoad(answers.lrn, NormalMode))
      }
    }

    "must go from SafetyAndSecurityConsigneeName to SafetyAndSecurityConsigneeAddress" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(SafetyAndSecurityConsigneeNamePage, NormalMode, answers)
            .mustBe(routes.SafetyAndSecurityConsigneeAddressController.onPageLoad(answers.lrn, NormalMode))
      }
    }

    "must go from SafetyAndSecurityConsigneeEori to AddCarrier" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(SafetyAndSecurityConsigneeEoriPage, NormalMode, answers)
            .mustBe(routes.AddCarrierController.onPageLoad(answers.lrn, NormalMode))
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
            .mustBe(routes.AddCarrierEoriController.onPageLoad(answers.lrn, NormalMode))
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
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(answers.lrn))
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
            .mustBe(routes.CarrierEoriController.onPageLoad(answers.lrn, NormalMode))
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
            .mustBe(routes.CarrierNameController.onPageLoad(answers.lrn, NormalMode))
      }
    }

    "must go from CarrierName to CarrierAddress" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(CarrierNamePage, NormalMode, answers)
            .mustBe(routes.CarrierAddressController.onPageLoad(answers.lrn, NormalMode))
      }
    }

    "must go from CarrierEori to CheckYourAnswers" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(CarrierEoriPage, NormalMode, answers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(answers.lrn))
      }
    }

    "must go from AddSafetyAndSecurityConsignor to AddSafetyAndSecurityConsignorEori if 'true'" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSafetyAndSecurityConsignorPage, true)
            .success
            .value
            .set(CircumstanceIndicatorPage, "circumstanceIndicator")
            .success
            .value
            .set(OfficeOfDeparturePage, CustomsOffice("id", "name", CountryCode("XI"), None))
            .success
            .value

          navigator
            .nextPage(AddSafetyAndSecurityConsignorPage, NormalMode, updatedAnswers)
            .mustBe(routes.AddSafetyAndSecurityConsignorEoriController.onPageLoad(answers.lrn, NormalMode))
      }
    }
  }

  "in Check Mode" - {
    "must go from AddSecurityConsignorPage" - {
      "to CheckYourAnswers when user selects 'No' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsignorPage, false)
              .success
              .value

            navigator
              .nextPage(AddSafetyAndSecurityConsignorPage, CheckMode, updatedAnswers)
              .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "to CheckYourAnswers when user selects 'Yes' and answers already exist for consignor" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsignorPage, true)
              .success
              .value
              .set(AddSafetyAndSecurityConsignorEoriPage, true)
              .success
              .value

            navigator
              .nextPage(AddSafetyAndSecurityConsignorPage, CheckMode, updatedAnswers)
              .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "to AddSafetyAndSecurityConsignorEoriPage when user selects 'Yes' and answers do not exist for consignor" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsignorPage, true)
              .success
              .value
              .remove(AddSafetyAndSecurityConsignorEoriPage)
              .success
              .value

            navigator
              .nextPage(AddSafetyAndSecurityConsignorPage, CheckMode, updatedAnswers)
              .mustBe(routes.AddSafetyAndSecurityConsignorEoriController.onPageLoad(answers.lrn, CheckMode))
        }
      }
    }

    "AddSafetyAndSecurityConsignorEoriPage must" - {
      "go to Check Your Answers page when Yes is selected and answer already exists for SafetyAndSecurityConsignorEoriPage" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsignorEoriPage, true)
              .success
              .value
              .set(SafetyAndSecurityConsignorEoriPage, "GB000000")
              .success
              .value

            navigator
              .nextPage(AddSafetyAndSecurityConsignorEoriPage, CheckMode, updatedAnswers)
              .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }
      "go to SafetyAndSecurityConsignorEoriPage when Yes is selected and no answer already exists for SafetyAndSecurityConsignorEoriPage" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsignorEoriPage, true)
              .success
              .value
              .remove(SafetyAndSecurityConsignorEoriPage)
              .success
              .value

            navigator
              .nextPage(AddSafetyAndSecurityConsignorEoriPage, CheckMode, updatedAnswers)
              .mustBe(routes.SafetyAndSecurityConsignorEoriController.onPageLoad(answers.lrn, CheckMode))
        }
      }
      "go to Check Your Answers page when No is selected and answer already exists for SafetyAndSecurityConsignorNamePage" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsignorEoriPage, false)
              .success
              .value
              .set(SafetyAndSecurityConsignorNamePage, "TestName")
              .success
              .value

            navigator
              .nextPage(AddSafetyAndSecurityConsignorEoriPage, CheckMode, updatedAnswers)
              .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }
      "go to SafetyAndSecurityConsignorNamePage when No is selected and no answer already exists for SafetyAndSecurityConsignorNamePage" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsignorEoriPage, false)
              .success
              .value
              .remove(SafetyAndSecurityConsignorNamePage)
              .success
              .value

            navigator
              .nextPage(AddSafetyAndSecurityConsignorEoriPage, CheckMode, updatedAnswers)
              .mustBe(routes.SafetyAndSecurityConsignorNameController.onPageLoad(answers.lrn, CheckMode))
        }
      }
    }

    "Must go from SafetyAndSecurityConsignorEoriPage to Check Your Answers page" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(SafetyAndSecurityConsignorEoriPage, CheckMode, answers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(answers.lrn))
      }
    }

    "Must go from SafetyAndSecurityConsignorNamePage to Check Your Answers page if answer exists for SafetyAndSecurityConsignorAddressPage" in {
      val consignorAddress = arbitrary[CommonAddress].sample.value
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(SafetyAndSecurityConsignorAddressPage, consignorAddress)
            .success
            .value
          navigator
            .nextPage(SafetyAndSecurityConsignorNamePage, CheckMode, updatedAnswers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
      }
    }

    "Must go from SafetyAndSecurityConsignorNamePage to SafetyAndSecurityConsignorAddressPage if no answer exists for  SafetyAndSecurityConsignorAddressPage" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .remove(SafetyAndSecurityConsignorAddressPage)
            .success
            .value
          navigator
            .nextPage(SafetyAndSecurityConsignorNamePage, CheckMode, updatedAnswers)
            .mustBe(routes.SafetyAndSecurityConsignorAddressController.onPageLoad(updatedAnswers.lrn, CheckMode))
      }
    }

    "Must go from SafetyAndSecurityConsignorAddressPage to Check Your Answers page" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(SafetyAndSecurityConsignorAddressPage, CheckMode, answers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(answers.lrn))
      }
    }

    "Must go from AddSafetyAndSecurityConsigneePage to" - {

      "CheckYourAnswersPage when No is selected" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsigneePage, false)
              .success
              .value

            navigator
              .nextPage(AddSafetyAndSecurityConsigneePage, CheckMode, updatedAnswers)
              .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "to CheckYourAnswers when user selects 'Yes' and answers already exist for consignee" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsigneePage, true)
              .success
              .value
              .set(AddSafetyAndSecurityConsigneeEoriPage, true)
              .success
              .value

            navigator
              .nextPage(AddSafetyAndSecurityConsigneePage, CheckMode, updatedAnswers)
              .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "to AddSafetyAndSecurityConsigneeEoriPage when user selects 'Yes' and answers do not exist for consignee and user selects 'E' for Circumstance Indicator" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsigneePage, true)
              .success
              .value
              .remove(AddSafetyAndSecurityConsigneeEoriPage)
              .success
              .value
              .set(CircumstanceIndicatorPage, "E")
              .success
              .value
              .set(OfficeOfDeparturePage, CustomsOffice("id", "name", CountryCode("GB"), None))
              .success
              .value

            navigator
              .nextPage(AddSafetyAndSecurityConsigneePage, CheckMode, updatedAnswers)
              .mustBe(routes.SafetyAndSecurityConsigneeEoriController.onPageLoad(answers.lrn, CheckMode))
        }
      }

      "to AddSafetyAndSecurityConsigneeEoriPage when user selects 'Yes' and answers do not exist for consignee and user does not select 'E' for Circumstance Indicator" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsigneePage, true)
              .success
              .value
              .remove(AddSafetyAndSecurityConsigneeEoriPage)
              .success
              .value
              .set(CircumstanceIndicatorPage, "B")
              .success
              .value
              .set(OfficeOfDeparturePage, CustomsOffice("id", "name", CountryCode("GB"), None))
              .success
              .value

            navigator
              .nextPage(AddSafetyAndSecurityConsigneePage, CheckMode, updatedAnswers)
              .mustBe(routes.AddSafetyAndSecurityConsigneeEoriController.onPageLoad(answers.lrn, CheckMode))
        }
      }

      "to AddSafetyAndSecurityConsigneeEoriPage when user selects 'Yes' and answers do not exist for consignee and selects 'No' to Add Circumstance Indicator" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsigneePage, true)
              .success
              .value
              .remove(AddSafetyAndSecurityConsigneeEoriPage)
              .success
              .value
              .set(AddCircumstanceIndicatorPage, false)
              .success
              .value
              .set(OfficeOfDeparturePage, CustomsOffice("id", "name", CountryCode("GB"), None))
              .success
              .value

            navigator
              .nextPage(AddSafetyAndSecurityConsigneePage, CheckMode, updatedAnswers)
              .mustBe(routes.AddSafetyAndSecurityConsigneeEoriController.onPageLoad(answers.lrn, CheckMode))
        }
      }
    }

    "AddSafetyAndSecurityConsigneeEoriPage must" - {

      "go to Check Your Answers page when Yes is selected and answer already exists for SafetyAndSecurityConsigneeEoriPage" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsigneeEoriPage, true)
              .success
              .value
              .set(SafetyAndSecurityConsigneeEoriPage, "GB000000")
              .success
              .value

            navigator
              .nextPage(AddSafetyAndSecurityConsigneeEoriPage, CheckMode, updatedAnswers)
              .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }
      "go to SafetyAndSecurityConsigneeEoriPage when Yes is selected and no answer already exists for SafetyAndSecurityConsigneeEoriPage" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsigneeEoriPage, true)
              .success
              .value
              .remove(SafetyAndSecurityConsigneeEoriPage)
              .success
              .value

            navigator
              .nextPage(AddSafetyAndSecurityConsigneeEoriPage, CheckMode, updatedAnswers)
              .mustBe(routes.SafetyAndSecurityConsigneeEoriController.onPageLoad(answers.lrn, CheckMode))
        }
      }
      "go to Check Your Answers page when No is selected and answer already exists for SafetyAndSecurityConsigneeNamePage" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsigneeEoriPage, false)
              .success
              .value
              .set(SafetyAndSecurityConsigneeNamePage, "TestName")
              .success
              .value

            navigator
              .nextPage(AddSafetyAndSecurityConsigneeEoriPage, CheckMode, updatedAnswers)
              .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }
      "go to SafetyAndSecurityConsigneeNamePage when No is selected and no answer already exists for SafetyAndSecurityConsigneeNamePage" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsigneeEoriPage, false)
              .success
              .value
              .remove(SafetyAndSecurityConsigneeNamePage)
              .success
              .value

            navigator
              .nextPage(AddSafetyAndSecurityConsigneeEoriPage, CheckMode, updatedAnswers)
              .mustBe(routes.SafetyAndSecurityConsigneeNameController.onPageLoad(answers.lrn, CheckMode))
        }
      }
    }

    "Must go from SafetyAndSecurityConsigneeEoriPage to Check Your Answers page" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(SafetyAndSecurityConsigneeEoriPage, CheckMode, answers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(answers.lrn))
      }
    }

    "Must go from SafetyAndSecurityConsigneeAddressPage to Check Your Answers page" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(SafetyAndSecurityConsigneeAddressPage, CheckMode, answers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(answers.lrn))
      }
    }

    "Must go from SafetyAndSecurityConsigneeNamePage to Check Your Answers page if answer exists for SafetyAndSecurityConsigneeAddressPage" in {

      val consigneeAddress = arbitrary[CommonAddress].sample.value
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(SafetyAndSecurityConsigneeAddressPage, consigneeAddress)
            .success
            .value
          navigator
            .nextPage(SafetyAndSecurityConsigneeNamePage, CheckMode, updatedAnswers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
      }
    }
    "Must go from SafetyAndSecurityConsigneeNamePage to SafetyAndSecurityConsigneeAddressPage if no answer exists for  SafetyAndSecurityConsigneeAddressPage" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .remove(SafetyAndSecurityConsigneeAddressPage)
            .success
            .value
          navigator
            .nextPage(SafetyAndSecurityConsigneeNamePage, CheckMode, updatedAnswers)
            .mustBe(routes.SafetyAndSecurityConsigneeAddressController.onPageLoad(updatedAnswers.lrn, CheckMode))
      }
    }

    "AddCarrierPage must go to" - {
      "CheckYourAnswersPage when user selects No" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddCarrierPage, false)
              .success
              .value
            navigator
              .nextPage(AddCarrierPage, CheckMode, updatedAnswers)
              .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "CheckYourAnswersPage when user selects Yes and an answer exists for CarriersEoriDetails" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddCarrierPage, true)
              .success
              .value
              .set(AddCarrierEoriPage, true)
              .success
              .value
            navigator
              .nextPage(AddCarrierPage, CheckMode, updatedAnswers)
              .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "AddCarrierEoriPage when user selects Yes and no answer exists for CarriersEoriDetails" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddCarrierPage, true)
              .success
              .value
              .remove(AddCarrierEoriPage)
              .success
              .value
            navigator
              .nextPage(AddCarrierPage, CheckMode, updatedAnswers)
              .mustBe(routes.AddCarrierEoriController.onPageLoad(answers.lrn, CheckMode))
        }
      }
    }

    "Must go from AddCarrierEoriPage" - {
      "to CheckYourAnswersPage if user selects Yes and an answer already exists for CarrierEoriNumberPage" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddCarrierEoriPage, true)
              .success
              .value
              .set(CarrierEoriPage, "GB123456")
              .success
              .value
            navigator
              .nextPage(AddCarrierEoriPage, CheckMode, updatedAnswers)
              .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "to CarrierEoriPage if user selects Yes and no answer already exists for CarrierEoriNumberPage" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddCarrierEoriPage, true)
              .success
              .value
              .remove(CarrierEoriPage)
              .success
              .value
            navigator
              .nextPage(AddCarrierEoriPage, CheckMode, updatedAnswers)
              .mustBe(routes.CarrierEoriController.onPageLoad(answers.lrn, CheckMode))
        }
      }

      "to CarrierNamePage if user selects No and no answer exists for CarrierNamePage" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddCarrierEoriPage, false)
              .success
              .value
              .remove(CarrierNamePage)
              .success
              .value
            navigator
              .nextPage(AddCarrierEoriPage, CheckMode, updatedAnswers)
              .mustBe(routes.CarrierNameController.onPageLoad(answers.lrn, CheckMode))
        }
      }

      "to CheckYourAnswersPage if user selects No and an answer already exists for CarrierNamePage" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddCarrierEoriPage, false)
              .success
              .value
              .set(CarrierNamePage, "test name")
              .success
              .value
            navigator
              .nextPage(AddCarrierEoriPage, CheckMode, updatedAnswers)
              .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }
    }

    "Must go from CarrierNamePage" - {
      "to CarrierAddressPage when no answer exists for CarrierAddressPage" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .remove(CarrierAddressPage)
              .success
              .value
            navigator
              .nextPage(CarrierNamePage, CheckMode, updatedAnswers)
              .mustBe(routes.CarrierAddressController.onPageLoad(answers.lrn, CheckMode))
        }
      }

      "to CheckYourAnswersPage when an answer exists for CarrierAddressPage" in {
        val carrierAddress = arbitrary[CommonAddress].sample.value

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(CarrierAddressPage, carrierAddress)
              .success
              .value
            navigator
              .nextPage(CarrierNamePage, CheckMode, updatedAnswers)
              .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }
    }

    "Must go from CarrierEoriPage to CheckYourAnswersPage" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(CarrierEoriPage, CheckMode, answers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(answers.lrn))
      }
    }

    "Must go from CarrierAddressPage to CheckYourAnswersPage" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(CarrierAddressPage, CheckMode, answers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(answers.lrn))
      }
    }
  }
}
