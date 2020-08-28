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
import controllers.routes
import generators.Generators
import pages._
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class NavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new Navigator

  "Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator.nextPage(UnknownPage, NormalMode, answers)
              .mustBe(routes.IndexController.onPageLoad())
        }
      }

      "must go from Local Reference Number page to Add Security Details page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator.nextPage(LocalReferenceNumberPage, NormalMode, answers)
              .mustBe(routes.AddSecurityDetailsController.onPageLoad(answers.id, NormalMode))
        }
      }

      "Movement Details Section" - {

        "must go from Declaration Type page to Procedure Type page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>

              navigator.nextPage(DeclarationTypePage, NormalMode, answers)
                .mustBe(routes.ProcedureTypeController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Procedure Type page to Container Used page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>

              navigator.nextPage(ProcedureTypePage, NormalMode, answers)
                .mustBe(routes.ContainersUsedPageController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from  Container Used page to Declaration Place page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>

              navigator.nextPage(ContainersUsedPage, NormalMode, answers)
                .mustBe(routes.DeclarationPlaceController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Declaration Place page to Declaration For Someone Else page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>

              navigator.nextPage(DeclarationPlacePage, NormalMode, answers)
                .mustBe(routes.DeclarationForSomeoneElseController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Declaration For Someone Else page to Representative Name page on selecting option 'Yes'" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedUserAnswers = answers.set(DeclarationForSomeoneElsePage, true).toOption.value

              navigator.nextPage(DeclarationForSomeoneElsePage, NormalMode, updatedUserAnswers)
                .mustBe(routes.RepresentativeNameController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Declaration For Someone Else page to movement details check your answers page on selecting option 'No'" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedUserAnswers = answers.set(DeclarationForSomeoneElsePage, false).toOption.value

              navigator.nextPage(DeclarationForSomeoneElsePage, NormalMode, updatedUserAnswers)
                .mustBe(routes.MovementDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
        }

        "must go from Representative Name page to Representative Capacity page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>

              navigator.nextPage(RepresentativeNamePage, NormalMode, answers)
                .mustBe(routes.RepresentativeCapacityController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Representative Capacity page to Check Your Answers page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>

              navigator.nextPage(RepresentativeCapacityPage, NormalMode, answers)
                .mustBe(routes.MovementDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
        }
      }

      "Trader Details section" - {

        "must go from Is principal eori known page to what is eori number page when 'YES' is selected" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(IsPrincipalEoriKnownPage, true).success.value
              navigator.nextPage(IsPrincipalEoriKnownPage, NormalMode, updatedAnswers)
                .mustBe(routes.WhatIsPrincipalEoriController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Is principal eori known page to principal name page when 'NO' is selected" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(IsPrincipalEoriKnownPage, false).success.value
              navigator.nextPage(IsPrincipalEoriKnownPage, NormalMode, updatedAnswers)
                .mustBe(routes.PrincipalNameController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Principal eori page to add consignor page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator.nextPage(WhatIsPrincipalEoriPage, NormalMode, answers)
                .mustBe(routes.AddConsignorController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Principal name page to Principal address page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator.nextPage(PrincipalNamePage, NormalMode, answers)
                .mustBe(routes.PrincipalAddressController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Principal address page to Add consignor page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator.nextPage(PrincipalAddressPage, NormalMode, answers)
                .mustBe(routes.AddConsignorController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Add consignor page to Is consignor eori known page when 'YES' is selected" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(AddConsignorPage, true).success.value
              navigator.nextPage(AddConsignorPage, NormalMode, updatedAnswers)
                .mustBe(routes.IsConsignorEoriKnownController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Add consignor page to Add consignee page when 'NO' is selected" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(AddConsignorPage, false).success.value
              navigator.nextPage(AddConsignorPage, NormalMode, updatedAnswers)
                .mustBe(routes.AddConsigneeController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Is consignor eori known page to Consignor eori page when 'YES' is selected" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(IsConsignorEoriKnownPage, true).success.value
              navigator.nextPage(IsConsignorEoriKnownPage, NormalMode, updatedAnswers)
                .mustBe(routes.ConsignorEoriController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Is consignor eori known page to Consignor name page when 'NO' is selected" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(IsConsignorEoriKnownPage, false).success.value
              navigator.nextPage(IsConsignorEoriKnownPage, NormalMode, updatedAnswers)
                .mustBe(routes.ConsignorNameController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Consignor name page to Consignor address page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator.nextPage(ConsignorNamePage, NormalMode, answers)
                .mustBe(routes.ConsignorAddressController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Consignor address page to Add consignee page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator.nextPage(ConsignorAddressPage, NormalMode, answers)
                .mustBe(routes.AddConsigneeController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Add consignee page to Is consignee eori known page when 'YES' is selected" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(AddConsigneePage, true).success.value
              navigator.nextPage(AddConsigneePage, NormalMode, updatedAnswers)
                .mustBe(routes.IsConsigneeEoriKnownController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Add consignee page to Trader details cya page when 'NO' is selected" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(AddConsigneePage, false).success.value
              navigator.nextPage(AddConsigneePage, NormalMode, updatedAnswers)
                .mustBe(routes.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
        }

        "must go from Is consignee eori known page to Consignee eori page when 'YES' is selected" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(IsConsigneeEoriKnownPage, true).success.value
              navigator.nextPage(IsConsigneeEoriKnownPage, NormalMode, updatedAnswers)
                .mustBe(routes.WhatIsConsigneeEoriController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Is consignee eori known page to Consignee name page when 'NO' is selected" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(IsConsigneeEoriKnownPage, false).success.value
              navigator.nextPage(IsConsigneeEoriKnownPage, NormalMode, updatedAnswers)
                .mustBe(routes.ConsigneeNameController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Consignee name page to Consignee address page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator.nextPage(ConsigneeNamePage, NormalMode, answers)
                .mustBe(routes.ConsigneeAddressController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Consignee address page to Trader details cya page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator.nextPage(ConsigneeAddressPage, NormalMode, answers)
                .mustBe(routes.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
        }

      }


    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map  to Check Your Answers" in {

        case object UnknownPage extends Page

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator.nextPage(UnknownPage, CheckMode, answers)
              .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.id))
        }
      }
      "Movement Details Section" - {

        "Must go from Declaration-Type to Movement Details Check Your Answers" - {

          forAll(arbitrary[UserAnswers]) {
            answers =>

              navigator.nextPage(DeclarationTypePage, CheckMode, answers)
                .mustBe(routes.MovementDetailsCheckYourAnswersController.onPageLoad(answers.id))

          }

          "must go from Declaration For Someone Else page to Representative Name page on selecting option 'Yes'" in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedUserAnswers = answers.set(DeclarationForSomeoneElsePage, true).toOption.value
                  .remove(RepresentativeNamePage).toOption.value

                navigator.nextPage(DeclarationForSomeoneElsePage, CheckMode, updatedUserAnswers)
                  .mustBe(routes.RepresentativeNameController.onPageLoad(answers.id, NormalMode))
            }
          }

          "must go from Declaration For Someone Else page to CYA page on selecting option 'Yes' and representativeNamePage has data" in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedUserAnswers = answers.set(DeclarationForSomeoneElsePage, true).toOption.value
                  .set(RepresentativeNamePage, "answer").toOption.value

                navigator.nextPage(DeclarationForSomeoneElsePage, CheckMode, updatedUserAnswers)
                  .mustBe(routes.MovementDetailsCheckYourAnswersController.onPageLoad(answers.id))
            }
          }

          "must go from Declaration For Someone Else page to movement details check your answers page on selecting option 'No'" in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedUserAnswers = answers.set(DeclarationForSomeoneElsePage, false).toOption.value

                navigator.nextPage(DeclarationForSomeoneElsePage, CheckMode, updatedUserAnswers)
                  .mustBe(routes.MovementDetailsCheckYourAnswersController.onPageLoad(answers.id))
            }
          }

        }
      }

      "Trader Details Section" - {

        "Must go from Is principal eori known page to What is principal eori page if the option selected is 'YES'" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.set(IsPrincipalEoriKnownPage, true).toOption.value
                .remove(WhatIsPrincipalEoriPage).toOption.value

              navigator.nextPage(IsPrincipalEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(routes.WhatIsPrincipalEoriController.onPageLoad(answers.id, NormalMode))
          }
        }

      }
    }
  }
}