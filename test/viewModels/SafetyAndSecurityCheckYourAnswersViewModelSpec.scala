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
import generators.Generators
import models.reference.{CircumstanceIndicator, Country, CountryCode}
import models.{CircumstanceIndicatorList, CommonAddress, CountryList}
import org.scalacheck.Arbitrary.arbitrary
import pages.CommonAddressPage
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
    val address = arbitrary[CommonAddress].sample.value
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
      .set(CommonAddressPage("safetyAndSecurityConsignorAddress"), address)
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

  val countryList                = new CountryList(Seq(Country(CountryCode("FR"), "France")))
  val circumstanceIndicatorsList = CircumstanceIndicatorList(Seq(CircumstanceIndicator("C", "Road mode of transport")))
  // scalastyle:on

  private val data: Seq[Section] = SafetyAndSecurityCheckYourAnswersViewModel(setSafetyAndSecurityCarrier, countryList, circumstanceIndicatorsList)

  "SafetyAndSecurityCheckYourAnswersViewModel" - {

    "must display the correct total number of sections" in {
      data.length mustEqual 5
    }

    "must display the correct total number of rows for summary section" in {
      data.head.rows.length mustEqual 11
    }

    "must display the correct total number of rows for Country of Routing section" in {
      data(1).sectionTitle.get mustBe msg"safetyAndSecurity.checkYourAnswersLabel.countriesOfRouting"
      data(1).rows.length mustEqual 1
    }

    "must display the correct total number of rows for Security Trader Consignor" in {
      data(2).sectionTitle.get mustBe msg"safetyAndSecurity.checkYourAnswersLabel.securityTraderDetails"
      data(2).sectionSubTitle.get mustBe msg"safetyAndSecurity.checkYourAnswersLabel.subTitle.securityConsignor"
      data(2).rows.length mustEqual 4
    }

    "must display the correct total number of rows for Security Trader Consignee" in {
      data(3).sectionSubTitle.get mustBe msg"safetyAndSecurity.checkYourAnswersLabel.subTitle.securityConsignee"
      data(3).rows.length mustEqual 3
    }

    "must display the correct total number of rows for Security Trader Carrier" in {
      data(4).sectionSubTitle.get mustBe msg"safetyAndSecurity.checkYourAnswersLabel.subTitle.carrier"
      data(4).rows.length mustEqual 3
    }
  }

}
