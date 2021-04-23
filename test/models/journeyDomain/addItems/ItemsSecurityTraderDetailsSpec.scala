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
import models.{Index, UserAnswers}
import org.scalatest.TryValues
import pages.addItems.securityDetails.{AddDangerousGoodsCodePage, CommercialReferenceNumberPage, DangerousGoodsCodePage, TransportChargesPage}
import pages.addItems.traderSecurityDetails._
import pages.safetyAndSecurity.{AddSafetyAndSecurityConsigneePage, AddSafetyAndSecurityConsignorPage, _}

class ItemsSecurityTraderDetailsSpec extends SpecBase with GeneratorSpec with TryValues with JourneyModelGenerators {

  "ItemsSecurityTraderDetails can be parsed within user answers" - {
    "when the minimal user answers has been answered" in {

      forAll(arb[UserAnswers], arb[ItemsSecurityTraderDetails]) {
        (baseUserAnswers, itemsSecurityTraderDetails) =>
          val userAnswers = ItemsSecurityTraderDetailsSpec
            .setItemsSecurityTraderDetails(itemsSecurityTraderDetails, index)(baseUserAnswers)
            .unsafeSetVal(AddTransportChargesPaymentMethodPage)(itemsSecurityTraderDetails.methodOfPayment.isEmpty)
            .unsafeSetVal(AddCommercialReferenceNumberAllItemsPage)(itemsSecurityTraderDetails.commercialReferenceNumber.isEmpty)
            .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(itemsSecurityTraderDetails.consignor.isDefined)
            .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(itemsSecurityTraderDetails.consignee.isDefined)

          val result: ItemsSecurityTraderDetails = ItemsSecurityTraderDetails.parser(index).run(userAnswers).right.value

          result mustBe itemsSecurityTraderDetails
      }
    }

    "when the minimal user answers has been answered and AddCommercialReferenceNumberAllItems is not answered" in {

      forAll(arb[UserAnswers], arb[ItemsSecurityTraderDetails]) {
        (baseUserAnswers, itemsSecurityTraderDetails) =>
          val userAnswers = ItemsSecurityTraderDetailsSpec
            .setItemsSecurityTraderDetails(itemsSecurityTraderDetails, index)(baseUserAnswers)
            .unsafeSetVal(AddTransportChargesPaymentMethodPage)(itemsSecurityTraderDetails.methodOfPayment.isEmpty)
            .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(itemsSecurityTraderDetails.consignor.isDefined)
            .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(itemsSecurityTraderDetails.consignee.isDefined)
            .unsafeRemove(AddCommercialReferenceNumberAllItemsPage)

          val result: ItemsSecurityTraderDetails = ItemsSecurityTraderDetails.parser(index).run(userAnswers).right.value

          result mustBe itemsSecurityTraderDetails
      }
    }
  }

  "ItemsSecurityDetails cannot be parsed within user answers" ignore {
    "when the safety and security consignor page cannot be read" in {

      forAll(arb[UserAnswers], arb[ItemsSecurityTraderDetails]) {
        (baseUserAnswers, itemsSecurityTraderDetails) =>
          val userAnswers = ItemsSecurityTraderDetailsSpec
            .setItemsSecurityTraderDetails(itemsSecurityTraderDetails, index)(baseUserAnswers)
            .unsafeSetVal(AddTransportChargesPaymentMethodPage)(itemsSecurityTraderDetails.methodOfPayment.isEmpty)
            .unsafeSetVal(AddCommercialReferenceNumberAllItemsPage)(itemsSecurityTraderDetails.commercialReferenceNumber.isEmpty)
            .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(itemsSecurityTraderDetails.consignee.isDefined)
            .unsafeRemove(AddSafetyAndSecurityConsignorPage)

          val result: ItemsSecurityTraderDetails = ItemsSecurityTraderDetails.parser(index).run(userAnswers).right.value

          result.consignor mustBe None
      }
    }

    "when the safety and security consignee page cannot be read" in {

      forAll(arb[UserAnswers], arb[ItemsSecurityTraderDetails]) {
        (baseUserAnswers, itemsSecurityTraderDetails) =>
          val userAnswers = ItemsSecurityTraderDetailsSpec
            .setItemsSecurityTraderDetails(itemsSecurityTraderDetails, index)(baseUserAnswers)
            .unsafeSetVal(AddTransportChargesPaymentMethodPage)(itemsSecurityTraderDetails.methodOfPayment.isEmpty)
            .unsafeSetVal(AddCommercialReferenceNumberAllItemsPage)(itemsSecurityTraderDetails.commercialReferenceNumber.isEmpty)
            .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(itemsSecurityTraderDetails.consignor.isDefined)
            .unsafeRemove(AddSafetyAndSecurityConsigneePage)

          val result: ItemsSecurityTraderDetails = ItemsSecurityTraderDetails.parser(index).run(userAnswers).right.value

          result.consignee mustBe None
      }
    }
  }
}

object ItemsSecurityTraderDetailsSpec {

  def setItemsSecurityTraderDetails(itemsSecurityTraderDetails: ItemsSecurityTraderDetails, index: Index)(startUserAnswers: UserAnswers): UserAnswers =
    startUserAnswers
    // Set method of payment
      .unsafeSetOpt(TransportChargesPage(index))(itemsSecurityTraderDetails.methodOfPayment)

      // Set commerical reference number
      .unsafeSetOpt(CommercialReferenceNumberPage(index))(itemsSecurityTraderDetails.commercialReferenceNumber)

      // Set Dangerous goods
      .unsafeSetVal(AddDangerousGoodsCodePage(index))(itemsSecurityTraderDetails.dangerousGoodsCode.isDefined)
      .unsafeSetOpt(DangerousGoodsCodePage(index))(itemsSecurityTraderDetails.dangerousGoodsCode)

      // Set Consignor
      .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(false)
      .unsafeSetOpt(TransportChargesPage(index))(itemsSecurityTraderDetails.methodOfPayment)
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
      .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(false) //TODO: Move out to call site
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
