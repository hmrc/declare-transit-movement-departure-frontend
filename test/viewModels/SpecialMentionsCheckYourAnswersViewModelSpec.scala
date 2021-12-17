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

import base.SpecBase
import models.{NormalMode, UserAnswers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.addItems.specialMentions.{SpecialMentionAdditionalInfoPage, SpecialMentionTypePage}

class SpecialMentionsCheckYourAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks {

  // format: off

  private def viewModel(userAnswers: UserAnswers): SpecialMentionsCheckYourAnswersViewModel =
    SpecialMentionsCheckYourAnswersViewModel(userAnswers, itemIndex, referenceIndex, NormalMode)

  "SpecialMentionsCheckYourAnswersViewModel" - {

    "display the correct number of rows" in {

      val userAnswers = emptyUserAnswers
        .set(SpecialMentionTypePage(itemIndex, referenceIndex), "type").success.value
        .set(SpecialMentionAdditionalInfoPage(itemIndex, referenceIndex), "info").success.value

      val result = viewModel(userAnswers)
      result.section.rows.length mustEqual 2
    }
  }

  // format: on
}