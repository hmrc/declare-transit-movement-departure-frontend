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
import models.{Index, Mode, UserAnswers}
import pages.addItems.containers.ContainerNumberPage
import uk.gov.hmrc.viewmodels.SummaryList.Row

class ContainersCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode) extends CheckYourAnswersHelper(userAnswers) {

  def containerRow(itemIndex: Index, containerIndex: Index): Option[Row] = getAnswerAndBuildRemovableRow[String](
    page = ContainerNumberPage(itemIndex, containerIndex),
    formatAnswer = formatAsLiteral,
    id = s"container-number-${itemIndex.display}-${containerIndex.display}",
    changeCall = containerRoutes.ContainerNumberController.onPageLoad(lrn, itemIndex, containerIndex, mode),
    removeCall = containerRoutes.ConfirmRemoveContainerController.onPageLoad(lrn, itemIndex, containerIndex, mode)
  )

}
