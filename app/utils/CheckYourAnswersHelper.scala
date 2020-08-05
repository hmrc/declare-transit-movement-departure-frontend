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

package utils

import java.time.format.DateTimeFormatter

import controllers.routes
import pages._
import models.{CheckMode, LocalReferenceNumber, UserAnswers}
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels._

class CheckYourAnswersHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  def declarationPlace: Option[Row] = userAnswers.get(declarationPlacePage) map {
    answer =>
      Row(
        key     = Key(msg"declarationPlace.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"${answer.field1} "),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.declarationPlaceController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"declarationPlace.checkYourAnswersLabel"))
          )
        )
      )
  }

  def declarationType: Option[Row] = userAnswers.get(DeclarationTypePage) map {
    answer =>
      Row(
        key     = Key(msg"declarationType.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(msg"declarationType.$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.DeclarationTypeController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"declarationType.checkYourAnswersLabel"))
          )
        )
      )
  }

  def addSecurityDetails: Option[Row] = userAnswers.get(AddSecurityDetailsPage) map {
    answer =>
      Row(
        key     = Key(msg"addSecurityDetails.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddSecurityDetailsController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addSecurityDetails.checkYourAnswersLabel"))
          )
        )
      )
  }

  private def yesOrNo(answer: Boolean): Content =
    if (answer) {
      msg"site.yes"
    } else {
      msg"site.no"
    }

  def lrn: LocalReferenceNumber = userAnswers.id
}

object CheckYourAnswersHelper {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
}
