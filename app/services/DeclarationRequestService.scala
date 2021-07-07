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

package services

import cats.data.NonEmptyList
import cats.implicits._
import logging.Logging
import models.DeclarationType.Option4
import models.domain.SealDomain
import models.journeyDomain.GoodsSummary.{GoodSummaryDetails, GoodSummaryNormalDetailsWithPreLodge, GoodSummaryNormalDetailsWithoutPreLodge, GoodSummarySimplifiedDetails}
import models.journeyDomain.ItemTraderDetails.RequiredDetails
import models.journeyDomain.RouteDetails.TransitInformation
import models.journeyDomain.SafetyAndSecurity.SecurityTraderDetails
import models.journeyDomain.TransportDetails.DetailsAtBorder.{NewDetailsAtBorder, SameDetailsAtBorder}
import models.journeyDomain.TransportDetails.{DetailsAtBorder, InlandMode, ModeCrossingBorder}
import models.journeyDomain.addItems.{ItemsSecurityTraderDetails, SecurityPersonalInformation, SecurityTraderEori}
import models.journeyDomain.traderDetails._
import models.journeyDomain.{GuaranteeDetails, ItemSection, Itinerary, JourneyDomain, Packages, UserAnswersReader, _}
import models.messages._
import models.messages.customsoffice.{CustomsOfficeDeparture, CustomsOfficeDestination, CustomsOfficeTransit}
import models.messages.goodsitem.{BulkPackage, GoodsItem, RegularPackage, UnpackedPackage, _}
import models.messages.guarantee.{Guarantee, GuaranteeReferenceWithGrn, GuaranteeReferenceWithOther}
import models.messages.header.{Header, Transport}
import models.messages.safetyAndSecurity._
import models.messages.trader.{TraderConsignor, TraderPrincipal, TraderPrincipalWithEori, TraderPrincipalWithoutEori, _}
import models.{CommonAddress, EoriNumber, UserAnswers}
import repositories.InterchangeControlReferenceIdRepository

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

trait DeclarationRequestServiceInt {
  def convert(userAnswers: UserAnswers): Future[EitherType[DeclarationRequest]]
}

@deprecated("Merge with DeclarationRequestService", "")
class DeclarationRequestService @Inject() (
  icrRepository: InterchangeControlReferenceIdRepository,
  dateTimeService: DateTimeService
)(implicit ec: ExecutionContext)
    extends DeclarationRequestServiceInt
    with Logging {

  override def convert(userAnswers: UserAnswers): Future[EitherType[DeclarationRequest]] =
    icrRepository
      .nextInterchangeControlReferenceId()
      .map {
        icrId =>
          UserAnswersReader[JourneyDomain]
            .map(journeyModelToSubmissionModel(_, icrId, dateTimeService.currentDateTime))
            .run(userAnswers)
      }

  // TODO refactor / move to seperate module for unit testing
  private def journeyModelToSubmissionModel(
    journeyDomain: JourneyDomain,
    icr: InterchangeControlReference,
    dateTimeOfPrep: LocalDateTime
  ): DeclarationRequest = {

    val JourneyDomain(
      preTaskList,
      movementDetails,
      routeDetails,
      transportDetails,
      traderDetails,
      itemDetails,
      goodsSummary,
      guarantee,
      safetyAndSecurity
    ) = journeyDomain

    def guaranteeDetails(guaranteeDetails: NonEmptyList[GuaranteeDetails]): NonEmptyList[Guarantee] =
      guaranteeDetails map {
        case GuaranteeDetails.GuaranteeReference(guaranteeType, guaranteeReferenceNumber, _, accessCode) =>
          val guaranteeReferenceWithGrn = GuaranteeReferenceWithGrn(guaranteeReferenceNumber, accessCode)
          Guarantee(guaranteeType.toString, Seq(guaranteeReferenceWithGrn))
        case GuaranteeDetails.GuaranteeOther(guaranteeType, otherReference) =>
          val guaranteeReferenceOther = GuaranteeReferenceWithOther(otherReference, None)
          Guarantee(guaranteeType.toString, Seq(guaranteeReferenceOther))
      }

    def packages(packages: NonEmptyList[Packages]): NonEmptyList[models.messages.goodsitem.Package] =
      packages.map {
        case Packages.UnpackedPackages(packageType, totalPieces, markOrNumber) =>
          UnpackedPackage(packageType.code, totalPieces, markOrNumber)
        case Packages.BulkPackages(packageType, markOrNumber) =>
          BulkPackage(packageType.code, markOrNumber)
        case Packages.OtherPackages(packageType, howManyPackagesPage, markOrNumber) =>
          RegularPackage(packageType.code, howManyPackagesPage, markOrNumber)
      }

    def goodsItems(goodsItems: NonEmptyList[ItemSection], guaranteeDetails: NonEmptyList[GuaranteeDetails]): NonEmptyList[GoodsItem] =
      goodsItems.zipWithIndex.map {
        case (itemSection, index) =>
          GoodsItem(
            itemNumber = index + 1,
            commodityCode = itemSection.itemDetails.commodityCode,
            declarationType = None, // Clarify with policy
            description = itemSection.itemDetails.itemDescription,
            grossMass = Some(BigDecimal(itemSection.itemDetails.itemTotalGrossMass)),
            netMass = itemSection.itemDetails.totalNetMass.map(BigDecimal(_)),
            countryOfDispatch = None, // Not required, defined at header level
            countryOfDestination = None, // Not required, defined at header level
            methodOfPayment = collectWhen(goodsItems.size > 1)(itemSection.itemSecurityTraderDetails.flatMap(_.methodOfPayment)),
            commercialReferenceNumber = collectWhen(goodsItems.size > 1)(itemSection.itemSecurityTraderDetails.flatMap(_.commercialReferenceNumber)),
            dangerousGoodsCode = itemSection.itemSecurityTraderDetails.flatMap(_.dangerousGoodsCode),
            previousAdministrativeReferences = previousAdministrativeReference(itemSection.previousReferences),
            producedDocuments = producedDocuments(itemSection.producedDocuments),
            specialMention = SpecialMentionConversion(itemSection.specialMentions, guaranteeDetails, index),
            traderConsignorGoodsItem = collectWhen(goodsItems.size > 1)(traderConsignor(itemSection.consignor)),
            traderConsigneeGoodsItem = collectWhen(goodsItems.size > 1)(traderConsignee(itemSection.consignee)),
            containers = containers(itemSection.containers),
            packages = packages(itemSection.packages).toList,
            sensitiveGoodsInformation = Seq.empty, // Not required, defined at security level
            collectWhen(goodsItems.size > 1)(GoodsItemSafetyAndSecurityConsignor(itemSection.itemSecurityTraderDetails)),
            collectWhen(goodsItems.size > 1)(GoodsItemSafetyAndSecurityConsignee(itemSection.itemSecurityTraderDetails))
          )
      }

    def previousAdministrativeReference(previousReferences: Option[NonEmptyList[PreviousReferences]]): Seq[PreviousAdministrativeReference] =
      previousReferences
        .map(
          _.toList.map(
            x => PreviousAdministrativeReference(x.referenceType, x.previousReference, x.extraInformation)
          )
        )
        .getOrElse(List.empty)

    def producedDocuments(producedDocument: Option[NonEmptyList[models.journeyDomain.ProducedDocument]]): Seq[goodsitem.ProducedDocument] =
      producedDocument
        .map(
          _.toList.map(
            x => goodsitem.ProducedDocument(x.documentType, Some(x.documentReference), x.extraInformation)
          )
        )
        .getOrElse(List.empty)

    def containers(containers: Option[NonEmptyList[Container]]): Seq[String] =
      containers.map(_.toList.map(_.containerNumber)).getOrElse(List.empty)

    def GoodsItemSafetyAndSecurityConsignor(itemSecurityTraderDetails: Option[ItemsSecurityTraderDetails]): Option[GoodsItemSecurityConsignor] =
      itemSecurityTraderDetails.flatMap {
        x =>
          x.consignor.map {
            case SecurityPersonalInformation(name, CommonAddress(buildingAndStreet, city, postcode, country)) =>
              ItemsSecurityConsignorWithoutEori(name, buildingAndStreet, postcode, city, country.code.code)
            case SecurityTraderEori(eori) =>
              ItemsSecurityConsignorWithEori(eori.value)
          }
      }

    def GoodsItemSafetyAndSecurityConsignee(itemSecurityTraderDetails: Option[ItemsSecurityTraderDetails]): Option[GoodsItemSecurityConsignee] =
      itemSecurityTraderDetails.flatMap {
        x =>
          x.consignee.map {
            case SecurityPersonalInformation(name, CommonAddress(buildingAndStreet, city, postcode, country)) =>
              ItemsSecurityConsigneeWithoutEori(name, buildingAndStreet, postcode, city, country.code.code)
            case SecurityTraderEori(eori) =>
              ItemsSecurityConsigneeWithEori(eori.value)
          }
      }

    def principalTrader(traderDetails: TraderDetails, preTaskListDetails: PreTaskListDetails): TraderPrincipal = {
      if(preTaskListDetails.declarationType != Option4){

        traderDetails.principalTraderDetails match {
          case PrincipalTraderPersonalInfo(name, CommonAddress(buildingAndStreet, city, postcode, country)) =>
            TraderPrincipalWithoutEori(
              name = name,
              streetAndNumber = buildingAndStreet,
              postCode = postcode,
              city = city,
              countryCode = country.code.code
            )
          case PrincipalTraderEoriInfo(traderEori) =>
            TraderPrincipalWithEori(eori = traderEori.value, None, None, None, None, None)

          case PrincipalTraderEoriPersonalInfo(eori, name, CommonAddress(buildingAndStreet, city, postcode, country)) =>
            TraderPrincipalWithEori(eori.value, Some(name), Some(buildingAndStreet), Some(postcode), Some(city), Some(country.code.code))
        }
      } else {
        traderDetails.principalTraderDetails match {
          case PrincipalTraderPersonalInfo(name, CommonAddress(buildingAndStreet, city, postcode, country)) =>
            TraderPrincipalWithPrincipalWithoutEoriTirHolderId(
              name = name,
              streetAndNumber = buildingAndStreet,
              postCode = postcode,
              city = city,
              countryCode = country.code.code,
              principalTirHolderId =
            )
          case PrincipalTraderEoriInfo(traderEori) =>
            TraderPrincipalWithEoriTirHolderId(eori = traderEori.value, None, None, None, None, None)

          case PrincipalTraderEoriPersonalInfo(eori, name, CommonAddress(buildingAndStreet, city, postcode, country)) =>
            TraderPrincipalWithEoriTirHolderId(eori.value, Some(name), Some(buildingAndStreet), Some(postcode), Some(city), Some(country.code.code))
        }
      }
    }

    def detailsAtBorderMode(detailsAtBorder: DetailsAtBorder, inlandCode: Int): String =
      detailsAtBorder match {
        case DetailsAtBorder.NewDetailsAtBorder(mode, _) => mode
        case SameDetailsAtBorder                         => inlandCode.toString
      }

    def customsOfficeTransit(transitInformation: NonEmptyList[TransitInformation]): Seq[CustomsOfficeTransit] =
      transitInformation.map {
        case TransitInformation(office, arrivalTime) => CustomsOfficeTransit(office, arrivalTime)
      }.toList

    def headerSeals(domainSeals: Seq[SealDomain]): Option[Seals] =
      if (domainSeals.nonEmpty) {
        val sealList = domainSeals.map(_.numberOrMark)
        Some(Seals(domainSeals.size, sealList))
      } else None

    def representative(movementDetails: MovementDetails): Option[Representative] =
      movementDetails.declarationForSomeoneElse match {
        case MovementDetails.DeclarationForSelf =>
          None
        case MovementDetails.DeclarationForSomeoneElse(companyName, capacity) =>
          Some(Representative(companyName, Some(capacity.toString)))
      }

    def traderConsignor(requiredDetails: Option[RequiredDetails]): Option[TraderConsignorGoodsItem] =
      requiredDetails
        .flatMap {
          case ItemTraderDetails.RequiredDetails(name, CommonAddress(addressLine1, addressLine2, postCode, country), eori) =>
            Some(TraderConsignorGoodsItem(name, addressLine1, postCode, addressLine2, country.code.code, eori.map(_.value)))
          case _ =>
            logger.error(s"traderConsignor failed to get name and address")
            None
        }

    def traderConsignee(requiredDetails: Option[RequiredDetails]): Option[TraderConsigneeGoodsItem] =
      requiredDetails
        .flatMap {
          case ItemTraderDetails.RequiredDetails(name, CommonAddress(addressLine1, addressLine2, postCode, country), eori) =>
            Some(TraderConsigneeGoodsItem(name, addressLine1, postCode, addressLine2, country.code.code, eori.map(_.value)))
          case _ =>
            logger.error(s"traderConsignee failed to get name and address")
            None
        }

    def nationalityAtDeparture(inlandMode: InlandMode): Option[String] =
      inlandMode match {
        case InlandMode.NonSpecialMode(_, nationalityAtDeparture, _) => nationalityAtDeparture.map(_.code)
        case _                                                       => None
      }

    def identityOfTransportAtDeparture(inlandMode: InlandMode): Option[String] =
      inlandMode match {
        case InlandMode.NonSpecialMode(_, _, departureId) => departureId
        case _                                            => None
      }

//cusSubPlaHEA66
    def customsSubPlace(goodsSummary: GoodsSummary): Option[String] =
      goodsSummary.goodSummaryDetails match {
        case GoodsSummary.GoodSummaryNormalDetailsWithoutPreLodge(None, customsApprovedLocation) => customsApprovedLocation
        case _                                                                                   => None
      }

//agrLocOfGooHEA39
    def agreedLocationOfGoods(goodsSummaryDetails: GoodSummaryDetails): Option[String] =
      goodsSummaryDetails match {
        case GoodSummaryNormalDetailsWithPreLodge(agreedLocationOfGoods)          => agreedLocationOfGoods
        case GoodSummaryNormalDetailsWithoutPreLodge(agreedLocationOfGoods, None) => agreedLocationOfGoods
        case _                                                                    => None
      }

//agrLocOfGooCodHEA38
    def agreedLocationOfGoodsCode(goodsSummaryDetails: GoodSummaryDetails): Option[String] =
      goodsSummaryDetails match {
        case GoodSummaryNormalDetailsWithPreLodge(_) => Some("Pre-lodge")
        case _                                       => None
      }

//autLocOfGooCodHEA41
    def goodsSummarySimplifiedDetails(goodsSummaryDetails: GoodSummaryDetails): Option[GoodSummarySimplifiedDetails] =
      goodsSummaryDetails match {
        case result: GoodSummarySimplifiedDetails => Some(result)
        case _                                    => None
      }

    def safetyAndSecurityFlag(boolFlag: Boolean): Int = if (boolFlag) 1 else 0

    def safetyAndSecurityConsignee(securityTraderDetails: Option[SecurityTraderDetails]): Option[SafetyAndSecurityConsignee] =
      securityTraderDetails
        .map {
          case SafetyAndSecurity.PersonalInformation(name, CommonAddress(addressLine1, addressLine2, postCode, country)) =>
            SafetyAndSecurityConsigneeWithoutEori(name, addressLine1, postCode, addressLine2, country.code.code)
          case SafetyAndSecurity.TraderEori(EoriNumber(eori)) =>
            SafetyAndSecurityConsigneeWithEori(eori)
        }

    def safetyAndSecurityConsignor(securityTraderDetails: Option[SecurityTraderDetails]): Option[SafetyAndSecurityConsignor] =
      securityTraderDetails
        .map {
          case SafetyAndSecurity.PersonalInformation(name, CommonAddress(addressLine1, addressLine2, postCode, country)) =>
            SafetyAndSecurityConsignorWithoutEori(name, addressLine1, postCode, addressLine2, country.code.code)
          case SafetyAndSecurity.TraderEori(EoriNumber(eori)) =>
            SafetyAndSecurityConsignorWithEori(eori)
        }

    def carrier(securityTraderDetails: Option[SecurityTraderDetails]): Option[SafetyAndSecurityCarrier] =
      securityTraderDetails
        .map {
          case SafetyAndSecurity.PersonalInformation(name, CommonAddress(addressLine1, addressLine2, postCode, country)) =>
            SafetyAndSecurityCarrierWithoutEori(name, addressLine1, postCode, addressLine2, country.code.code)
          case SafetyAndSecurity.TraderEori(EoriNumber(eori)) =>
            SafetyAndSecurityCarrierWithEori(eori)
        }

    def identityOfTransportAtCrossing(detailsAtBorder: DetailsAtBorder, inlandMode: InlandMode): Option[String] =
      detailsAtBorder match {
        case newDetailsAtBorder: NewDetailsAtBorder =>
          newDetailsAtBorder.modeCrossingBorder match {
            case ModeCrossingBorder.ModeExemptNationality(_)                           => None
            case ModeCrossingBorder.ModeWithNationality(_, _, idOfTrasnportAtCrossing) => Some(idOfTrasnportAtCrossing)
          }
        case DetailsAtBorder.SameDetailsAtBorder => identityOfTransportAtDeparture(inlandMode)
      }

    def nationalityAtCrossing(detailsAtBorder: DetailsAtBorder, inlandMode: InlandMode): Option[String] =
      detailsAtBorder match {
        case newDetailsAtBorder: NewDetailsAtBorder =>
          newDetailsAtBorder.modeCrossingBorder match {
            case ModeCrossingBorder.ModeExemptNationality(_)                             => None
            case ModeCrossingBorder.ModeWithNationality(nationalityCrossingBorder, _, _) => Some(nationalityCrossingBorder.code)
          }
        case DetailsAtBorder.SameDetailsAtBorder => nationalityAtDeparture(inlandMode)
      }

    def modeAtCrossing(detailsAtBorder: DetailsAtBorder, inlandMode: InlandMode): Int =
      detailsAtBorder match {
        case newDetailsAtBorder: NewDetailsAtBorder => newDetailsAtBorder.modeCrossingBorder.modeCode
        case DetailsAtBorder.SameDetailsAtBorder    => inlandMode.code
      }

    def itineraries(itineraries: NonEmptyList[Itinerary]): Seq[models.messages.Itinerary] =
      itineraries.toList.map(
        countryCode => models.messages.Itinerary(countryCode.countryCode.code)
      )

    DeclarationRequest(
      Meta(
        interchangeControlReference = icr,
        dateOfPreparation = dateTimeOfPrep.toLocalDate,
        timeOfPreparation = dateTimeOfPrep.toLocalTime
      ),
      Header(
        refNumHEA4 = preTaskList.lrn.value,
        typOfDecHEA24 = preTaskList.declarationType.code,
        couOfDesCodHEA30 = Some(routeDetails.destinationCountry.code),
        agrLocOfGooCodHEA38 = agreedLocationOfGoodsCode(goodsSummary.goodSummaryDetails), // Not required
        agrLocOfGooHEA39 = agreedLocationOfGoods(goodsSummary.goodSummaryDetails),
        autLocOfGooCodHEA41 = goodsSummarySimplifiedDetails(goodsSummary.goodSummaryDetails).map(_.authorisedLocationCode),
        plaOfLoaCodHEA46 = goodsSummary.loadingPlace,
        couOfDisCodHEA55 = Some(routeDetails.countryOfDispatch.country.code),
        cusSubPlaHEA66 = customsSubPlace(goodsSummary),
        transportDetails = Transport(
          inlTraModHEA75 = Some(transportDetails.inlandMode.code),
          traModAtBorHEA76 = Some(detailsAtBorderMode(transportDetails.detailsAtBorder, transportDetails.inlandMode.code)),
          ideOfMeaOfTraAtDHEA78 = identityOfTransportAtDeparture(transportDetails.inlandMode),
          natOfMeaOfTraAtDHEA80 = nationalityAtDeparture(transportDetails.inlandMode),
          ideOfMeaOfTraCroHEA85 = identityOfTransportAtCrossing(transportDetails.detailsAtBorder, transportDetails.inlandMode),
          natOfMeaOfTraCroHEA87 = nationalityAtCrossing(transportDetails.detailsAtBorder, transportDetails.inlandMode),
          typOfMeaOfTraCroHEA88 = Some(modeAtCrossing(transportDetails.detailsAtBorder, transportDetails.inlandMode))
        ),
        conIndHEA96 = booleanToInt(movementDetails.containersUsed),
        totNumOfIteHEA305 = itemDetails.size,
        totNumOfPacHEA306 = ItemSections(itemDetails).totalPackages,
        totGroMasHEA307 = ItemSections(itemDetails).totalGrossMassFormatted,
        decDatHEA383 = dateTimeOfPrep.toLocalDate,
        decPlaHEA394 = movementDetails.declarationPlacePage,
        speCirIndHEA1 = safetyAndSecurity.flatMap(_.circumstanceIndicator),
        traChaMetOfPayHEA1 = safetyAndSecurity.flatMap(_.paymentMethod) orElse headerPaymentMethodFromItemDetails(journeyDomain.itemDetails),
        comRefNumHEA = safetyAndSecurity.flatMap(_.commercialReferenceNumber) orElse headerCommercialReferenceNumberFromItemDetails(journeyDomain.itemDetails),
        secHEA358 = if (preTaskList.addSecurityDetails) {
          Some(safetyAndSecurityFlag(preTaskList.addSecurityDetails))
        } else {
          None
        },
        conRefNumHEA = safetyAndSecurity.flatMap(_.conveyanceReferenceNumber),
        codPlUnHEA357 = safetyAndSecurity.flatMap(_.placeOfUnloading)
      ),
      principalTrader(traderDetails),
      traderDetails.consignor.map(headerConsignor) orElse headerConsignorFromItemDetails(journeyDomain.itemDetails),
      traderDetails.consignee.map(headerConsignee) orElse headerConsigneeFromItemDetails(journeyDomain.itemDetails),
      None, // not required
      CustomsOfficeDeparture(
        referenceNumber = preTaskList.officeOfDeparture.id
      ),
      customsOfficeTransit(routeDetails.transitInformation),
      CustomsOfficeDestination(
        referenceNumber = routeDetails.destinationOffice.id
      ),
      goodsSummarySimplifiedDetails(goodsSummary.goodSummaryDetails).map(
        x => ControlResult(x.controlResultDateLimit)
      ),
      representative(movementDetails),
      headerSeals(goodsSummary.sealNumbers),
      guaranteeDetails(guarantee),
      goodsItems(journeyDomain.itemDetails, guarantee),
      safetyAndSecurity
        .map(
          sas => itineraries(sas.itineraryList)
        )
        .getOrElse(Seq.empty),
      safetyAndSecurity.flatMap(
        sas => carrier(sas.carrier)
      ),
      safetyAndSecurity.flatMap(
        sas => safetyAndSecurityConsignor(sas.consignor) orElse headerSecurityDetailsFromConsignorItemDetails(journeyDomain.itemDetails)
      ),
      safetyAndSecurity.flatMap(
        sas => safetyAndSecurityConsignee(sas.consignee) orElse headerSecurityDetailsFromConsigneeItemDetails(journeyDomain.itemDetails)
      )
    )
  }

  private def headerConsignor(consignorDetails: ConsignorDetails): TraderConsignor = {
    val ConsignorDetails(name, CommonAddress(addressLine1, addressLine2, postCode, country), eori) = consignorDetails

    TraderConsignor(name, addressLine1, postCode, addressLine2, country.code.code, eori.map(_.value))
  }

  private def headerConsignee(consigneeDetails: ConsigneeDetails): TraderConsignee = {
    val ConsigneeDetails(name, CommonAddress(addressLine1, addressLine2, postCode, country), eori) = consigneeDetails

    TraderConsignee(name, addressLine1, postCode, addressLine2, country.code.code, eori.map(_.value))
  }

  private def headerConsignorFromItemDetails(itemSectionsDetails: NonEmptyList[ItemSection]): Option[TraderConsignor] =
    collectWhen(itemSectionsDetails.size == 1) {
      itemSectionsDetails.map {
        _.consignor.map {
          details =>
            headerConsignor(ConsignorDetails(details.name, details.address, details.eori))
        }
      }.head
    }

  private def headerConsigneeFromItemDetails(itemSectionsDetails: NonEmptyList[ItemSection]): Option[TraderConsignee] =
    collectWhen(itemSectionsDetails.size == 1) {
      itemSectionsDetails.map {
        _.consignee.map {
          details =>
            headerConsignee(ConsigneeDetails(details.name, details.address, details.eori))
        }
      }.head
    }

  private def headerSecurityDetailsFromConsigneeItemDetails(itemSectionsDetails: NonEmptyList[ItemSection]): Option[SafetyAndSecurityConsignee] =
    collectWhen(itemSectionsDetails.size == 1) {
      itemSectionsDetails.map {
        _.itemSecurityTraderDetails.flatMap {
          itemsSecurityTraderDetails =>
            safetyAndSecurityItemConsignee(itemsSecurityTraderDetails)
        }
      }.head
    }

  private def headerSecurityDetailsFromConsignorItemDetails(itemSectionsDetails: NonEmptyList[ItemSection]): Option[SafetyAndSecurityConsignor] =
    collectWhen(itemSectionsDetails.size == 1) {
      itemSectionsDetails.map {
        _.itemSecurityTraderDetails.flatMap {
          itemsSecurityTraderDetails =>
            safetyAndSecurityItemConsignor(itemsSecurityTraderDetails)
        }
      }.head
    }

  private def headerCommercialReferenceNumberFromItemDetails(itemSectionsDetails: NonEmptyList[ItemSection]): Option[String] =
    collectWhen(itemSectionsDetails.size == 1) {
      itemSectionsDetails.map {
        _.itemSecurityTraderDetails.flatMap {
          itemsSecurityTraderDetails =>
            itemsSecurityTraderDetails.commercialReferenceNumber
        }
      }.head
    }

  private def headerPaymentMethodFromItemDetails(itemSectionsDetails: NonEmptyList[ItemSection]): Option[String] =
    collectWhen(itemSectionsDetails.size == 1) {
      itemSectionsDetails.map {
        _.itemSecurityTraderDetails.flatMap {
          itemsSecurityTraderDetails =>
            itemsSecurityTraderDetails.methodOfPayment
        }
      }.head
    }

  private def safetyAndSecurityItemConsignor(itemsSecurityTraderDetails: ItemsSecurityTraderDetails): Option[SafetyAndSecurityConsignor] =
    itemsSecurityTraderDetails.consignor
      .map {
        case SecurityPersonalInformation(name, CommonAddress(addressLine1, addressLine2, postCode, country)) =>
          SafetyAndSecurityConsignorWithoutEori(name, addressLine1, postCode, addressLine2, country.code.code)

        case SecurityTraderEori(EoriNumber(eori)) =>
          SafetyAndSecurityConsignorWithEori(eori)
      }

  private def safetyAndSecurityItemConsignee(itemsSecurityTraderDetails: ItemsSecurityTraderDetails): Option[SafetyAndSecurityConsignee] =
    itemsSecurityTraderDetails.consignee
      .map {
        case SecurityPersonalInformation(name, CommonAddress(addressLine1, addressLine2, postCode, country)) =>
          SafetyAndSecurityConsigneeWithoutEori(name, addressLine1, postCode, addressLine2, country.code.code)
        case SecurityTraderEori(EoriNumber(eori)) =>
          SafetyAndSecurityConsigneeWithEori(eori)
      }

}
