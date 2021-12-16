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
import pages.addItems._

class ReferencesCheckYourAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks {

  // format: off

  private def viewModel(userAnswers: UserAnswers): ReferencesCheckYourAnswersViewModel =
    ReferencesCheckYourAnswersViewModel(userAnswers, itemIndex, packageIndex, NormalMode)

  "ReferencesCheckYourAnswersViewModel" - {

    "display the correct number of rows" in {

      val userAnswers = emptyUserAnswers
        .set(ReferenceTypePage(itemIndex, documentIndex), "type").success.value
        .set(PreviousReferencePage(itemIndex, documentIndex), "ref").success.value
        .set(AddExtraInformationPage(itemIndex, documentIndex), true).success.value
        .set(ExtraInformationPage(itemIndex, documentIndex), "info").success.value

      val result = viewModel(userAnswers)
      result.section.rows.length mustEqual 4
    }
  }

  // format: on
}
