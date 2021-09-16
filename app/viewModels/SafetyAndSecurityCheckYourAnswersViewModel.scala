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

import derivable.DeriveNumberOfCountryOfRouting
import models.{CircumstanceIndicatorList, CountryList, Index, UserAnswers}
import uk.gov.hmrc.viewmodels.{MessageInterpolators, SummaryList}
import utils.SafetyAndSecurityCheckYourAnswerHelper
import viewModels.sections.Section

object SafetyAndSecurityCheckYourAnswersViewModel {

  def apply(userAnswers: UserAnswers, countries: CountryList, circumstanceIndicators: CircumstanceIndicatorList): Seq[Section] = {
    val cyah = new SafetyAndSecurityCheckYourAnswerHelper(userAnswers)

    val countriesOfRoutingRows: Seq[SummaryList.Row] = List.range(0, userAnswers.get(DeriveNumberOfCountryOfRouting).getOrElse(0)).flatMap {
      countryOfRoutingPosition =>
        cyah.countryOfRoutingRow(Index(countryOfRoutingPosition), countries)
    }

    Seq(
      Section(
        Seq(
          cyah.addCircumstanceIndicator,
          cyah.circumstanceIndicator(circumstanceIndicators),
          cyah.addTransportChargesPaymentMethod,
          cyah.transportChargesPaymentMethod,
          cyah.addCommercialReferenceNumber,
          cyah.addCommercialReferenceNumberAllItems,
          cyah.commercialReferenceNumberAllItems,
          cyah.addConveyanceReferenceNumber,
          cyah.conveyanceReferenceNumber,
          cyah.addPlaceOfUnloadingCode,
          cyah.placeOfUnloadingCode
        ).flatten
      ),
      Section(
        msg"safetyAndSecurity.checkYourAnswersLabel.countriesOfRouting",
        countriesOfRoutingRows,
        cyah.addAnotherCountryOfRouting(msg"addOrRemoveCountryOfRouting.checkYourAnswersLabel")
      ),
      Section(
        msg"safetyAndSecurity.checkYourAnswersLabel.securityTraderDetails",
        msg"safetyAndSecurity.checkYourAnswersLabel.subTitle.securityConsignor",
        Seq(
          cyah.addSafetyAndSecurityConsignor,
          cyah.addSafetyAndSecurityConsignorEori,
          cyah.safetyAndSecurityConsignorEori,
          cyah.safetyAndSecurityConsignorName,
          cyah.safetyAndSecurityConsignorAddress
        ).flatten
      ),
      Section(
        None,
        Some(msg"safetyAndSecurity.checkYourAnswersLabel.subTitle.securityConsignee"),
        Seq(
          cyah.addSafetyAndSecurityConsignee,
          cyah.addSafetyAndSecurityConsigneeEori,
          cyah.safetyAndSecurityConsigneeEori,
          cyah.safetyAndSecurityConsigneeName,
          cyah.safetyAndSecurityConsigneeAddress
        ).flatten,
        None
      ),
      Section(
        None,
        Some(msg"safetyAndSecurity.checkYourAnswersLabel.subTitle.carrier"),
        Seq(
          cyah.addCarrier,
          cyah.addCarrierEori,
          cyah.carrierEori,
          cyah.carrierName,
          cyah.carrierAddress
        ).flatten,
        None
      )
    )

  }
}
