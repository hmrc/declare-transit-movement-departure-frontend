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

import cats.implicits._
import models.domain.Address
import models.journeyDomain.UserAnswersReader
import models.{EoriNumber, Index}
import pages.addItems.securityDetails.{AddDangerousGoodsCodePage, CommercialReferenceNumberPage, DangerousGoodsCodePage, TransportChargesPage}
import pages.addItems.traderSecurityDetails._
import pages.safetyAndSecurity._
import models.journeyDomain._

final case class ItemsSecurityTraderDetails(
  methodOfPayment: Option[String],
  commercialReferenceNumber: Option[String],
  dangerousGoodsCode: Option[String],
  consignor: Option[SecurityTraderDetails],
  consignee: Option[SecurityTraderDetails]
)

object ItemsSecurityTraderDetails {

  def parser(index: Index): UserAnswersReader[ItemsSecurityTraderDetails] =
    (
      methodOfPaymentPage(index),
      commercialReferenceNumberPage(index),
      dangerousGoodsCodePage(index),
      consignorDetails(index),
      consigneeDetails(index)
    ).tupled.map((ItemsSecurityTraderDetails.apply _).tupled)

  private def consignorDetails(index: Index): UserAnswersReader[Option[SecurityTraderDetails]] = {

    val useEori =
      SecurityConsignorEoriPage(index).reader.map(
        eori => SecurityTraderDetails(EoriNumber(eori))
      )

    val useNameAndAddress =
      (
        SecurityConsignorNamePage(index).reader,
        SecurityConsignorAddressPage(index).reader
      ).tupled
        .map {
          case (name, consignorAddress) =>
            val address = Address.prismAddressToConsignorAddress(consignorAddress)
            SecurityTraderDetails(name, address)
        }

    val isEoriKnown =
      AddSecurityConsignorsEoriPage(index).reader.flatMap(
        isEoriKnown => if (isEoriKnown) useEori else useNameAndAddress
      )

    // TODO add matcher
    AddSafetyAndSecurityConsignorPage.reader
      .flatMap {
        _ =>
          isEoriKnown.map(_.some)
      }
  }

  private def consigneeDetails(index: Index): UserAnswersReader[Option[SecurityTraderDetails]] = {

    val useEori = SecurityConsigneeEoriPage(index).reader.map(
      eori => SecurityTraderDetails(EoriNumber(eori))
    )

    val useNameAndAddress =
      (
        SecurityConsigneeNamePage(index).reader,
        SecurityConsigneeAddressPage(index).reader
      ).tupled
        .map {
          case (name, consigneeAddress) =>
            val address = Address.prismAddressToConsigneeAddress(consigneeAddress)
            SecurityTraderDetails(name, address)
        }

    val isEoriKnown = AddSecurityConsigneesEoriPage(index).reader.flatMap(
      isEoriKnown => if (isEoriKnown) useEori else useNameAndAddress
    )

    // TODO add matcher
    AddSafetyAndSecurityConsigneePage.reader
      .flatMap {
        _ =>
          isEoriKnown.map(_.some)
      }
  }

  private def methodOfPaymentPage(index: Index): UserAnswersReader[Option[String]] =
    AddTransportChargesPaymentMethodPage.filterOptionalDependent(_ == false) {
      TransportChargesPage(index).reader
    }

  private def commercialReferenceNumberPage(index: Index): UserAnswersReader[Option[String]] =
    AddCommercialReferenceNumberAllItemsPage.optionalReader
      .flatMap {
        case Some(true) => none[String].pure[UserAnswersReader]
        case _          => CommercialReferenceNumberPage(index).reader.map(Some(_))
      }

  private def dangerousGoodsCodePage(index: Index): UserAnswersReader[Option[String]] =
    AddDangerousGoodsCodePage(index).filterOptionalDependent(identity) {
      DangerousGoodsCodePage(index).reader
    }
}
