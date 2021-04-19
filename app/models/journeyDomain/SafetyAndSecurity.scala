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

package models.journeyDomain

import cats.data._
import cats.implicits._
import models.domain.Address
import models.journeyDomain.Itinerary.readItineraries
import models.journeyDomain.SafetyAndSecurity.SecurityTraderDetails
import models.{EoriNumber, UserAnswers}
import pages.ModeAtBorderPage
import pages.safetyAndSecurity._

case class SafetyAndSecurity(
  circumstanceIndicator: Option[String],
  paymentMethod: Option[String],
  commercialReferenceNumber: Option[String],
  conveyanceReferenceNumber: Option[String],
  placeOfUnloading: Option[String],
  consignor: Option[SecurityTraderDetails],
  consignee: Option[SecurityTraderDetails],
  carrier: Option[SecurityTraderDetails],
  itineraryList: NonEmptyList[Itinerary]
)

object SafetyAndSecurity {

  implicit val parser: UserAnswersReader[SafetyAndSecurity] =
    (
      addCircumstanceIndicator,
      paymentMethod,
      commercialReferenceNumber,
      conveyanceReferenceNumber,
      placeOfUnloading,
      consignorDetails,
      consigneeDetails,
      carrierDetails,
      readItineraries
    ).tupled.map((SafetyAndSecurity.apply _).tupled)

  sealed trait SecurityTraderDetails

  object SecurityTraderDetails {
    def apply(eori: EoriNumber): SecurityTraderDetails               = TraderEori(eori)
    def apply(name: String, address: Address): SecurityTraderDetails = PersonalInformation(name, address)
  }

  final case class PersonalInformation(name: String, address: Address) extends SecurityTraderDetails
  final case class TraderEori(eori: EoriNumber) extends SecurityTraderDetails

  private def addCircumstanceIndicator: UserAnswersReader[Option[String]] =
    AddCircumstanceIndicatorPage.filterOptionalDependent(identity) {
      CircumstanceIndicatorPage.reader
    }

  private def paymentMethod: UserAnswersReader[Option[String]] =
    AddTransportChargesPaymentMethodPage.filterOptionalDependent(identity) {
      TransportChargesPaymentMethodPage.reader
    }

  private def commercialReferenceNumber: UserAnswersReader[Option[String]] =
    (AddCommercialReferenceNumberPage.reader, AddCommercialReferenceNumberAllItemsPage.optionalReader).tupled.flatMap {
      case (true, Some(true)) => CommercialReferenceNumberAllItemsPage.optionalReader
      case _                  => none[String].pure[UserAnswersReader]
    }

  private def conveyanceReferenceNumber: UserAnswersReader[Option[String]] =
    ModeAtBorderPage.optionalReader.flatMap {
      case Some("4") | Some("40") => ConveyanceReferenceNumberPage.reader.map(Some(_))
      case _ =>
        AddConveyanceReferenceNumberPage.filterOptionalDependent(identity) {
          ConveyanceReferenceNumberPage.reader
        }
    }

  private def placeOfUnloading: UserAnswersReader[Option[String]] =
    addCircumstanceIndicator.flatMap {
      case Some("E") =>
        AddPlaceOfUnloadingCodePage.filterMandatoryDependent(identity) {
          PlaceOfUnloadingCodePage.optionalReader
        }
      case _ =>
        PlaceOfUnloadingCodePage.reader.map(Some(_))
    }

  private def consignorDetails: UserAnswersReader[Option[SecurityTraderDetails]] = {

    val useEori: ReaderT[EitherType, UserAnswers, SecurityTraderDetails] =
      SafetyAndSecurityConsignorEoriPage.reader.map(
        eori => SecurityTraderDetails(EoriNumber(eori))
      )

    val useAddress =
      (
        SafetyAndSecurityConsignorNamePage.reader,
        SafetyAndSecurityConsignorAddressPage.reader
      ).tupled
        .map {
          case (name, consignorAddress) =>
            val address = Address.prismAddressToConsignorAddress(consignorAddress)

            SecurityTraderDetails(name, address)
        }

    val isEoriKnown: ReaderT[EitherType, UserAnswers, SecurityTraderDetails] =
      AddSafetyAndSecurityConsignorEoriPage.reader.flatMap(
        isEoriKnown => if (isEoriKnown) useEori else useAddress
      )

    AddSafetyAndSecurityConsignorPage.filterOptionalDependent(identity) {
      isEoriKnown
    }
  }

  private def consigneeDetails: UserAnswersReader[Option[SecurityTraderDetails]] = {

    val useEori: ReaderT[EitherType, UserAnswers, SecurityTraderDetails] =
      SafetyAndSecurityConsigneeEoriPage.reader.map(
        eori => SecurityTraderDetails(EoriNumber(eori))
      )

    val useAddress =
      (
        SafetyAndSecurityConsigneeNamePage.reader,
        SafetyAndSecurityConsigneeAddressPage.reader
      ).tupled
        .map {
          case (name, consigneeAddress) =>
            val address = Address.prismAddressToConsigneeAddress(consigneeAddress)

            SecurityTraderDetails(name, address)
        }

    val isEoriKnown: ReaderT[EitherType, UserAnswers, SecurityTraderDetails] =
      AddSafetyAndSecurityConsigneeEoriPage.reader.flatMap(
        isEoriKnown => if (isEoriKnown) useEori else useAddress
      )

    AddSafetyAndSecurityConsigneePage.filterOptionalDependent(identity) {
      isEoriKnown
    }
  }

  private def carrierDetails: UserAnswersReader[Option[SecurityTraderDetails]] = {

    val useEori: ReaderT[EitherType, UserAnswers, SecurityTraderDetails] =
      CarrierEoriPage.reader.map(
        eori => SecurityTraderDetails(EoriNumber(eori))
      )

    val useAddress =
      (
        CarrierNamePage.reader,
        CarrierAddressPage.reader
      ).tupled
        .map {
          case (name, carrierAddress) =>
            val address = Address.prismAddressToCarrierAddress(carrierAddress)

            SecurityTraderDetails(name, address)
        }

    val isEoriKnown: ReaderT[EitherType, UserAnswers, SecurityTraderDetails] =
      AddCarrierEoriPage.reader.flatMap(
        isEoriKnown => if (isEoriKnown) useEori else useAddress
      )

    AddCarrierPage.filterOptionalDependent(identity) {
      isEoriKnown
    }
  }
}
