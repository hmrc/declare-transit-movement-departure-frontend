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
import controllers.addItems.routes
import generators.Generators
import models.{CheckMode, NormalMode}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.addItems._
import queries.DocumentQuery

class DocumentNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {
  // format: off
  val navigator = new DocumentNavigator

  "Document navigator" - {
    "in Normal Mode" - {
      "AddDocumentPage must go to CYA when user selects 'no'" in {
        val updatedAnswers = emptyUserAnswers
          .set(AddDocumentsPage(index), false).success.value
        navigator
          .nextPage(AddDocumentsPage(index), NormalMode, updatedAnswers)
          .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
      }

      "AddDocumentPage must go to DocumentTypePage when user selects 'yes'" in {
        val updatedAnswers = emptyUserAnswers
          .set(AddDocumentsPage(index), true).success.value
        navigator
          .nextPage(AddDocumentsPage(index), NormalMode, updatedAnswers)
          .mustBe(routes.DocumentTypeController.onPageLoad(updatedAnswers.id, index, index, NormalMode))
      }

      "DocumentTypePage must go to DocumentReferencePage" in {
        val updatedAnswers = emptyUserAnswers
          .set(DocumentTypePage(index, documentIndex), "test").success.value
        navigator
          .nextPage(DocumentTypePage(index, documentIndex), NormalMode, updatedAnswers)
          .mustBe(routes.DocumentReferenceController.onPageLoad(updatedAnswers.id, index, documentIndex, NormalMode))
      }

      "DocumentReferencePage must go to AddExtraDocumentInformation page" in {
        val updatedAnswers = emptyUserAnswers
          .set(DocumentReferencePage(index, documentIndex), "test").success.value
        navigator
          .nextPage(DocumentReferencePage(index, documentIndex), NormalMode, updatedAnswers)
          .mustBe(routes.AddExtraDocumentInformationController.onPageLoad(updatedAnswers.id, index, documentIndex, NormalMode))
      }
      "AddExtraDocumentInformation page must go to" - {
        "DocumentExtraInformationPage when user selects 'Yes' " in {
          val updatedAnswers = emptyUserAnswers
            .set(AddExtraDocumentInformationPage(index, documentIndex), true).success.value
          navigator
            .nextPage(AddExtraDocumentInformationPage(index, documentIndex), NormalMode, updatedAnswers)
            .mustBe(routes.DocumentExtraInformationController.onPageLoad(updatedAnswers.id, index, documentIndex, NormalMode))
        }

        "AddAnotherDocument page when user selects 'No' " in {
          val updatedAnswers = emptyUserAnswers
            .set(AddExtraDocumentInformationPage(index, documentIndex), false).success.value
          navigator
            .nextPage(AddExtraDocumentInformationPage(index, documentIndex), NormalMode, updatedAnswers)
            .mustBe(routes.AddAnotherDocumentController.onPageLoad(updatedAnswers.id, index, NormalMode))
        }
      }

      "DocumentExtraInformationPage must go to AddAnotherDocument" in {
        val updatedAnswers = emptyUserAnswers
          .set(DocumentExtraInformationPage(index, documentIndex), "test").success.value
        navigator
          .nextPage(DocumentExtraInformationPage(index, documentIndex), NormalMode, updatedAnswers)
          .mustBe(routes.AddAnotherDocumentController.onPageLoad(updatedAnswers.id, index, NormalMode))
      }

      "AddAnotherDocument page must go to" - {
        "DocumentTypePage when user selects 'Yes'" in {
          val updatedAnswers = emptyUserAnswers
            .set(AddAnotherDocumentPage(index), true).success.value
          navigator
            .nextPage(AddAnotherDocumentPage(index), NormalMode, updatedAnswers)
            .mustBe(routes.DocumentTypeController.onPageLoad(updatedAnswers.id, index, documentIndex, NormalMode))
        }
      }
      "ItemsCheckYourANswersPage when user selects 'No'" in {
        val updatedAnswers = emptyUserAnswers
          .set(AddAnotherDocumentPage(index), false).success.value
        navigator
          .nextPage(AddAnotherDocumentPage(index), NormalMode, updatedAnswers)
          .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
      }
    }
    "Confirm remove Document page must go to AddDocument page when user selects NO" in {
      val updatedAnswers = emptyUserAnswers
        .set(ConfirmRemoveDocumentPage(index, documentIndex), false).success.value
      navigator
        .nextPage(ConfirmRemoveDocumentPage(index, documentIndex), NormalMode, updatedAnswers)
        .mustBe(routes.AddDocumentsController.onPageLoad(updatedAnswers.id, index, NormalMode))

    }
    "Confirm remove Document page must go to AddDocument page when user selects yes" in {
      val updatedAnswers = emptyUserAnswers
        .set(ConfirmRemoveDocumentPage(index, documentIndex), true).success.value
      navigator
        .nextPage(ConfirmRemoveDocumentPage(index, documentIndex), NormalMode, updatedAnswers)
        .mustBe(routes.AddDocumentsController.onPageLoad(updatedAnswers.id, index, NormalMode))

    }
  }
    "In CheckMode" - {
      "AddDocumentPage must go to" - {
        "CYA when user selects 'no'" in {
          val updatedAnswers = emptyUserAnswers
            .set(AddDocumentsPage(index), false).success.value
          navigator
            .nextPage(AddDocumentsPage(index), CheckMode, updatedAnswers)
            .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
        }
      }

      "AddDocumentPage must go to DocumentTypePage when user selects 'yes' when previously selected no" in {
        val updatedAnswers = emptyUserAnswers
          .remove(DocumentQuery(index, documentIndex)).success.value
          .set(AddDocumentsPage(index), true).success.value

        navigator
          .nextPage(AddDocumentsPage(index), CheckMode, updatedAnswers)
          .mustBe(routes.DocumentTypeController.onPageLoad(updatedAnswers.id, index, index, CheckMode))
      }

      "AddDocumentPage must go to ItemsCheckYourAnswersPage when user selects 'yes' when previously selected Yes" in {
        val updatedAnswers = emptyUserAnswers
          .set(AddDocumentsPage(index), true).success.value
          .set(DocumentTypePage(index, documentIndex), "test").success.value
          .set(DocumentReferencePage(index, documentIndex), "test").success.value
        navigator
          .nextPage(AddDocumentsPage(index), CheckMode, updatedAnswers)
          .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
      }
    }

    "DocumentTypePage must go to DocumentReferencePage" in {
      val updatedAnswers = emptyUserAnswers
        .set(DocumentTypePage(index, documentIndex), "Test").success.value
      navigator
        .nextPage(DocumentTypePage(index, documentIndex), CheckMode, updatedAnswers)
        .mustBe(routes.DocumentReferenceController.onPageLoad(updatedAnswers.id, index, documentIndex, CheckMode))
    }

    "DocumentReferencePage must go to AddExtraDocumentInformationPage" in {
      val updatedAnswers = emptyUserAnswers
        .set(DocumentReferencePage(index, documentIndex), "Test").success.value
      navigator
        .nextPage(DocumentReferencePage(index, documentIndex), CheckMode, updatedAnswers)
        .mustBe(controllers.addItems.routes.AddExtraDocumentInformationController.onPageLoad(updatedAnswers.id, index, documentIndex, CheckMode))
    }


    "AddDocumentExtraInformationPage must go to" - {
      "AddAnotherDocumentPage if user selects 'No'" in {
        val updatedAnswers = emptyUserAnswers
          .set(AddExtraDocumentInformationPage(index, documentIndex), false).success.value
        navigator
          .nextPage(AddExtraDocumentInformationPage(index, documentIndex), CheckMode, updatedAnswers)
          .mustBe(routes.AddAnotherDocumentController.onPageLoad(updatedAnswers.id, index, CheckMode))
      }

      "DocumentExtraInformationPage if user selects 'Yes'" in {
        val updatedAnswers = emptyUserAnswers
          .set(AddExtraDocumentInformationPage(index, documentIndex), true).success.value
        navigator
          .nextPage(AddExtraDocumentInformationPage(index, documentIndex), CheckMode, updatedAnswers)
          .mustBe(routes.DocumentExtraInformationController.onPageLoad(updatedAnswers.id, index, documentIndex, CheckMode))
      }
    }

    "DocumentExtraInformationPage must go to AddAnotherDocumentPage" in {
      val updatedAnswers = emptyUserAnswers
        .set(DocumentExtraInformationPage(index, documentIndex), "Test").success.value
      navigator
        .nextPage(DocumentExtraInformationPage(index, documentIndex), CheckMode, updatedAnswers)
        .mustBe(controllers.addItems.routes.AddAnotherDocumentController.onPageLoad(updatedAnswers.id, index, CheckMode))
    }

    "AddAnotherDocumentPage must go to" - {
      "DocumentType if user selects 'Yes'" in {
        val updatedAnswers = emptyUserAnswers
          .set(AddDocumentsPage(index), true).success.value
        navigator
          .nextPage(AddDocumentsPage(index), CheckMode, updatedAnswers)
          .mustBe(controllers.addItems.routes.DocumentTypeController.onPageLoad(updatedAnswers.id, index, documentIndex, CheckMode))
      }

      "ItemDetailsCheckYourAnswers if user selects 'No'" in {
        val updatedAnswers = emptyUserAnswers
          .set(AddDocumentsPage(index), false).success.value
        navigator
          .nextPage(AddDocumentsPage(index), CheckMode, updatedAnswers)
          .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
      }
    }
  "Confirm remove Document page must go to AddDocument page when user selects NO" in {
    val updatedAnswers = emptyUserAnswers
      .set(ConfirmRemoveDocumentPage(index, documentIndex), false).success.value
    navigator
      .nextPage(ConfirmRemoveDocumentPage(index, documentIndex), CheckMode, updatedAnswers)
      .mustBe(routes.AddDocumentsController.onPageLoad(updatedAnswers.id, index, CheckMode))

  }
  "Confirm remove Document page must go to AddDocument page when user selects yes" in {
    val updatedAnswers = emptyUserAnswers
      .set(ConfirmRemoveDocumentPage(index, documentIndex), true).success.value
    navigator
      .nextPage(ConfirmRemoveDocumentPage(index, documentIndex), CheckMode, updatedAnswers)
      .mustBe(routes.AddDocumentsController.onPageLoad(updatedAnswers.id, index, CheckMode))

  }
  // format: on
}
