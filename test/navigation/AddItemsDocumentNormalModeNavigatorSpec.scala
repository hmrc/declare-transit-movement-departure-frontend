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
import controllers.addItems.previousReferences.{routes => previousReferencesRoutes}
import generators.Generators
import models.{DeclarationType, NormalMode}
import navigation.annotations.addItemsNavigators.AddItemsDocumentNavigator
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.DeclarationTypePage
import pages.addItems._

class AddItemsDocumentNormalModeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {
  // format: off
  val navigator = new AddItemsDocumentNavigator

  "Document navigator" - {
    "in Normal Mode" - {
      "AddDocumentsPage must go to" - {

        "Add Administrative Reference page when declaration type is T1 " in {

          val updatedAnswers = emptyUserAnswers
            .set(AddDocumentsPage(index), false).success.value
            .set(DeclarationTypePage, DeclarationType.Option1).success.value

          navigator
            .nextPage(AddDocumentsPage(index), NormalMode, updatedAnswers)
            .mustBe(previousReferencesRoutes.AddAdministrativeReferenceController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
        }


        "Reference Type page when user selects 'No', and declaration type is T2 and office of departure country is non-EU" in {

          val updatedAnswers = emptyUserAnswers
            .set(AddDocumentsPage(index), false).success.value
            .set(DeclarationTypePage, DeclarationType.Option2).success.value
            .set(IsNonEuOfficePage, true).success.value
          navigator
            .nextPage(AddDocumentsPage(index), NormalMode, updatedAnswers)
            .mustBe(previousReferencesRoutes.ReferenceTypeController.onPageLoad(updatedAnswers.lrn, index, referenceIndex, NormalMode))
        }

        "Reference Type page when user selects 'No', and declaration type is T2F and office of departure country is non-EU" in {

          val updatedAnswers = emptyUserAnswers
            .set(AddDocumentsPage(index), false).success.value
            .set(DeclarationTypePage, DeclarationType.Option3).success.value
            .set(IsNonEuOfficePage, true).success.value
          navigator
            .nextPage(AddDocumentsPage(index), NormalMode, updatedAnswers)
            .mustBe(previousReferencesRoutes.ReferenceTypeController.onPageLoad(updatedAnswers.lrn, index, referenceIndex, NormalMode))
        }


        "DocumentTypePage when user selects 'Yes'" in {
          val updatedAnswers = emptyUserAnswers
            .set(AddDocumentsPage(index), true).success.value
          navigator
            .nextPage(AddDocumentsPage(index), NormalMode, updatedAnswers)
            .mustBe(controllers.addItems.documents.routes.DocumentTypeController.onPageLoad(updatedAnswers.lrn, index, index, NormalMode))
        }
      }

      "DocumentTypePage must go to DocumentReferencePage" in {
        val updatedAnswers = emptyUserAnswers
          .set(DocumentTypePage(index, documentIndex), "test").success.value
        navigator
          .nextPage(DocumentTypePage(index, documentIndex), NormalMode, updatedAnswers)
          .mustBe(controllers.addItems.documents.routes.DocumentReferenceController.onPageLoad(updatedAnswers.lrn, index, documentIndex, NormalMode))
      }

      "DocumentReferencePage must go to AddExtraDocumentInformation page" in {
        val updatedAnswers = emptyUserAnswers
          .set(DocumentReferencePage(index, documentIndex), "test").success.value
        navigator
          .nextPage(DocumentReferencePage(index, documentIndex), NormalMode, updatedAnswers)
          .mustBe(controllers.addItems.documents.routes.AddExtraDocumentInformationController.onPageLoad(updatedAnswers.lrn, index, documentIndex, NormalMode))
      }

      "AddExtraDocumentInformation page must go to" - {

        "DocumentExtraInformationPage when user selects 'Yes' " in {
          val updatedAnswers = emptyUserAnswers
            .set(AddExtraDocumentInformationPage(index, documentIndex), true).success.value
          navigator
            .nextPage(AddExtraDocumentInformationPage(index, documentIndex), NormalMode, updatedAnswers)
            .mustBe(controllers.addItems.documents.routes.DocumentExtraInformationController.onPageLoad(updatedAnswers.lrn, index, documentIndex, NormalMode))
        }

        "CYA page when user selects 'No' " in {
          val updatedAnswers = emptyUserAnswers
            .set(AddExtraDocumentInformationPage(index, documentIndex), false).success.value
          navigator
            .nextPage(AddExtraDocumentInformationPage(index, documentIndex), NormalMode, updatedAnswers)
            .mustBe(controllers.addItems.documents.routes.DocumentCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index, documentIndex, NormalMode))
        }
      }

      "DocumentExtraInformationPage must go to CYA" in {
        val updatedAnswers = emptyUserAnswers
          .set(DocumentExtraInformationPage(index, documentIndex), "test").success.value
        navigator
          .nextPage(DocumentExtraInformationPage(index, documentIndex), NormalMode, updatedAnswers)
          .mustBe(controllers.addItems.documents.routes.DocumentCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index, documentIndex, NormalMode))
      }

      "AddAnotherDocument page must go to" - {

        "DocumentTypePage when user selects 'Yes'" in {
          val updatedAnswers = emptyUserAnswers
            .set(AddAnotherDocumentPage(index), true).success.value
          navigator
            .nextPage(AddAnotherDocumentPage(index), NormalMode, updatedAnswers)
            .mustBe(controllers.addItems.documents.routes.DocumentTypeController.onPageLoad(updatedAnswers.lrn, index, documentIndex, NormalMode))
        }

        "Add Administrative Reference page when user selects 'No' and is in EU" in {

          val updatedAnswers = emptyUserAnswers
            .set(AddAnotherDocumentPage(index), false).success.value
            .set(IsNonEuOfficePage, false).success.value
          navigator
            .nextPage(AddAnotherDocumentPage(index), NormalMode, updatedAnswers)
            .mustBe(previousReferencesRoutes.AddAdministrativeReferenceController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
        }

        "Reference Type page when user selects 'No', and declaration type is T2 and office of departure country is non-EU" in {

          val updatedAnswers = emptyUserAnswers
            .set(AddAnotherDocumentPage(index), false).success.value
            .set(DeclarationTypePage, DeclarationType.Option2).success.value
            .set(IsNonEuOfficePage, true).success.value

          navigator
            .nextPage(AddAnotherDocumentPage(index), NormalMode, updatedAnswers)
            .mustBe(previousReferencesRoutes.ReferenceTypeController.onPageLoad(updatedAnswers.lrn, index, referenceIndex, NormalMode))
        }
        "Reference Type page when user selects 'No', and declaration type is T2F and office of departure country is non-EU" in {

          val updatedAnswers = emptyUserAnswers
            .set(AddAnotherDocumentPage(index), false).success.value
            .set(DeclarationTypePage, DeclarationType.Option3).success.value
            .set(IsNonEuOfficePage, true).success.value

          navigator
            .nextPage(AddAnotherDocumentPage(index), NormalMode, updatedAnswers)
            .mustBe(previousReferencesRoutes.ReferenceTypeController.onPageLoad(updatedAnswers.lrn, index, referenceIndex, NormalMode))
        }
      }

      "Confirm remove Document page must go to" - {

        "AddDocument page when user selects 'No'" in {
          val updatedAnswers = emptyUserAnswers
            .set(ConfirmRemoveDocumentPage(index, documentIndex), false).success.value
          navigator
            .nextPage(ConfirmRemoveDocumentPage(index, documentIndex), NormalMode, updatedAnswers)
            .mustBe(controllers.addItems.documents.routes.AddDocumentsController.onPageLoad(updatedAnswers.lrn, index, NormalMode))

        }
        "AddDocument page when user selects 'Yes'" in {
          val updatedAnswers = emptyUserAnswers
            .set(ConfirmRemoveDocumentPage(index, documentIndex), true).success.value
          navigator
            .nextPage(ConfirmRemoveDocumentPage(index, documentIndex), NormalMode, updatedAnswers)
            .mustBe(controllers.addItems.documents.routes.AddDocumentsController.onPageLoad(updatedAnswers.lrn, index, NormalMode))

        }
      }

      "TIRCarnetReference page must go to DocumentExtraInformation page" in {

        val updatedAnswers = emptyUserAnswers
          .set(TIRCarnetReferencePage(index, documentIndex), "test").success.value

        navigator
          .nextPage(TIRCarnetReferencePage(index, documentIndex), NormalMode, updatedAnswers)
          .mustBe(controllers.addItems.documents.routes.DocumentExtraInformationController.onPageLoad(updatedAnswers.lrn, index, documentIndex, NormalMode))
      }
    }

  }

  // format: on
}
