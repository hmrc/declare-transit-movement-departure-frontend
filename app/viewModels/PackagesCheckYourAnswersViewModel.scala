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

import models.{Index, Mode, UserAnswers}
import uk.gov.hmrc.viewmodels.SummaryList
import utils.AddItemsCheckYourAnswersHelper
import viewModels.sections.Section

object PackagesCheckYourAnswersViewModel {

  def apply(
    userAnswers: UserAnswers,
    itemIndex: Index,
    packageIndex: Index,
    mode: Mode
  ): PackagesCheckYourAnswersViewModel = {

    val checkYourAnswersHelper = new AddItemsCheckYourAnswersHelper(userAnswers, mode)

    def packageRows: Seq[SummaryList.Row] =
      Seq(
        checkYourAnswersHelper.packageType(itemIndex, packageIndex),
        checkYourAnswersHelper.totalPieces(itemIndex, packageIndex),
        checkYourAnswersHelper.numberOfPackages(itemIndex, packageIndex),
        checkYourAnswersHelper.addMark(itemIndex, packageIndex),
        checkYourAnswersHelper.mark(itemIndex, packageIndex)
      ).flatten

    PackagesCheckYourAnswersViewModel(
      Section(packageRows)
    )
  }

}

case class PackagesCheckYourAnswersViewModel(section: Section)
