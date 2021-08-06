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
import controllers.addItems.routes
import generators.Generators
import models.{CheckMode, DeclarationType, NormalMode}
import navigation.annotations.addItemsNavigators.AddItemsDocumentNavigator
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.DeclarationTypePage
import pages.addItems._
import queries.DocumentQuery

class AddItemsDocumentCheckModeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {
  // format: off
  val navigator = new AddItemsDocumentNavigator

  "Document navigator" - {

    "In CheckMode" - {
      "AddDocumentPage must go to" - {
        "CYA when user selects 'no'" in {
          val updatedAnswers = emptyUserAnswers
            .set(AddDocumentsPage(index), false).success.value
          navigator
            .nextPage(AddDocumentsPage(index), CheckMode, updatedAnswers)
            .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
        }
      }

      "AddDocumentPage must go to DocumentTypePage when user selects 'yes' when previously selected no" in {
        val updatedAnswers = emptyUserAnswers
          .remove(DocumentQuery(index, documentIndex)).success.value
          .set(AddDocumentsPage(index), true).success.value

        navigator
          .nextPage(AddDocumentsPage(index), CheckMode, updatedAnswers)
          .mustBe(controllers.addItems.documents.routes.DocumentTypeController.onPageLoad(updatedAnswers.lrn, index, index, CheckMode))
      }

      "AddDocumentPage must go to ItemsCheckYourAnswersPage when user selects 'yes' when previously selected Yes" in {
        val updatedAnswers = emptyUserAnswers
          .set(AddDocumentsPage(index), true).success.value
          .set(DocumentTypePage(index, documentIndex), "test").success.value
          .set(DocumentReferencePage(index, documentIndex), "test").success.value
        navigator
          .nextPage(AddDocumentsPage(index), CheckMode, updatedAnswers)
          .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
      }
    }

    "DocumentTypePage must go to DocumentReferencePage" in {
      val updatedAnswers = emptyUserAnswers
        .set(DocumentTypePage(index, documentIndex), "Test").success.value
      navigator
        .nextPage(DocumentTypePage(index, documentIndex), CheckMode, updatedAnswers)
        .mustBe(controllers.addItems.documents.routes.DocumentReferenceController.onPageLoad(updatedAnswers.lrn, index, documentIndex, CheckMode))
    }

    "DocumentReferencePage must go to AddExtraDocumentInformationPage" in {
      val updatedAnswers = emptyUserAnswers
        .set(DocumentReferencePage(index, documentIndex), "Test").success.value
      navigator
        .nextPage(DocumentReferencePage(index, documentIndex), CheckMode, updatedAnswers)
        .mustBe(controllers.addItems.documents.routes.AddExtraDocumentInformationController.onPageLoad(updatedAnswers.lrn, index, documentIndex, CheckMode))
    }


    "AddDocumentExtraInformationPage must go to" - {
      "AddAnotherDocumentPage if user selects 'No'" in {
        val updatedAnswers = emptyUserAnswers
          .set(AddExtraDocumentInformationPage(index, documentIndex), false).success.value
        navigator
          .nextPage(AddExtraDocumentInformationPage(index, documentIndex), CheckMode, updatedAnswers)
          .mustBe(controllers.addItems.documents.routes.AddAnotherDocumentController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
      }

      "DocumentExtraInformationPage if user selects 'Yes'" in {
        val updatedAnswers = emptyUserAnswers
          .set(AddExtraDocumentInformationPage(index, documentIndex), true).success.value
        navigator
          .nextPage(AddExtraDocumentInformationPage(index, documentIndex), CheckMode, updatedAnswers)
          .mustBe(controllers.addItems.documents.routes.DocumentExtraInformationController.onPageLoad(updatedAnswers.lrn, index, documentIndex, CheckMode))
      }
    }

    "DocumentExtraInformationPage must go to AddAnotherDocumentPage" in {
      val updatedAnswers = emptyUserAnswers
        .set(DocumentExtraInformationPage(index, documentIndex), "Test").success.value
      navigator
        .nextPage(DocumentExtraInformationPage(index, documentIndex), CheckMode, updatedAnswers)
        .mustBe(controllers.addItems.documents.routes.AddAnotherDocumentController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
    }

    "AddAnotherDocumentPage must go to" - {
      "DocumentType if user selects 'Yes'" in {
        val updatedAnswers = emptyUserAnswers
          .set(AddDocumentsPage(index), true).success.value
        navigator
          .nextPage(AddDocumentsPage(index), CheckMode, updatedAnswers)
          .mustBe(controllers.addItems.documents.routes.DocumentTypeController.onPageLoad(updatedAnswers.lrn, index, documentIndex, CheckMode))
      }

      "ItemDetailsCheckYourAnswers if user selects 'No'" in {
        val updatedAnswers = emptyUserAnswers
          .set(AddDocumentsPage(index), false).success.value
        navigator
          .nextPage(AddDocumentsPage(index), CheckMode, updatedAnswers)
          .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
      }
    }
    "Confirm remove Document page must go to AddDocument page when user selects NO" in {
      val updatedAnswers = emptyUserAnswers
        .set(ConfirmRemoveDocumentPage(index, documentIndex), false).success.value
      navigator
        .nextPage(ConfirmRemoveDocumentPage(index, documentIndex), CheckMode, updatedAnswers)
        .mustBe(controllers.addItems.documents.routes.AddDocumentsController.onPageLoad(updatedAnswers.lrn, index, CheckMode))

    }
    "Confirm remove Document page must go to AddDocument page when user selects yes" in {
      val updatedAnswers = emptyUserAnswers
        .set(ConfirmRemoveDocumentPage(index, documentIndex), true).success.value
      navigator
        .nextPage(ConfirmRemoveDocumentPage(index, documentIndex), CheckMode, updatedAnswers)
        .mustBe(controllers.addItems.documents.routes.AddDocumentsController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
    }

    "TIRCarnetReference page must go to DocumentExtraInformation page" in {

      val updatedAnswers = emptyUserAnswers
        .set(TIRCarnetReferencePage(index, documentIndex), "test").success.value

      navigator
        .nextPage(TIRCarnetReferencePage(index, documentIndex), CheckMode, updatedAnswers)
        .mustBe(controllers.addItems.documents.routes.DocumentExtraInformationController.onPageLoad(updatedAnswers.lrn, index, documentIndex, CheckMode))
    }
  }
  // format: on
}
