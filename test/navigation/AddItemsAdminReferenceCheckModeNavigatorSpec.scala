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
import commonTestUtils.UserAnswersSpecHelper
import controllers.addItems.previousReferences.{routes => previousReferenceRoutes}
import controllers.addItems.routes
import generators.Generators
import models.DeclarationType.t2Options
import models.{CheckMode, DeclarationType, UserAnswers}
import navigation.annotations.addItemsNavigators.AddItemsAdminReferenceNavigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.addItems._
import pages.{AddSecurityDetailsPage, DeclarationTypePage}
import queries.PreviousReferencesQuery

class AddItemsAdminReferenceCheckModeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersSpecHelper {
  // format: off
  val navigator = new AddItemsAdminReferenceNavigator

  "Add Items section" - {
    
  "previous references journey" - {
    "must go from add administrative reference page to CYA page when selected 'No' and also selected 'No' for add safety and security" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddAdministrativeReferencePage(itemIndex), false).success.value
            .set(AddSecurityDetailsPage, false).success.value
          navigator
            .nextPage(AddAdministrativeReferencePage(itemIndex), CheckMode, updatedAnswers)
            .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, itemIndex))
      }
    }

    "must go from 'reference-type page' to 'previous reference' page" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(ReferenceTypePage(itemIndex, referenceIndex), CheckMode, answers)
            .mustBe(previousReferenceRoutes.PreviousReferenceController.onPageLoad(answers.lrn, itemIndex, referenceIndex, CheckMode))
      }
    }

    "must go from 'previous reference' page to 'add extra information' page" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(PreviousReferencePage(itemIndex, referenceIndex), CheckMode, answers)
            .mustBe(previousReferenceRoutes.AddExtraInformationController.onPageLoad(answers.lrn, itemIndex, referenceIndex, CheckMode))
      }
    }

    "must go from 'add extra information' page to 'extra information' page on selecting 'Yes'" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswer = answers.set(AddExtraInformationPage(itemIndex, referenceIndex), true).success.value

          navigator
            .nextPage(AddExtraInformationPage(itemIndex, referenceIndex), CheckMode, updatedAnswer)
            .mustBe(previousReferenceRoutes.ExtraInformationController.onPageLoad(answers.lrn, itemIndex, referenceIndex, CheckMode))
      }
    }

    "must go from 'add extra information' page to 'CYA' page on selecting 'No'" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswer = answers.set(AddExtraInformationPage(itemIndex, referenceIndex), false).success.value

          navigator
            .nextPage(AddExtraInformationPage(itemIndex, referenceIndex), CheckMode, updatedAnswer)
            .mustBe(previousReferenceRoutes.ReferenceCheckYourAnswersController.onPageLoad(answers.lrn, itemIndex, referenceIndex, CheckMode))
      }
    }

    "must go from 'extra information' page to 'CYA' page" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswer = answers.set(ExtraInformationPage(itemIndex, referenceIndex), "text").success.value

          navigator
            .nextPage(ExtraInformationPage(itemIndex, referenceIndex), CheckMode, updatedAnswer)
            .mustBe(previousReferenceRoutes.ReferenceCheckYourAnswersController.onPageLoad(answers.lrn, itemIndex, referenceIndex, CheckMode))
      }
    }

    "must go to Reference Type page when user selects 'Yes'" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswer = answers
            .remove(PreviousReferencesQuery(itemIndex)).success.value
            .set(AddAnotherPreviousAdministrativeReferencePage(itemIndex), true).success.value

          navigator
            .nextPage(AddAnotherPreviousAdministrativeReferencePage(itemIndex), CheckMode, updatedAnswer)
            .mustBe(previousReferenceRoutes.ReferenceTypeController.onPageLoad(answers.lrn, itemIndex, referenceIndex, CheckMode))
      }
    }

    "must go to CYA page when user selects 'No'" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswer = answers
            .remove(PreviousReferencesQuery(itemIndex)).success.value
            .set(AddAnotherPreviousAdministrativeReferencePage(itemIndex), false).success.value

          navigator
            .nextPage(AddAnotherPreviousAdministrativeReferencePage(itemIndex), CheckMode, updatedAnswer)
            .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.lrn, itemIndex))
      }
    }

    "must go from Remove previous administrative reference" - {
      
        "when previous administrative references are mandatory" - {

          "and there are existing administrative references" - {

            "to add another previous administrative reference page" in {

              forAll(Gen.oneOf(t2Options)) {
                t2Option =>

                  val updatedAnswer = emptyUserAnswers
                    .set(ReferenceTypePage(itemIndex, referenceIndex), "Foo").success.value
                    .set(DeclarationTypePage, t2Option).success.value
                    .set(IsNonEuOfficePage, true).success.value


                  navigator
                    .nextPage(ConfirmRemovePreviousAdministrativeReferencePage(itemIndex, referenceIndex), CheckMode, updatedAnswer)
                    .mustBe(previousReferenceRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(updatedAnswer.lrn, itemIndex, CheckMode))
              }
            }
          }

          "and there are no existing administrative references" - {

            "to reference type page" in {

              forAll(Gen.oneOf(t2Options)) {
                t2Option =>

                  val updatedAnswer = emptyUserAnswers
                    .set(DeclarationTypePage, t2Option).success.value
                    .set(IsNonEuOfficePage, true).success.value


                  navigator
                    .nextPage(ConfirmRemovePreviousAdministrativeReferencePage(itemIndex, referenceIndex), CheckMode, updatedAnswer)
                    .mustBe(previousReferenceRoutes.ReferenceTypeController.onPageLoad(updatedAnswer.lrn, itemIndex, referenceIndex, CheckMode))
              }
            }
          }
        }

        "when previous administrative references are optional" - {

          "and there are existing administrative references" - {

            "to add another previous administrative reference page" in {

              forAll(arbitrary[DeclarationType]) {
                declarationType =>

                  val updatedAnswer = emptyUserAnswers
                    .set(ReferenceTypePage(itemIndex, referenceIndex), "Foo").success.value
                    .set(DeclarationTypePage, declarationType).success.value
                    .set(IsNonEuOfficePage, false).success.value


                  navigator
                    .nextPage(ConfirmRemovePreviousAdministrativeReferencePage(itemIndex, referenceIndex), CheckMode, updatedAnswer)
                    .mustBe(previousReferenceRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(updatedAnswer.lrn, itemIndex, CheckMode))
              }
            }
          }

          "and there are no existing administrative references" - {

            "to add administrative references page" in {

              forAll(arbitrary[DeclarationType]) {
                declarationType =>
                
                  val updatedAnswer = emptyUserAnswers
                    .set(DeclarationTypePage, declarationType).success.value
                    .set(IsNonEuOfficePage, false).success.value


                  navigator
                    .nextPage(ConfirmRemovePreviousAdministrativeReferencePage(itemIndex, referenceIndex), CheckMode, updatedAnswer)
                    .mustBe(previousReferenceRoutes.AddAdministrativeReferenceController.onPageLoad(updatedAnswer.lrn, itemIndex, CheckMode))
              }
            }
          }
        }
      }
  }
 }
  // format: on
}
