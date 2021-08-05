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
import controllers.addItems.previousReferences.{routes => previousReferenceRoutes}
import controllers.addItems.routes
import generators.Generators
import models.{CheckMode, UserAnswers}
import navigation.annotations.addItemsNavigators.AddItemsAdminReferenceNavigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.AddSecurityDetailsPage
import pages.addItems._
import queries.PreviousReferencesQuery

class AddItemsAdminReferenceCheckModeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersSpecHelper {
  // format: off
  val navigator = new AddItemsAdminReferenceNavigator

  "Add Items section" - {

  }
    "previous references journey" - {
      "must go from add administrative reference page to CYA page when selected 'No' and also selected 'No' for add safety and security" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddAdministrativeReferencePage(index), false).success.value
              .set(AddSecurityDetailsPage, false).success.value
            navigator
              .nextPage(AddAdministrativeReferencePage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
        }
      }

      "must go from 'reference-type page' to 'previous reference' page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ReferenceTypePage(index, referenceIndex), CheckMode, answers)
              .mustBe(previousReferenceRoutes.PreviousReferenceController.onPageLoad(answers.lrn, index, referenceIndex, CheckMode))
        }
      }

      "must go from 'previous reference' page to 'add extra information' page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(PreviousReferencePage(index, referenceIndex), CheckMode, answers)
              .mustBe(previousReferenceRoutes.AddExtraInformationController.onPageLoad(answers.lrn, index, referenceIndex, CheckMode))
        }
      }

      "must go from 'add extra information' page to 'extra information' page on selecting 'Yes'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswer = answers.set(AddExtraInformationPage(index, referenceIndex), true).success.value

            navigator
              .nextPage(AddExtraInformationPage(index, referenceIndex), CheckMode, updatedAnswer)
              .mustBe(previousReferenceRoutes.ExtraInformationController.onPageLoad(answers.lrn, index, referenceIndex, CheckMode))
        }
      }

      "must go from 'add extra information' page to 'Add another reference' page on selecting 'No'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswer = answers.set(AddExtraInformationPage(index, referenceIndex), false).success.value

            navigator
              .nextPage(AddExtraInformationPage(index, referenceIndex), CheckMode, updatedAnswer)
              .mustBe(previousReferenceRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(answers.lrn, index, CheckMode))
        }
      }

      "must go from 'extra information' page to 'Add another reference' page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswer = answers.set(ExtraInformationPage(index, referenceIndex), "text").success.value

            navigator
              .nextPage(ExtraInformationPage(index, referenceIndex), CheckMode, updatedAnswer)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.lrn, index))
        }
      }

      "must go to Reference Type page when user selects 'Yes'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswer = answers
              .remove(PreviousReferencesQuery(index)).success.value
              .set(AddAnotherPreviousAdministrativeReferencePage(index), true).success.value

            navigator
              .nextPage(AddAnotherPreviousAdministrativeReferencePage(index), CheckMode, updatedAnswer)
              .mustBe(previousReferenceRoutes.ReferenceTypeController.onPageLoad(answers.lrn, index, index, CheckMode))
        }
      }

      "must go to CYA page when user selects 'No'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswer = answers
              .remove(PreviousReferencesQuery(index)).success.value
              .set(AddAnotherPreviousAdministrativeReferencePage(index), false).success.value

            navigator
              .nextPage(AddAnotherPreviousAdministrativeReferencePage(index), CheckMode, updatedAnswer)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.lrn, index))
        }
      }


    }
  // format: on
}
