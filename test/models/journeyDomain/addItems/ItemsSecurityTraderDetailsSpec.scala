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

package models.journeyDomain.addItems

import base.{GeneratorSpec, SpecBase}
import generators.JourneyModelGenerators
import models.domain.Address
import models.journeyDomain.PackagesSpec.UserAnswersSpecHelperOps
import models.journeyDomain.{EitherType, UserAnswersReader}
import models.reference.{Country, CountryCode}
import models.{ConsignorAddress, EoriNumber, Index, UserAnswers}
import org.scalatest.TryValues
import pages.AddSecurityDetailsPage
import pages.addItems.securityDetails.{AddDangerousGoodsCodePage, CommercialReferenceNumberPage, DangerousGoodsCodePage, TransportChargesPage}
import pages.addItems.traderSecurityDetails._
import pages.safetyAndSecurity.{AddSafetyAndSecurityConsigneePage, AddSafetyAndSecurityConsignorPage, _}

class ItemsSecurityTraderDetailsSpec extends SpecBase with GeneratorSpec with TryValues with JourneyModelGenerators {

  "ItemsSecurityDetails" - {
    "When user selects 'No' to add Safety and Security then item security trader details is not defined" in {
      val userAnswers = emptyUserAnswers
        .unsafeSetVal(AddSecurityDetailsPage)(false)

      val result = ItemsSecurityTraderDetails.parser(index).run(userAnswers).right.value

      result mustBe None
    }

    "When user selects 'Yes' to Add Safety and Security" - {

      "then item security details will be defined by user answers with no optional data" in {
        val userAnswers = emptyUserAnswers
          .unsafeSetVal(AddSecurityDetailsPage)(true)
          .unsafeSetVal(AddTransportChargesPaymentMethodPage)(false)
          .unsafeSetVal(TransportChargesPage(index))("4.00")
          .unsafeSetVal(CommercialReferenceNumberPage(index))("111111")
          .unsafeSetVal(AddDangerousGoodsCodePage(index))(false)
          .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(true)
          .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(true)

        val result = ItemsSecurityTraderDetails.parser(index).run(userAnswers).right.value

        val expected =
          ItemsSecurityTraderDetails(Some("4.00"), Some("111111"), None, None, None)
        result.value mustBe expected
      }

      "then item security details will be defined by user answers with optional data" in {
        val userAnswers = emptyUserAnswers
          .unsafeSetVal(AddSecurityDetailsPage)(true)
          .unsafeSetVal(AddTransportChargesPaymentMethodPage)(false)
          //      .unsafeSetVal(TransportChargesPaymentMethodPage)("Payment in cash")
          .unsafeSetVal(TransportChargesPage(index))("4.00")
          .unsafeSetVal(CommercialReferenceNumberPage(index))("111111")
          .unsafeSetVal(AddDangerousGoodsCodePage(index))(true)
          .unsafeSetVal(DangerousGoodsCodePage(index))("4")
          .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(false)
          .unsafeSetVal(AddSecurityConsignorsEoriPage(index))(false)
          .unsafeSetVal(SecurityConsignorNamePage(index))("Bob")
          .unsafeSetVal(SecurityConsignorAddressPage(index))(ConsignorAddress("First line", "Second line", "Postcode", Country(CountryCode("FR"), "France")))
          .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(false)
          .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
          .unsafeSetVal(CircumstanceIndicatorPage)("A")
          .unsafeSetVal(AddSecurityConsigneesEoriPage(index))(true)
          .unsafeSetVal(SecurityConsigneeEoriPage(index))("GB123456")

        println("*************" + ItemsSecurityTraderDetails.parser(index).run(userAnswers))
        val result = ItemsSecurityTraderDetails.parser(index).run(userAnswers).right.value

        val expected =
          ItemsSecurityTraderDetails(
            Some("4.00"),
            Some("111111"),
            Some("4"),
            Some(SecurityPersonalInformation("Bob", Address("First line", "Second line", "Postcode", Some(Country(CountryCode("FR"), "France"))))),
            Some(SecurityTraderEori(EoriNumber("GB123456")))
          )
        result.value mustBe expected
      }
    }
  }
}

object ItemsSecurityTraderDetailsSpec {

  def setItemsSecurityTraderDetails(itemsSecurityTraderDetails: ItemsSecurityTraderDetails, index: Index)(startUserAnswers: UserAnswers): UserAnswers =
    startUserAnswers
    // Set method of payment
      .unsafeSetOpt(TransportChargesPage(index))(itemsSecurityTraderDetails.methodOfPayment)

      // Set commercial reference number
      .unsafeSetOpt(CommercialReferenceNumberPage(index))(itemsSecurityTraderDetails.commercialReferenceNumber)

      // Set Dangerous goods
      .unsafeSetVal(AddDangerousGoodsCodePage(index))(itemsSecurityTraderDetails.dangerousGoodsCode.contains(true))
      .unsafeSetOpt(DangerousGoodsCodePage(index))(itemsSecurityTraderDetails.dangerousGoodsCode)

      // Set Consignor
      .unsafeSetPFn(AddSecurityConsignorsEoriPage(index))(itemsSecurityTraderDetails.consignor)({
        case Some(SecurityTraderEori(_)) => true
        case Some(_)                     => false
      })
      .unsafeSetPFn(SecurityConsignorEoriPage(index))(itemsSecurityTraderDetails.consignor)({
        case Some(SecurityTraderEori(eori)) => eori.value
      })
      .unsafeSetPFn(SecurityConsignorNamePage(index))(itemsSecurityTraderDetails.consignor)({
        case Some(SecurityPersonalInformation(name, _)) => name
      })
      .unsafeSetPFn(SecurityConsignorAddressPage(index))(itemsSecurityTraderDetails.consignor)({
        case Some(SecurityPersonalInformation(_, address)) => Address.prismAddressToConsignorAddress.getOption(address).get
      })

      //     Set Consignee
      .unsafeSetPFn(AddSecurityConsigneesEoriPage(index))(itemsSecurityTraderDetails.consignee)({
        case Some(SecurityTraderEori(_)) => true
        case Some(_)                     => false
      })
      .unsafeSetPFn(SecurityConsigneeEoriPage(index))(itemsSecurityTraderDetails.consignee)({
        case Some(SecurityTraderEori(eori)) => eori.value
      })
      .unsafeSetPFn(SecurityConsigneeNamePage(index))(itemsSecurityTraderDetails.consignee)({
        case Some(SecurityPersonalInformation(name, _)) => name
      })
      .unsafeSetPFn(SecurityConsigneeAddressPage(index))(itemsSecurityTraderDetails.consignee)({
        case Some(SecurityPersonalInformation(_, address)) => Address.prismAddressToConsigneeAddress.getOption(address).get
      })

}
