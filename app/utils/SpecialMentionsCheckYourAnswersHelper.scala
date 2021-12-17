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

package utils

import controllers.addItems.specialMentions.{routes => specialMentionRoutes}
import models.{Index, Mode, SpecialMentionList, UserAnswers}
import pages.QuestionPage
import pages.addItems.specialMentions._
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._
import viewModels.AddAnotherViewModel

class SpecialMentionsCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode) extends CheckYourAnswersHelper(userAnswers) {

  def specialMentionType(itemIndex: Index, referenceIndex: Index, specialMentions: SpecialMentionList): Option[Row] =
    getAnswerAndBuildSpecialMentionRow(
      page = SpecialMentionTypePage(itemIndex, referenceIndex),
      specialMentions = specialMentions,
      buildRow = label =>
        buildRemovableRow(
          label = label,
          id = s"special-mention-${referenceIndex.display}",
          changeCall = specialMentionRoutes.SpecialMentionCheckYourAnswersController.onPageLoad(lrn, itemIndex, referenceIndex, mode),
          removeCall = specialMentionRoutes.RemoveSpecialMentionController.onPageLoad(userAnswers.lrn, itemIndex, referenceIndex, mode)
        )
    )

  def specialMentionSectionRow(itemIndex: Index, referenceIndex: Index, specialMentions: SpecialMentionList): Option[Row] =
    getAnswerAndBuildSpecialMentionRow(
      page = SpecialMentionTypePage(itemIndex, referenceIndex),
      specialMentions = specialMentions,
      buildRow = answer =>
        buildSectionRow(
          label = msg"addAnotherSpecialMention.specialMentionList.label".withArgs(referenceIndex.display),
          answer = answer,
          id = Some(s"change-special-mention-${referenceIndex.display}"),
          call = specialMentionRoutes.SpecialMentionCheckYourAnswersController.onPageLoad(lrn, itemIndex, referenceIndex, mode)
        )
    )

  def addSpecialMention(itemIndex: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddSpecialMentionPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "addSpecialMention",
    id = None,
    call = specialMentionRoutes.AddSpecialMentionController.onPageLoad(lrn, itemIndex, mode)
  )

  def specialMentionTypeRow(itemIndex: Index, referenceIndex: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = SpecialMentionTypePage(itemIndex, referenceIndex),
    formatAnswer = formatAsLiteral,
    prefix = "specialMentionType",
    id = Some("change-special-mention-type"),
    call = specialMentionRoutes.SpecialMentionTypeController.onPageLoad(lrn, itemIndex, referenceIndex, mode)
  )

  def specialMentionAdditionalInfoRow(itemIndex: Index, referenceIndex: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = SpecialMentionAdditionalInfoPage(itemIndex, referenceIndex),
    formatAnswer = formatAsLiteral,
    prefix = "specialMentionAdditionalInfo",
    id = Some("change-special-mention-additional-information"),
    call = specialMentionRoutes.SpecialMentionAdditionalInfoController.onPageLoad(lrn, itemIndex, referenceIndex, mode)
  )

  def addAnother(itemIndex: Index, content: Text): AddAnotherViewModel = {

    val addAnotherContainerHref = userAnswers.get(AddSpecialMentionPage(itemIndex)) match {
      case Some(true) => specialMentionRoutes.AddAnotherSpecialMentionController.onPageLoad(lrn, itemIndex, mode).url
      case _          => specialMentionRoutes.AddSpecialMentionController.onPageLoad(lrn, itemIndex, mode).url
    }

    AddAnotherViewModel(addAnotherContainerHref, content)
  }

  private def getAnswerAndBuildSpecialMentionRow(
    page: QuestionPage[String],
    specialMentions: SpecialMentionList,
    buildRow: Text => Row
  ): Option[Row] = userAnswers.get(page) flatMap {
    answer =>
      specialMentions.getSpecialMention(answer) map {
        specialMention =>
          buildRow(lit"(${specialMention.code}) ${specialMention.description}")
      }
  }
}
