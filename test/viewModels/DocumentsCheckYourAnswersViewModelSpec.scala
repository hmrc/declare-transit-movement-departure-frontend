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

package viewModels

import base.SpecBase
import models.reference.DocumentType
import models.{DocumentTypeList, NormalMode, UserAnswers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.addItems._

class DocumentsCheckYourAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks {

  // format: off

  private val code = "code"
  private val document: DocumentType = DocumentType(code, "description", transportDocument = true)

  private def viewModel(userAnswers: UserAnswers): DocumentsCheckYourAnswersViewModel =
    DocumentsCheckYourAnswersViewModel(userAnswers, itemIndex, packageIndex, NormalMode, DocumentTypeList(Seq(document)))

  "DocumentsCheckYourAnswersViewModel" - {

    "display the correct number of rows" - {

      "when TIR carnet reference" in {
        val userAnswers = emptyUserAnswers
          .set(TIRCarnetReferencePage(itemIndex, documentIndex), "ref").success.value
          .set(DocumentTypePage(itemIndex, documentIndex), code).success.value
          .set(DocumentExtraInformationPage(itemIndex, documentIndex), "info").success.value

        val result = viewModel(userAnswers)
        result.section.rows.length mustEqual 2
      }

      "when not TIR carnet reference" in {
        val userAnswers = emptyUserAnswers
          .set(DocumentTypePage(itemIndex, documentIndex), code).success.value
          .set(DocumentReferencePage(itemIndex, documentIndex), "ref").success.value
          .set(AddExtraDocumentInformationPage(itemIndex, documentIndex), true).success.value
          .set(DocumentExtraInformationPage(itemIndex, documentIndex), "info").success.value

        val result = viewModel(userAnswers)
        result.section.rows.length mustEqual 4
      }
    }
  }

  // format: on
}
