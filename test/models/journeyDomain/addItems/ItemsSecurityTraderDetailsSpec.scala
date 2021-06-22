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
import models.reference.{Country, CountryCode}
import models.{CommonAddress, EoriNumber, Index, UserAnswers}
import org.scalacheck.Gen
import org.scalatest.TryValues
import pages.{AddSecurityDetailsPage, QuestionPage}
import pages.addItems.securityDetails.{AddDangerousGoodsCodePage, CommercialReferenceNumberPage, DangerousGoodsCodePage, TransportChargesPage}
import pages.addItems.traderSecurityDetails._
import pages.safetyAndSecurity.{AddSafetyAndSecurityConsigneePage, AddSafetyAndSecurityConsignorPage, _}

class ItemsSecurityTraderDetailsSpec extends SpecBase with GeneratorSpec with TryValues with JourneyModelGenerators {

  private val itemSecurityTraderDetailsUa = emptyUserAnswers
    .unsafeSetVal(AddSecurityDetailsPage)(true)
    .unsafeSetVal(AddCommercialReferenceNumberAllItemsPage)(true)
    .unsafeSetVal(AddDangerousGoodsCodePage(index))(false)
    .unsafeSetVal(AddTransportChargesPaymentMethodPage)(true)
    .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(true)
    .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(true)

  "ItemsSecurityDetails" - {

    "can be parsed from UserAnswers" - {

      "when add security details is true and all mandatory answers are defined" in {

        val expectedResult = ItemsSecurityTraderDetails(None, None, None, None, None)

        val result = ItemsSecurityTraderDetails.parser(index).run(itemSecurityTraderDetailsUa).right.value.value

        result mustBe expectedResult
      }

      "when add security details is true and all optional answers are defined without consignor and consignee" in {

        val expectedResult = ItemsSecurityTraderDetails(
          Some("methodOfPayment"),
          Some("commercialReferenceNumber"),
          Some("dangerousGoodsCode"),
          None,
          None
        )

        val userAnswers = itemSecurityTraderDetailsUa
          .unsafeSetVal(AddTransportChargesPaymentMethodPage)(false)
          .unsafeSetVal(TransportChargesPage(index))("methodOfPayment")
          .unsafeSetVal(AddCommercialReferenceNumberAllItemsPage)(false)
          .unsafeSetVal(CommercialReferenceNumberPage(index))("commercialReferenceNumber")
          .unsafeSetVal(AddDangerousGoodsCodePage(index))(true)
          .unsafeSetVal(DangerousGoodsCodePage(index))("dangerousGoodsCode")
          .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(true)
          .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(true)

        val result = ItemsSecurityTraderDetails.parser(index).run(userAnswers).right.value.value

        result mustBe expectedResult
      }

      "when add security details is true and all mandatory answers are defined with consignor and consignee" in {

        val consignorAddress  = Address("1", "2", "3", Some(Country(CountryCode("ZZ"), "")))
        val expectedConsignor = SecurityPersonalInformation("testName", consignorAddress)
        val expectedConsignee = SecurityTraderEori(EoriNumber("testEori"))

        val userAnswers = itemSecurityTraderDetailsUa
          .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(false)
          .unsafeSetVal(AddSecurityConsigneesEoriPage(index))(true)
          .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
          .unsafeSetVal(CircumstanceIndicatorPage)("E")
          .unsafeSetVal(SecurityConsigneeEoriPage(index))("testEori")
          .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(false)
          .unsafeSetVal(AddSecurityConsignorsEoriPage(index))(false)
          .unsafeSetVal(SecurityConsignorNamePage(index))("testName")
          .unsafeSetVal(SecurityConsignorAddressPage(index))(CommonAddress("1", "2", "3", Country(CountryCode("ZZ"), "")))

        val expectedResult = ItemsSecurityTraderDetails(None, None, None, Some(expectedConsignor), Some(expectedConsignee))

        val result = ItemsSecurityTraderDetails.parser(index).run(userAnswers).right.value.value

        result mustBe expectedResult
      }

      "when add security details is false" in {
        val userAnswers = emptyUserAnswers.unsafeSetVal(AddSecurityDetailsPage)(false)

        val result = ItemsSecurityTraderDetails.parser(index).run(userAnswers).right.value

        result mustBe None
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when a mandatory page is missing" in {

        val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
          AddSecurityDetailsPage,
          AddDangerousGoodsCodePage(index),
          AddTransportChargesPaymentMethodPage,
          AddSafetyAndSecurityConsignorPage,
          AddSafetyAndSecurityConsigneePage
        )

        forAll(mandatoryPages) {
          mandatoryPage =>
            val userAnswers = itemSecurityTraderDetailsUa
              .unsafeRemove(mandatoryPage)

            val result = ItemsSecurityTraderDetails.parser(index).run(userAnswers).left.value

            result.page mustBe mandatoryPage
        }
      }

      "when AddCommercialReferenceNumberAllItemsPage is not true and CommercialReferenceNumberPage is not defined" in {

        val userAnswers = itemSecurityTraderDetailsUa
          .unsafeSetVal(AddCommercialReferenceNumberAllItemsPage)(false)
          .unsafeRemove(CommercialReferenceNumberPage(index))

        val result = ItemsSecurityTraderDetails.parser(index).run(userAnswers).left.value

        result.page mustBe CommercialReferenceNumberPage(index)
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
        case Some(SecurityPersonalInformation(_, address)) => Address.prismAddressToCommonAddress.getOption(address).get
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
        case Some(SecurityPersonalInformation(_, address)) => Address.prismAddressToCommonAddress.getOption(address).get
      })

}
