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

import derivable.DeriveNumberOfSeals
import models.UserAnswers
import uk.gov.hmrc.viewmodels.SummaryList
import utils.{AddSealHelper, CheckYourAnswersHelper, GoodsSummaryCheckYourAnswersHelper}
import viewModels.sections.Section

case class GoodsSummaryCheckYourAnswersViewModel(sections: Seq[Section])

object GoodsSummaryCheckYourAnswersViewModel {

  def apply(userAnswers: UserAnswers): GoodsSummaryCheckYourAnswersViewModel = {

    val goodsSummaryheckYourAnswersHelper = new GoodsSummaryCheckYourAnswersHelper(userAnswers)
    val checkYourAnswersHelper            = new CheckYourAnswersHelper(userAnswers)
    val addSealHelper                     = new AddSealHelper(userAnswers)

    val totalPackages: Option[SummaryList.Row]              = goodsSummaryheckYourAnswersHelper.totalPackages
    val authorisedLocationCode: Option[SummaryList.Row]     = goodsSummaryheckYourAnswersHelper.authorisedLocationCode
    val controlResultDateLimit: Option[SummaryList.Row]     = goodsSummaryheckYourAnswersHelper.controlResultDateLimit
    val addCustomsApprovedLocation: Option[SummaryList.Row] = goodsSummaryheckYourAnswersHelper.addCustomsApprovedLocation
    val customsApprovedLocation: Option[SummaryList.Row]    = goodsSummaryheckYourAnswersHelper.customsApprovedLocation
    val addAgreedLocationOfGoods: Option[SummaryList.Row]   = goodsSummaryheckYourAnswersHelper.addAgreedLocationOfGoods
    val agreedLocationOfGoods: Option[SummaryList.Row]      = goodsSummaryheckYourAnswersHelper.agreedLocationOfGoods
    val loadingPlace: Option[SummaryList.Row]               = checkYourAnswersHelper.loadingPlace
    val addSeals: Option[SummaryList.Row]                   = goodsSummaryheckYourAnswersHelper.addSeals
    val numberOfSeals                                       = userAnswers.get(DeriveNumberOfSeals).getOrElse(0)
    val seals                                               = if (numberOfSeals == 0) None else addSealHelper.sealsRow(userAnswers.id)
    val sealsInformation: Option[SummaryList.Row]           = goodsSummaryheckYourAnswersHelper.sealsInformation

    val checkYourAnswersData = Seq(
      totalPackages,
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
