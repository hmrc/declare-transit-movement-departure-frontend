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

import models.{CheckMode, LocalReferenceNumber, UserAnswers}
import pages.DeclarePackagesPage
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._
import controllers.goodsSummary.routes

class GoodsSummaryCheckYourAnswersHelper(userAnswers: UserAnswers) {

  def declarePackages: Option[Row] = userAnswers.get(DeclarePackagesPage) map {
    answer =>
      Row(
        key     = Key(msg"declarePackages.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.DeclarePackagesController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"declarePackages.checkYourAnswersLabel"))
          )
        )
      )
  }

  def lrn: LocalReferenceNumber = userAnswers.id
}
