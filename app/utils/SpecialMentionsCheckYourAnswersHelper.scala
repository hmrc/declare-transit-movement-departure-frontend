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
import models.{CheckMode, Index, Mode, SpecialMentionList, UserAnswers}
import pages.addItems.specialMentions._
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._
import viewModels.AddAnotherViewModel

class SpecialMentionsCheckYourAnswersHelper(userAnswers: UserAnswers) extends CheckYourAnswersHelper(userAnswers) {

  def specialMentionType(itemIndex: Index, referenceIndex: Index, specialMentions: SpecialMentionList, mode: Mode): Option[Row] =
    userAnswers.get(SpecialMentionTypePage(itemIndex, referenceIndex)) flatMap {
      answer =>
        specialMentions.getSpecialMention(answer) map {
          specialMention =>
            val updatedAnswer = s"(${specialMention.code}) ${specialMention.description}"
            Row(
              key = Key(msg"$updatedAnswer"),
              value = Value(lit""),
              actions = List(
                Action(
                  content = msg"site.change",
                  href = specialMentionRoutes.SpecialMentionTypeController.onPageLoad(lrn, itemIndex, referenceIndex, mode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"$updatedAnswer")),
                  attributes = Map("id" -> s"change-special-mentions-${itemIndex.display}")
                ),
                Action(
                  content = msg"site.delete",
                  href = specialMentionRoutes.RemoveSpecialMentionController.onPageLoad(userAnswers.lrn, itemIndex, referenceIndex, mode).url,
                  visuallyHiddenText = Some(msg"site.delete.hidden".withArgs(updatedAnswer)),
                  attributes = Map("id" -> s"remove-special-mentions-${itemIndex.display}")
                )
              )
            )
        }
    }

  def specialMentionTypeNoRemoval(itemIndex: Index, referenceIndex: Index, specialMentions: SpecialMentionList): Option[Row] =
    userAnswers.get(SpecialMentionTypePage(itemIndex, referenceIndex)) flatMap {
      answer =>
        specialMentions.getSpecialMention(answer) map {
          specialMention =>
            val updatedAnswer = s"(${specialMention.code}) ${specialMention.description}"
            Row(
              key = Key(msg"$updatedAnswer"),
              value = Value(lit""),
              actions = List(
                Action(
                  content = msg"site.change",
                  href = specialMentionRoutes.SpecialMentionTypeController.onPageLoad(lrn, itemIndex, referenceIndex, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"$updatedAnswer")),
                  attributes = Map("id" -> s"change-special-mentions-${itemIndex.display}")
                )
              )
            )
        }
    }

  def addSpecialMention(itemIndex: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddSpecialMentionPage(itemIndex),
    format = yesOrNo,
    prefix = "addSpecialMention",
    id = None,
    call = specialMentionRoutes.AddSpecialMentionController.onPageLoad(lrn, itemIndex, CheckMode)
  )

  def addAnother(itemIndex: Index, content: Text): AddAnotherViewModel = {

    val addAnotherContainerHref = specialMentionRoutes.AddAnotherSpecialMentionController.onPageLoad(lrn, itemIndex, CheckMode).url

    AddAnotherViewModel(addAnotherContainerHref, content)
  }
}
