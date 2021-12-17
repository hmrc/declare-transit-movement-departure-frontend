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

package viewModels

import models.{Index, Mode, SpecialMentionList, UserAnswers}
import uk.gov.hmrc.viewmodels.SummaryList
import utils.SpecialMentionsCheckYourAnswersHelper
import viewModels.sections.Section

object SpecialMentionsCheckYourAnswersViewModel {

  def apply(
    userAnswers: UserAnswers,
    itemIndex: Index,
    referenceIndex: Index,
    mode: Mode,
    specialMentions: SpecialMentionList
  ): SpecialMentionsCheckYourAnswersViewModel = {

    val checkYourAnswersHelper = new SpecialMentionsCheckYourAnswersHelper(userAnswers, mode)

    def specialMentionRows: Seq[SummaryList.Row] =
      Seq(
        checkYourAnswersHelper.specialMentionTypeRow(itemIndex, referenceIndex, specialMentions),
        checkYourAnswersHelper.specialMentionAdditionalInfoRow(itemIndex, referenceIndex)
      ).flatten

    SpecialMentionsCheckYourAnswersViewModel(
      Section(specialMentionRows)
    )
  }

}

case class SpecialMentionsCheckYourAnswersViewModel(section: Section)
