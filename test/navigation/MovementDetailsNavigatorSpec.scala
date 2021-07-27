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
import controllers.movementDetails.{routes => movementDetailsRoute}
import generators.Generators
import models.DeclarationType.Option1
import models._
import models.ProcedureType._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.generalInformation.{
  ContainersUsedPage,
  DeclarationForSomeoneElsePage,
  DeclarationPlacePage,
  PreLodgeDeclarationPage,
  RepresentativeCapacityPage,
  RepresentativeNamePage
}

class MovementDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new MovementDetailsNavigator
  // format: off
  "Movement Details Section" - {
    "Normal mode" - {

      "must go from PreLodge Declaration page to Containers Used page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(PreLodgeDeclarationPage, NormalMode, answers)
              .mustBe(movementDetailsRoute.ContainersUsedPageController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from Containers Used page to Declaration Place page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ContainersUsedPage, NormalMode, answers)
              .mustBe(movementDetailsRoute.DeclarationPlaceController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from Declaration Place page to Declaration For Someone Else page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(DeclarationPlacePage, NormalMode, answers)
              .mustBe(movementDetailsRoute.DeclarationForSomeoneElseController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from Declaration For Someone Else page to Representative Name page on selecting option 'Yes'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedUserAnswers = answers.set(DeclarationForSomeoneElsePage, true).toOption.value

            navigator
              .nextPage(DeclarationForSomeoneElsePage, NormalMode, updatedUserAnswers)
              .mustBe(movementDetailsRoute.RepresentativeNameController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from Declaration For Someone Else page to movement details check your answers page on selecting option 'No'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedUserAnswers = answers.set(DeclarationForSomeoneElsePage, false).toOption.value

            navigator
              .nextPage(DeclarationForSomeoneElsePage, NormalMode, updatedUserAnswers)
              .mustBe(movementDetailsRoute.MovementDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from Representative Name page to Representative Capacity page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(RepresentativeNamePage, NormalMode, answers)
              .mustBe(movementDetailsRoute.RepresentativeCapacityController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from Representative Capacity page to Check Your Answers page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(RepresentativeCapacityPage, NormalMode, answers)
              .mustBe(movementDetailsRoute.MovementDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }
    }

    "Check mode" - {

      "must go from PreLodge Declaration page to CYA page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(PreLodgeDeclarationPage, CheckMode, answers)
              .mustBe(movementDetailsRoute.MovementDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from Declaration For Someone Else page to CYA page on selecting option 'Yes' and representativeNamePage has data" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedUserAnswers = answers
              .set(DeclarationForSomeoneElsePage, true).toOption.value
              .set(RepresentativeNamePage, "answer").toOption.value

            navigator
              .nextPage(DeclarationForSomeoneElsePage, CheckMode, updatedUserAnswers)
              .mustBe(movementDetailsRoute.MovementDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from Declaration For Someone Else page to Representative Name page on selecting option 'Yes'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedUserAnswers = answers
              .set(DeclarationForSomeoneElsePage, true).toOption.value
              .remove(RepresentativeNamePage).toOption.value

            navigator
              .nextPage(DeclarationForSomeoneElsePage, CheckMode, updatedUserAnswers)
              .mustBe(movementDetailsRoute.RepresentativeNameController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from Declaration For Someone Else page to movement details check your answers page on selecting option 'No'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedUserAnswers = answers.set(DeclarationForSomeoneElsePage, false).toOption.value

            navigator
              .nextPage(DeclarationForSomeoneElsePage, CheckMode, updatedUserAnswers)
              .mustBe(movementDetailsRoute.MovementDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }
    }
  }
  // format: on
}
