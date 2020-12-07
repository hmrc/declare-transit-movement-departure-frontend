/*
 * Copyright 2020 HM Revenue & Customs
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
import generators.Generators
import models.ConsignorAddress
import models.reference.CountryCode
import org.scalacheck.Arbitrary.arbitrary
import pages.safetyAndSecurity._
import uk.gov.hmrc.viewmodels.MessageInterpolators
import viewModels.sections.Section

class SafetyAndSecurityCheckYourAnswersViewModelSpec extends SpecBase with Generators {

  // scalastyle:off
  private val setSafetyAndSecuritySummary = emptyUserAnswers
    .set(AddCircumstanceIndicatorPage, true)
    .success
    .value
    .set(CircumstanceIndicatorPage, "answer")
    .success
    .value
    .set(AddTransportChargesPaymentMethodPage, true)
    .success
    .value
    .set(TransportChargesPaymentMethodPage, "answer")
    .success
    .value
    .set(AddCommercialReferenceNumberPage, true)
    .success
    .value
    .set(AddCommercialReferenceNumberAllItemsPage, true)
    .success
    .value
    .set(CommercialReferenceNumberAllItemsPage, "answer")
    .success
    .value
    .set(AddConveyanceReferenceNumberPage, true)
    .success
    .value
    .set(ConveyanceReferenceNumberPage, "answer")
    .success
    .value
    .set(AddPlaceOfUnloadingCodePage, true)
    .success
    .value
    .set(PlaceOfUnloadingCodePage, "answer")
    .success
    .value

  private val setSafetyAndSecurityCountriesOfRouting = setSafetyAndSecuritySummary
    .set(CountryOfRoutingPage(index), CountryCode("GB"))
    .success
    .value

  private val setSafetyAndSecurityConsignor = {
    val address = arbitrary[ConsignorAddress].sample.value
    setSafetyAndSecurityCountriesOfRouting
      .set(AddSafetyAndSecurityConsignorPage, true)
      .success
      .value
      .set(AddSafetyAndSecurityConsignorEoriPage, false)
      .success
      .value
      .set(SafetyAndSecurityConsignorNamePage, "answer")
      .success
      .value
      .set(SafetyAndSecurityConsignorAddressPage, address)
      .success
      .value
  }

  private val setSafetyAndSecurityConsignee = setSafetyAndSecurityConsignor
    .set(AddSafetyAndSecurityConsigneePage, true)
    .success
    .value
    .set(AddSafetyAndSecurityConsigneeEoriPage, true)
    .success
    .value
    .set(SafetyAndSecurityConsigneeEoriPage, "answer")
    .success
    .value

  private val setSafetyAndSecurityCarrier = setSafetyAndSecurityConsignee
    .set(AddCarrierPage, true)
    .success
    .value
    .set(AddCarrierEoriPage, true)
    .success
    .value
    .set(CarrierEoriPage, "answer")
    .success
    .value

  // scalastyle:on

  private val data: Seq[Section] = SafetyAndSecurityCheckYourAnswersViewModel(setSafetyAndSecurityCarrier)

  "SafetyAndSecurityCheckYourAnswersViewModel" - {

    "must display the correct total number of sections" in {
      data.length mustEqual 3
    }

    "must display the correct total number of rows for summary section" in {
      data.head.rows.length mustEqual 11
    }

    "must display the correct total number of rows for Country of Routing section" in {
      data(1).sectionTitle.get mustBe msg"safetyAndSecurity.checkYourAnswersLabel.countriesOfRouting"
      data(1).rows.length mustEqual 1
    }

    "must display the correct total number of rows for Security Trader Details section" in {
      data(2).sectionTitle.get mustBe msg"safetyAndSecurity.checkYourAnswersLabel.securityTraderDetails"
      data(2).rows.length mustEqual 10
    }
  }

}
