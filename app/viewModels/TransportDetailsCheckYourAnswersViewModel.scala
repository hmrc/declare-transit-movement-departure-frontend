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

import models.{CheckMode, CountryList, TransportModeList, UserAnswers}
import pages.InlandModePage
import uk.gov.hmrc.viewmodels.SummaryList
import utils.TransportDetailsCheckYourAnswersHelper
import viewModels.sections.Section

case class TransportDetailsCheckYourAnswersViewModel(sections: Seq[Section])

object TransportDetailsCheckYourAnswersViewModel {

  def apply(userAnswers: UserAnswers, countryList: CountryList, transportModeList: TransportModeList): TransportDetailsCheckYourAnswersViewModel = {

    val checkYourAnswersHelper = new TransportDetailsCheckYourAnswersHelper(userAnswers, CheckMode)
    val inlandModeCode         = userAnswers.get(InlandModePage).get

    val inlandMode: Option[SummaryList.Row]                = checkYourAnswersHelper.inlandMode(transportModeList)
    val modeCrossingBorder: Option[SummaryList.Row]        = checkYourAnswersHelper.modeCrossingBorder(transportModeList)
    val modeAtBorder: Option[SummaryList.Row]              = checkYourAnswersHelper.modeAtBorder(transportModeList)
    val addNationalityAtDeparture: Option[SummaryList.Row] = checkYourAnswersHelper.addNationalityAtDeparture(inlandModeCode)
    val nationalityAtDeparture: Option[SummaryList.Row]    = checkYourAnswersHelper.nationalityAtDeparture(countryList, inlandModeCode)
    val nationalityCrossingBorder: Option[SummaryList.Row] = checkYourAnswersHelper.nationalityCrossingBorder(countryList)
    val addIdAtDeparture: Option[SummaryList.Row]          = checkYourAnswersHelper.addIdAtDeparture(inlandModeCode)
    val idAtDeparture: Option[SummaryList.Row]             = checkYourAnswersHelper.idAtDeparture(inlandModeCode)
    val changeAtBorder: Option[SummaryList.Row]            = checkYourAnswersHelper.changeAtBorder
    val idCrossingBorder: Option[SummaryList.Row]          = checkYourAnswersHelper.idCrossingBorder

    TransportDetailsCheckYourAnswersViewModel(
      Seq(
        Section(
          Seq(
            inlandMode,
            addIdAtDeparture,
            idAtDeparture,
            addNationalityAtDeparture,
            nationalityAtDeparture,
            changeAtBorder,
            modeAtBorder,
            modeCrossingBorder,
            idCrossingBorder,
            nationalityCrossingBorder
          ).flatten
        )
      )
    )
  }
}
