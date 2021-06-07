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

import controllers.addItems.containers.{routes => containerRoutes}
import models.{CheckMode, Index, UserAnswers}
import pages.addItems.containers.ContainerNumberPage
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._

class ContainersCheckYourAnswersHelper(userAnswers: UserAnswers) {

  //todo: this isn't the correct pattern as should be two columns
  def containerNumber(itemIndex: Index, containerIndex: Index): Option[Row] = userAnswers.get(ContainerNumberPage(itemIndex, containerIndex)) map {
    answer =>
      Row(
        key = Key(msg"containerNumber.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = containerRoutes.ContainerNumberController.onPageLoad(userAnswers.id, itemIndex, containerIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"containerNumber.checkYourAnswersLabel")),
            attributes = Map("id" -> s"""edit-container-number-${itemIndex.display}""")
          ),
          Action(
            content = msg"site.delete",
            href = containerRoutes.ConfirmRemoveContainerController.onPageLoad(userAnswers.id, itemIndex, containerIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(answer)),
            attributes = Map("id" -> s"""remove-container-number-${itemIndex.display}""")
          )
        )
      )
  }

}
