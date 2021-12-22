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
import models.{Index, NormalMode, UserAnswers}
import navigation.annotations.addItemsNavigators.AddItemsAdminReferenceNavigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.AddSecurityDetailsPage
import pages.addItems._
import pages.safetyAndSecurity.{AddCommercialReferenceNumberAllItemsPage, AddTransportChargesPaymentMethodPage}
import queries.PreviousReferencesQuery

class AddItemsAdminReferenceNormalModeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersSpecHelper {
  // format: off
  val navigator = new AddItemsAdminReferenceNavigator


  "Add Items section" - {

    "must go from 'reference-type page' to 'previous reference' page" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(ReferenceTypePage(itemIndex, referenceIndex), NormalMode, answers)
            .mustBe(previousReferenceRoutes.PreviousReferenceController.onPageLoad(answers.lrn, itemIndex, referenceIndex, NormalMode))
      }
    }

    "must go from 'previous reference' page to 'add extra information' page" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(PreviousReferencePage(itemIndex, referenceIndex), NormalMode, answers)
            .mustBe(previousReferenceRoutes.AddExtraInformationController.onPageLoad(answers.lrn, itemIndex, referenceIndex, NormalMode))
      }
    }

    "must go from 'add extra information' page to 'extra information' page on selecting 'Yes'" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswer = answers.set(AddExtraInformationPage(itemIndex, referenceIndex), true).success.value

          navigator
            .nextPage(AddExtraInformationPage(itemIndex, referenceIndex), NormalMode, updatedAnswer)
            .mustBe(previousReferenceRoutes.ExtraInformationController.onPageLoad(answers.lrn, itemIndex, referenceIndex, NormalMode))
      }
    }

    "must go from 'add extra information' page to 'CYA' page on selecting 'No'" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswer = answers.set(AddExtraInformationPage(itemIndex, referenceIndex), false).success.value

          navigator
            .nextPage(AddExtraInformationPage(itemIndex, referenceIndex), NormalMode, updatedAnswer)
            .mustBe(previousReferenceRoutes.ReferenceCheckYourAnswersController.onPageLoad(answers.lrn, itemIndex, referenceIndex, NormalMode))
      }
    }

    "must go from 'extra information' page to 'CYA' page" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswer = answers.set(ExtraInformationPage(itemIndex, referenceIndex), "text").success.value

          navigator
            .nextPage(ExtraInformationPage(itemIndex, referenceIndex), NormalMode, updatedAnswer)
            .mustBe(previousReferenceRoutes.ReferenceCheckYourAnswersController.onPageLoad(answers.lrn, itemIndex, referenceIndex, NormalMode))
      }
    }

    "AddAnotherPreviousAdministrativeReferencePage" - {
      "must go to ReferenceType page when user selects 'Yes'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswer = answers
              .remove(PreviousReferencesQuery(itemIndex)).success.value
              .set(AddAnotherPreviousAdministrativeReferencePage(itemIndex), true).success.value

            navigator
              .nextPage(AddAnotherPreviousAdministrativeReferencePage(itemIndex), NormalMode, updatedAnswer)
              .mustBe(previousReferenceRoutes.ReferenceTypeController.onPageLoad(answers.lrn, itemIndex, referenceIndex, NormalMode))
        }
      }

      "must go to CYA page when user selects 'No' and Add Security page is 'No' " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswer = answers
              .remove(PreviousReferencesQuery(itemIndex)).success.value
              .set(AddAnotherPreviousAdministrativeReferencePage(itemIndex), false).success.value
              .set(AddSecurityDetailsPage, false).success.value

            navigator
              .nextPage(AddAnotherPreviousAdministrativeReferencePage(itemIndex), NormalMode, updatedAnswer)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswer.lrn, itemIndex))
        }
      }

    }


    "ConfirmRemovePreviousAdministrativeReference page" - {
      "must go to AddAnotherPreviousAdministrativeReference page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswer = answers
              .set(ReferenceTypePage(itemIndex, referenceIndex), "T1").success.value
              .set(ReferenceTypePage(itemIndex, Index(1)), "T1").success.value
              .set(ConfirmRemovePreviousAdministrativeReferencePage(itemIndex, referenceIndex), true).success.value
            navigator
              .nextPage(ConfirmRemovePreviousAdministrativeReferencePage(itemIndex, referenceIndex), NormalMode, updatedAnswer)
              .mustBe(previousReferenceRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(answers.lrn, itemIndex, NormalMode))
        }
      }

      "must go to add administrative reference page when there are no previous references" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswer = answers
              .remove(PreviousReferencesQuery(itemIndex)).success.value
              .set(ConfirmRemovePreviousAdministrativeReferencePage(itemIndex, referenceIndex), true).success.value

            navigator
              .nextPage(ConfirmRemovePreviousAdministrativeReferencePage(itemIndex, referenceIndex), NormalMode, updatedAnswer)
              .mustBe(previousReferenceRoutes.AddAdministrativeReferenceController.onPageLoad(answers.lrn, itemIndex, NormalMode))
        }
      }


      "must go from add administrative reference page to reference type page when selected 'Yes'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .remove(PreviousReferencesQuery(itemIndex)).success.value
              .set(AddAdministrativeReferencePage(itemIndex), true).success.value
            navigator
              .nextPage(AddAdministrativeReferencePage(itemIndex), NormalMode, updatedAnswers)
              .mustBe(previousReferenceRoutes.ReferenceTypeController.onPageLoad(updatedAnswers.lrn, itemIndex, referenceIndex, NormalMode))
        }
      }

      "must go from add administrative reference page to transport charges page when selected 'No' and not using same method of payment across all items" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSecurityDetailsPage, true).success.value
              .set(AddTransportChargesPaymentMethodPage, false).success.value
              .set(AddAdministrativeReferencePage(itemIndex), false).success.value

            navigator
              .nextPage(AddAdministrativeReferencePage(itemIndex), NormalMode, updatedAnswers)
              .mustBe(controllers.addItems.securityDetails.routes.TransportChargesController.onPageLoad(updatedAnswers.lrn, itemIndex, NormalMode))
        }
      }

      "must go from add administrative reference page to CYA page when selected 'No' and also selected 'No' for add safety and security" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddAdministrativeReferencePage(itemIndex), false).success.value
              .set(AddSecurityDetailsPage, false).success.value
            navigator
              .nextPage(AddAdministrativeReferencePage(itemIndex), NormalMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, itemIndex))
        }
      }

      "must go from add administrative reference page to Commercial Reference page when selected 'No' and also selected 'Yes' for add safety and security" +
        " and selected 'Yes' for add transport charges method of payment and 'No' for add commercial reference number across all items" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSecurityDetailsPage, true).success.value
              .set(AddTransportChargesPaymentMethodPage, true).success.value
              .set(AddCommercialReferenceNumberAllItemsPage, false).success.value
              .set(AddAdministrativeReferencePage(itemIndex), false).success.value

            navigator
              .nextPage(AddAdministrativeReferencePage(itemIndex), NormalMode, updatedAnswers)
              .mustBe(controllers.addItems.securityDetails.routes.CommercialReferenceNumberController.onPageLoad(updatedAnswers.lrn, itemIndex, NormalMode))
        }
      }

      "must go from add administrative reference page to Add Dangerous Goods page when selected 'No' and also selected 'Yes' for add safety and security" +
        " and selected 'Yes' for add transport charges method of payment and 'No' for add commercial reference number across all items" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSecurityDetailsPage, true).success.value
              .set(AddTransportChargesPaymentMethodPage, true).success.value
              .set(AddCommercialReferenceNumberAllItemsPage, true).success.value
              .set(AddAdministrativeReferencePage(itemIndex), false).success.value

            navigator
              .nextPage(AddAdministrativeReferencePage(itemIndex), NormalMode, updatedAnswers)
              .mustBe(controllers.addItems.securityDetails.routes.AddDangerousGoodsCodeController.onPageLoad(updatedAnswers.lrn, itemIndex, NormalMode))
        }
      }


      "must go from AddAnotherPreviousAdministrativeReferencePage to transport charges page when selected 'No' and not using same method of payment across all items" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSecurityDetailsPage, true).success.value
              .set(AddTransportChargesPaymentMethodPage, false).success.value
              .set(AddAnotherPreviousAdministrativeReferencePage(itemIndex), false).success.value

            navigator
              .nextPage(AddAnotherPreviousAdministrativeReferencePage(itemIndex), NormalMode, updatedAnswers)
              .mustBe(controllers.addItems.securityDetails.routes.TransportChargesController.onPageLoad(updatedAnswers.lrn, itemIndex, NormalMode))
        }
      }

      "must go from AddAnotherPreviousAdministrativeReferencePage to CYA page when selected 'No' and also selected 'No' for add safety and security" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddAnotherPreviousAdministrativeReferencePage(itemIndex), false).success.value
              .set(AddSecurityDetailsPage, false).success.value
            navigator
              .nextPage(AddAnotherPreviousAdministrativeReferencePage(itemIndex), NormalMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, itemIndex))
        }
      }

      "must go from AddAnotherPreviousAdministrativeReferencePage to Commercial Reference page when selected 'No' and also selected 'Yes' for add safety and security" +
        " and selected 'Yes' for add transport charges method of payment and 'No' for add commercial reference number across all items" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSecurityDetailsPage, true).success.value
              .set(AddTransportChargesPaymentMethodPage, true).success.value
              .set(AddCommercialReferenceNumberAllItemsPage, false).success.value
              .set(AddAnotherPreviousAdministrativeReferencePage(itemIndex), false).success.value

            navigator
              .nextPage(AddAnotherPreviousAdministrativeReferencePage(itemIndex), NormalMode, updatedAnswers)
              .mustBe(controllers.addItems.securityDetails.routes.CommercialReferenceNumberController.onPageLoad(updatedAnswers.lrn, itemIndex, NormalMode))
        }
      }

      "must go from AddAnotherPreviousAdministrativeReferencePage to Add Dangerous Goods page when selected 'No' and also selected 'Yes' for add safety and security" +
        " and selected 'Yes' for add transport charges method of payment and 'No' for add commercial reference number across all items" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSecurityDetailsPage, true).success.value
              .set(AddTransportChargesPaymentMethodPage, true).success.value
              .set(AddCommercialReferenceNumberAllItemsPage, true).success.value
              .set(AddAnotherPreviousAdministrativeReferencePage(itemIndex), false).success.value

            navigator
              .nextPage(AddAnotherPreviousAdministrativeReferencePage(itemIndex), NormalMode, updatedAnswers)
              .mustBe(controllers.addItems.securityDetails.routes.AddDangerousGoodsCodeController.onPageLoad(updatedAnswers.lrn, itemIndex, NormalMode))
        }
      }
    }
  }




  // format: on
}
