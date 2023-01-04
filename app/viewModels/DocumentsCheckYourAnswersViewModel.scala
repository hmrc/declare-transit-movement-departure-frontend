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

package viewModels

import models.{DocumentTypeList, Index, Mode, UserAnswers}
import pages.addItems.TIRCarnetReferencePage
import uk.gov.hmrc.viewmodels.SummaryList
import utils.AddItemsCheckYourAnswersHelper
import viewModels.sections.Section

object DocumentsCheckYourAnswersViewModel {

  def apply(
    userAnswers: UserAnswers,
    itemIndex: Index,
    documentIndex: Index,
    mode: Mode,
    documentTypes: DocumentTypeList
  ): DocumentsCheckYourAnswersViewModel = {

    val checkYourAnswersHelper = new AddItemsCheckYourAnswersHelper(userAnswers, mode)

    def documentRows: Seq[SummaryList.Row] =
      (userAnswers.get(TIRCarnetReferencePage(itemIndex, documentIndex)) match {
        case Some(_) =>
          Seq(
            checkYourAnswersHelper.tirCarnetReferenceRow(itemIndex, documentIndex),
            checkYourAnswersHelper.extraDocumentInformationRow(itemIndex, documentIndex)
          )
        case None =>
          Seq(
            checkYourAnswersHelper.documentTypeRow(itemIndex, documentIndex, documentTypes),
            checkYourAnswersHelper.documentReferenceRow(itemIndex, documentIndex),
            checkYourAnswersHelper.addExtraDocumentInformationRow(itemIndex, documentIndex),
            checkYourAnswersHelper.extraDocumentInformationRow(itemIndex, documentIndex)
          )
      }).flatten

    DocumentsCheckYourAnswersViewModel(
      Section(documentRows)
    )
  }

}

case class DocumentsCheckYourAnswersViewModel(section: Section)
