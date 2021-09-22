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

import controllers.goodsSummary.routes
import models.domain.SealDomain
import models.{CheckMode, Index, UserAnswers}
import pages.SealIdDetailsPage
import queries.SealsQuery
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._

class AddSealCheckYourAnswersHelper(userAnswers: UserAnswers) extends CheckYourAnswersHelper(userAnswers) {

  def sealRow(sealIndex: Index): Option[Row] = getAnswerAndBuildRemovableRow[SealDomain](
    page = SealIdDetailsPage(sealIndex),
    formatAnswer = sealDomain => lit"${sealDomain.numberOrMark}",
    id = s"seal-${sealIndex.display}",
    changeCall = routes.SealIdDetailsController.onPageLoad(lrn, sealIndex, CheckMode),
    removeCall = routes.ConfirmRemoveSealController.onPageLoad(lrn, sealIndex, CheckMode)
  )

  def sealsRow(): Option[Row] = getAnswerAndBuildDynamicRow[Seq[SealDomain]](
    page = SealsQuery(),
    formatAnswer = seals => Html(seals.map(_.numberOrMark).mkString("<br>")),
    dynamicPrefix = seals => s"sealIdDetails.${if (seals.size == 1) "singular" else "plural"}",
    dynamicId = seals => Some(s"change-${if (seals.size == 1) "seal" else "seals"}"),
    call = routes.SealsInformationController.onPageLoad(lrn, CheckMode)
  )
}
