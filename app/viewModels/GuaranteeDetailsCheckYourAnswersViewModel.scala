/*
 * Copyright 2022 HM Revenue & Customs
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

import models.{CheckMode, Index, UserAnswers}
import uk.gov.hmrc.viewmodels.SummaryList
import utils.GuaranteeDetailsCheckYourAnswersHelper
import viewModels.sections.Section

case class GuaranteeDetailsCheckYourAnswersViewModel(sections: Seq[Section])

object GuaranteeDetailsCheckYourAnswersViewModel {

  def apply(userAnswers: UserAnswers, index: Index): GuaranteeDetailsCheckYourAnswersViewModel = {

    val checkYourAnswersHelper                      = new GuaranteeDetailsCheckYourAnswersHelper(userAnswers, CheckMode)
    val guaranteeType: Option[SummaryList.Row]      = checkYourAnswersHelper.guaranteeType(index)
    val guaranteeReference: Option[SummaryList.Row] = checkYourAnswersHelper.guaranteeReference(index)
    val otherReference: Option[SummaryList.Row]     = checkYourAnswersHelper.otherReference(index)
    val liabilityAmount: Option[SummaryList.Row]    = checkYourAnswersHelper.liabilityAmount(index)
    val accessCode: Option[SummaryList.Row]         = checkYourAnswersHelper.accessCode(index)
    val defaultAmount: Option[SummaryList.Row]      = checkYourAnswersHelper.defaultAmount(index)
    val tirReferenceAmount: Option[SummaryList.Row] = checkYourAnswersHelper.tirGuaranteeReference(index)

    val checkYourAnswersData = Seq(
      guaranteeType,
      guaranteeReference,
      otherReference,
      liabilityAmount,
      defaultAmount,
      tirReferenceAmount,
      accessCode
    ).flatten

    GuaranteeDetailsCheckYourAnswersViewModel(Seq(Section(checkYourAnswersData)))
  }
}
