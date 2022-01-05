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

import base.SpecBase
import models.reference._
import models.{NormalMode, UserAnswers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.addItems._

class PackagesCheckYourAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks {

  // format: off
  // scalastyle:off magic.number

  private def viewModel(userAnswers: UserAnswers): PackagesCheckYourAnswersViewModel =
    PackagesCheckYourAnswersViewModel(userAnswers, itemIndex, packageIndex, NormalMode)

  "PackagesCheckYourAnswersViewModel" - {

    "display the correct number of rows" - {

      "when how many packages" in {
        val userAnswers = emptyUserAnswers
          .set(PackageTypePage(itemIndex, packageIndex), PackageType("AB", "Description")).success.value
          .set(HowManyPackagesPage(itemIndex, packageIndex), 123).success.value
          .set(DeclareMarkPage(itemIndex, packageIndex), "mark").success.value

        val result = viewModel(userAnswers)
        result.section.rows.length mustEqual 3
      }

      "when total pieces" in {
        val userAnswers = emptyUserAnswers
          .set(PackageTypePage(itemIndex, packageIndex), PackageType("AB", "Description")).success.value
          .set(TotalPiecesPage(itemIndex, packageIndex), 123).success.value
          .set(AddMarkPage(itemIndex, packageIndex), true).success.value
          .set(DeclareMarkPage(itemIndex, packageIndex), "mark").success.value

        val result = viewModel(userAnswers)
        result.section.rows.length mustEqual 4
      }
    }
  }

  // format: on
  // scalastyle:on magic.number
}
