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

import derivable.DeriveNumberOfSeals
import models.{CheckMode, UserAnswers}
import uk.gov.hmrc.viewmodels.SummaryList
import utils.{AddSealCheckYourAnswersHelper, GoodsSummaryCheckYourAnswersHelper}
import viewModels.sections.Section

case class GoodsSummaryCheckYourAnswersViewModel(sections: Seq[Section])

object GoodsSummaryCheckYourAnswersViewModel {

  def apply(userAnswers: UserAnswers): GoodsSummaryCheckYourAnswersViewModel = {

    val goodsSummaryCheckYourAnswersHelper = new GoodsSummaryCheckYourAnswersHelper(userAnswers, CheckMode)
    val addSealHelper                      = new AddSealCheckYourAnswersHelper(userAnswers, CheckMode)

    val authorisedLocationCode: Option[SummaryList.Row]     = goodsSummaryCheckYourAnswersHelper.authorisedLocationCode
    val controlResultDateLimit: Option[SummaryList.Row]     = goodsSummaryCheckYourAnswersHelper.controlResultDateLimit
    val addCustomsApprovedLocation: Option[SummaryList.Row] = goodsSummaryCheckYourAnswersHelper.addCustomsApprovedLocation
    val customsApprovedLocation: Option[SummaryList.Row]    = goodsSummaryCheckYourAnswersHelper.customsApprovedLocation
    val addAgreedLocationOfGoods: Option[SummaryList.Row]   = goodsSummaryCheckYourAnswersHelper.addAgreedLocationOfGoods
    val agreedLocationOfGoods: Option[SummaryList.Row]      = goodsSummaryCheckYourAnswersHelper.agreedLocationOfGoods
    val loadingPlace: Option[SummaryList.Row]               = goodsSummaryCheckYourAnswersHelper.loadingPlace
    val addSeals: Option[SummaryList.Row]                   = goodsSummaryCheckYourAnswersHelper.addSeals
    val numberOfSeals                                       = userAnswers.get(DeriveNumberOfSeals).getOrElse(0)
    val seals                                               = if (numberOfSeals == 0) None else addSealHelper.sealsRow()
    val sealsInformation: Option[SummaryList.Row]           = goodsSummaryCheckYourAnswersHelper.sealsInformation

    val checkYourAnswersData = Seq(
      authorisedLocationCode,
      controlResultDateLimit,
      addCustomsApprovedLocation,
      customsApprovedLocation,
      addAgreedLocationOfGoods,
      agreedLocationOfGoods,
      loadingPlace,
      addSeals,
      seals,
      sealsInformation
    ).flatten

    GoodsSummaryCheckYourAnswersViewModel(Seq(Section(checkYourAnswersData)))
  }
}
