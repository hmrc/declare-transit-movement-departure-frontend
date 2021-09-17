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

import controllers.goodsSummary.routes._
import models.{CheckMode, Index, UserAnswers}
import pages.SealIdDetailsPage
import queries.SealsQuery
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._

class AddSealCheckYourAnswersHelper(userAnswers: UserAnswers) extends CheckYourAnswersHelper(userAnswers) {

  def sealRow(sealIndex: Index): Option[Row] =
    userAnswers.get(SealIdDetailsPage(sealIndex)).map {
      answer =>
        Row(
          key = Key(msg"sealsInformation.sealList.label".withArgs(sealIndex.display)),
          value = Value(lit"${answer.numberOrMark}"),
          actions = List(
            Action(
              content = msg"site.edit",
              href = SealIdDetailsController.onPageLoad(lrn, sealIndex, CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"sealsInformation.sealList.label".withArgs(sealIndex.display))),
              attributes = Map("id" -> s"change-seal-${sealIndex.display}")
            ),
            Action(
              content = msg"site.delete",
              href = ConfirmRemoveSealController.onPageLoad(lrn, sealIndex, CheckMode).url,
              visuallyHiddenText = Some(msg"site.delete.hidden".withArgs(msg"sealsInformation.sealList.label".withArgs(sealIndex.display))),
              attributes = Map("id" -> s"remove-seal-${sealIndex.display}")
            )
          )
        )
    }

  def sealsRow(): Option[Row] = userAnswers.get(SealsQuery()).map {
    answer =>
      val numberOfSeals = answer.size

      val singularOrPlural = if (numberOfSeals == 1) "singular" else "plural"
      val idPluralisation  = if (numberOfSeals == 1) "change-seal" else "change-seals"
      val html             = Html(answer.map(_.numberOrMark).mkString("<br>"))

      Row(
        key = Key(msg"sealIdDetails.checkYourAnswersLabel.$singularOrPlural"),
        value = Value(html),
        actions = List(
          Action(
            content = msg"site.edit",
            href = SealsInformationController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"sealIdDetails.checkYourAnswersLabel.$singularOrPlural"),
            attributes = Map("id" -> idPluralisation)
          )
        )
      )
  }
}
