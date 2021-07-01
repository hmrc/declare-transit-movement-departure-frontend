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
import models.journeyDomain.Itinerary.readItineraries
import models.journeyDomain.SafetyAndSecurity.SecurityTraderDetails
import models.reference.{CountryCode, CustomsOffice}
import models.{CommonAddress, EoriNumber, UserAnswers}
import pages.{ModeAtBorderPage, OfficeOfDeparturePage}
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
    def apply(eori: EoriNumber): SecurityTraderDetails = TraderEori(eori)

    def apply(name: String, address: CommonAddress): SecurityTraderDetails = PersonalInformation(name, address)
  }

  final case class PersonalInformation(name: String, address: CommonAddress) extends SecurityTraderDetails

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
        AddPlaceOfUnloadingCodePage.filterOptionalDependent(identity) {
          PlaceOfUnloadingCodePage.reader
        }
      case _ =>
        PlaceOfUnloadingCodePage.reader.map(Some(_))
    }

  private def consignorDetails: UserAnswersReader[Option[SecurityTraderDetails]] = {

    val readEori: ReaderT[EitherType, UserAnswers, SecurityTraderDetails] =
      SafetyAndSecurityConsignorEoriPage.reader
        .map(EoriNumber(_))
        .map(SecurityTraderDetails(_))

    val readAddress: ReaderT[EitherType, UserAnswers, SecurityTraderDetails] =
      (
        SafetyAndSecurityConsignorNamePage.reader,
        SafetyAndSecurityConsignorAddressPage.reader
      ).tupled
        .map {
          case (name, address) =>
            SecurityTraderDetails(name, address)
        }

    AddSafetyAndSecurityConsignorPage.filterOptionalDependent(identity) {
      AddSafetyAndSecurityConsignorEoriPage.reader.flatMap {
        case true  => readEori
        case false => readAddress
      }
    }

    AddSafetyAndSecurityConsignorPage.filterOptionalDependent(identity) {
      (AddCircumstanceIndicatorPage.reader, CircumstanceIndicatorPage.optionalReader, OfficeOfDeparturePage.reader).tupled.flatMap {
        case (true, Some("E"), CustomsOffice(_, _, CountryCode("XI"), _)) => readEori
        case _ =>
          AddSafetyAndSecurityConsignorEoriPage.reader.flatMap {
            case true  => readEori
            case false => readAddress
          }
      }
    }
  }

  private def consigneeDetails: UserAnswersReader[Option[SecurityTraderDetails]] = {

    val readEori: ReaderT[EitherType, UserAnswers, SecurityTraderDetails] =
      SafetyAndSecurityConsigneeEoriPage.reader
        .map(EoriNumber(_))
        .map(SecurityTraderDetails(_))

    val readAddress: ReaderT[EitherType, UserAnswers, SecurityTraderDetails] =
      (
        SafetyAndSecurityConsigneeNamePage.reader,
        SafetyAndSecurityConsigneeAddressPage.reader
      ).tupled
        .map {
          case (name, address) =>
            SecurityTraderDetails(name, address)
        }

    AddSafetyAndSecurityConsigneePage.filterOptionalDependent(identity) {
      (AddCircumstanceIndicatorPage.reader, CircumstanceIndicatorPage.optionalReader, OfficeOfDeparturePage.reader).tupled.flatMap {
        case (true, Some("E"), CustomsOffice(_, _, countryCode, _)) if countryCode.code != "XI" => readEori
        case _ =>
          AddSafetyAndSecurityConsigneeEoriPage.reader.flatMap {
            case true  => readEori
            case false => readAddress
          }
      }
    }
  }

  private def carrierDetails: UserAnswersReader[Option[SecurityTraderDetails]] = {

    val readEori: ReaderT[EitherType, UserAnswers, SecurityTraderDetails] =
      CarrierEoriPage.reader
        .map(EoriNumber(_))
        .map(SecurityTraderDetails(_))

    val readAddress =
      (
        CarrierNamePage.reader,
        CarrierAddressPage.reader
      ).tupled
        .map {
          case (name, address) =>
            SecurityTraderDetails(name, address)
        }

    AddCarrierPage.filterOptionalDependent(identity) {
      AddCarrierEoriPage.reader.flatMap {
        case true  => readEori
        case false => readAddress
      }
    }
  }
}
