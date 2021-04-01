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

import java.time.{LocalDate, LocalDateTime}

import cats.data.NonEmptyList
import cats.implicits._
import javax.inject.Inject
import models.domain.{Address, SealDomain}
import models.journeyDomain.GoodsSummary.{GoodSummaryDetails, GoodSummaryNormalDetails, GoodSummarySimplifiedDetails}
import models.journeyDomain.GuaranteeDetails.GuaranteeReference
import models.journeyDomain.ItemTraderDetails.RequiredDetails
import models.journeyDomain.JourneyDomain.Constants
import models.journeyDomain.RouteDetails.TransitInformation
import models.journeyDomain.SafetyAndSecurity.SecurityTraderDetails
import models.journeyDomain.TransportDetails.DetailsAtBorder.{NewDetailsAtBorder, SameDetailsAtBorder}
import models.journeyDomain.TransportDetails.{DetailsAtBorder, InlandMode, ModeCrossingBorder}
import models.journeyDomain.{GuaranteeDetails, ItemSection, Itinerary, JourneyDomain, Packages, ProducedDocument, TraderDetails, UserAnswersReader, _}
import models.messages._
import models.messages.customsoffice.{CustomsOfficeDeparture, CustomsOfficeDestination, CustomsOfficeTransit}
import models.messages.goodsitem.{BulkPackage, GoodsItem, RegularPackage, UnpackedPackage, _}
import models.messages.guarantee.{Guarantee, GuaranteeReferenceWithGrn, GuaranteeReferenceWithOther}
import models.messages.header.{Header, Transport}
import models.messages.safetyAndSecurity._
import models.messages.trader.{TraderConsignor, TraderPrincipal, TraderPrincipalWithEori, TraderPrincipalWithoutEori, _}
import models.{CarrierAddress, ConsigneeAddress, ConsignorAddress, EoriNumber, UserAnswers}
import play.api.Logger
import repositories.InterchangeControlReferenceIdRepository

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}

trait DeclarationRequestServiceInt {
  def convert(userAnswers: UserAnswers): Future[Option[DeclarationRequest]]
}

class DeclarationRequestService @Inject()(
  icrRepository: InterchangeControlReferenceIdRepository,
  dateTimeService: DateTimeService
)(implicit ec: ExecutionContext)
    extends DeclarationRequestServiceInt {

  val logger: Logger = Logger(getClass)

  override def convert(userAnswers: UserAnswers): Future[Option[DeclarationRequest]] =
    icrRepository.nextInterchangeControlReferenceId().map {
      icrId =>
        UserAnswersReader[JourneyDomain]
          .map(journeyModelToSubmissionModel(_, icrId, dateTimeService.currentDateTime))
          .run(userAnswers)
    }

  private def journeyModelToSubmissionModel(journeyDomain: JourneyDomain,
                                            icr: InterchangeControlReference,
                                            dateTimeOfPrep: LocalDateTime): DeclarationRequest = {

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

    def additionalInformationLiabilityAmount(itemIndex: Int, guaranteeDetails: NonEmptyList[GuaranteeDetails]): Seq[SpecialMentionGuaranteeLiabilityAmount] =
      if (itemIndex == 0) {
        guaranteeDetails.toList collect {
          case GuaranteeDetails.GuaranteeReference(_, guaranteeReferenceNumber, liabilityAmount, _) =>
            specialMentionLiability(liabilityAmount, guaranteeReferenceNumber)
        }
      } else Seq.empty

    def specialMentionLiability(liabilityAmount: String, guaranteeReferenceNumber: String): SpecialMentionGuaranteeLiabilityAmount =
      liabilityAmount match {
        case GuaranteeReference.defaultLiability =>
          val defaultLiabilityAmount = s"${GuaranteeReference.defaultLiability}EUR$guaranteeReferenceNumber"
          SpecialMentionGuaranteeLiabilityAmount("CAL", defaultLiabilityAmount)

        case otherAmount =>
          val notDefaultAmount = s"${otherAmount}GBP$guaranteeReferenceNumber"
          SpecialMentionGuaranteeLiabilityAmount("CAL", notDefaultAmount)
      }

    def packages(packages: NonEmptyList[Packages]): NonEmptyList[models.messages.goodsitem.Package] =
      packages.map {
        case Packages.UnpackedPackages(packageType, _, totalPieces, markOrNumber) =>
          UnpackedPackage(packageType.code, totalPieces, markOrNumber)
        case Packages.BulkPackages(packageType, _, markOrNumber) =>
          BulkPackage(packageType.code, markOrNumber)
        case Packages.OtherPackages(packageType, howManyPackagesPage, markOrNumber) =>
          RegularPackage(packageType.code, howManyPackagesPage, markOrNumber)
      }

    def goodsItems(goodsItems: NonEmptyList[ItemSection], guaranteeDetails: NonEmptyList[GuaranteeDetails]): NonEmptyList[GoodsItem] =
      goodsItems.zipWithIndex.map {
        case (itemSection, index) =>
          GoodsItem(
            itemNumber                       = index + 1,
            commodityCode                    = itemSection.itemDetails.commodityCode,
            declarationType                  = None, // Clarify with policy
            description                      = itemSection.itemDetails.itemDescription,
            grossMass                        = Some(BigDecimal(itemSection.itemDetails.totalGrossMass)),
            netMass                          = itemSection.itemDetails.totalNetMass.map(BigDecimal(_)),
            countryOfDispatch                = None, // Not required, defined at header level
            countryOfDestination             = None, // Not required, defined at header level
            methodOfPayment                  = itemSection.itemSecurityTraderDetails.flatMap(_.methodOfPayment),
            commercialReferenceNumber        = itemSection.itemSecurityTraderDetails.flatMap(_.commercialReferenceNumber),
            dangerousGoodsCode               = itemSection.itemSecurityTraderDetails.flatMap(_.dangerousGoodsCode),
            previousAdministrativeReferences = previousAdministrativeReference(itemSection.previousReferences),
            producedDocuments                = producedDocuments(itemSection.producedDocuments),
            specialMention                   = additionalInformationLiabilityAmount(index, guaranteeDetails),
            traderConsignorGoodsItem         = traderConsignor(itemSection.consignor),
            traderConsigneeGoodsItem         = traderConsignee(itemSection.consignee),
            containers                       = containers(itemSection.containers),
            packages                         = packages(itemSection.packages).toList,
            sensitiveGoodsInformation        = Seq.empty, // Not required, defined at security level
            GoodsItemSafetyAndSecurityConsignor(itemSection.itemSecurityTraderDetails),
            GoodsItemSafetyAndSecurityConsignee(itemSection.itemSecurityTraderDetails)
          )
      }

    def previousAdministrativeReference(previousReferences: Option[NonEmptyList[PreviousReferences]]): Seq[PreviousAdministrativeReference] =
      previousReferences.map(_.toList.map(x => PreviousAdministrativeReference(x.referenceType, x.previousReference, x.extraInformation))).getOrElse(List.empty)

    def producedDocuments(producedDocument: Option[NonEmptyList[models.journeyDomain.ProducedDocument]]): Seq[goodsitem.ProducedDocument] =
      producedDocument.map(_.toList.map(x => goodsitem.ProducedDocument(x.documentType, Some(x.documentReference), x.extraInformation))).getOrElse(List.empty)

    def containers(containers: Option[NonEmptyList[Container]]): Seq[String] =
      containers.map(_.toList.map(_.containerNumber)).getOrElse(List.empty)

    def GoodsItemSafetyAndSecurityConsignor(itemSecurityTraderDetails: Option[ItemsSecurityTraderDetails]): Option[GoodsItemSecurityConsignor] =
      itemSecurityTraderDetails.flatMap {
        x =>
          x.consignor.map {
            case ItemsSecurityTraderDetails.SecurityPersonalInformation(name, Address(buildingAndStreet, city, postcode, _)) =>
              ItemsSecurityConsignorWithoutEori(name, buildingAndStreet, postcode, city, "GB")
            case ItemsSecurityTraderDetails.SecurityTraderEori(eori) =>
              ItemsSecurityConsignorWithEori(eori.value)
          }
      }

    def GoodsItemSafetyAndSecurityConsignee(itemSecurityTraderDetails: Option[ItemsSecurityTraderDetails]): Option[GoodsItemSecurityConsignee] =
      itemSecurityTraderDetails.flatMap {
        x =>
          x.consignor.map {
            case ItemsSecurityTraderDetails.SecurityPersonalInformation(name, Address(buildingAndStreet, city, postcode, _)) =>
              ItemsSecurityConsigneeWithoutEori(name, buildingAndStreet, postcode, city, "GB")
            case ItemsSecurityTraderDetails.SecurityTraderEori(eori) =>
              ItemsSecurityConsigneeWithEori(eori.value)
          }
      }

    def principalTrader(traderDetails: TraderDetails): TraderPrincipal =
      traderDetails.principalTraderDetails match {
        case TraderDetails.PersonalInformation(name, Address(buildingAndStreet, city, postcode, _)) =>
          TraderPrincipalWithoutEori(
            name            = name,
            streetAndNumber = buildingAndStreet,
            postCode        = postcode,
            city            = city,
            countryCode     = Constants.principalTraderCountryCode.code
          )
        case TraderDetails.TraderEori(traderEori) =>
          TraderPrincipalWithEori(eori = traderEori.value, None, None, None, None, None)
      }

    def headerConsignor(traderDetails: TraderDetails): Option[TraderConsignor] =
      traderDetails.consignor
        .flatMap {
          case TraderDetails.TraderInformation(name, address, eori) =>
            Address.prismAddressToConsignorAddress.getOption(address).map {
              case ConsignorAddress(addressLine1, addressLine2, addressLine3, country) =>
                TraderConsignor(name, addressLine1, addressLine3, addressLine2, country.code.code, eori.map(_.value))
            }
          case _ =>
            logger.error(s"headerConsignor failed to get name and address")
            None
        }

    def headerConsignee(traderDetails: TraderDetails): Option[TraderConsignee] =
      traderDetails.consignee
        .flatMap {
          case TraderDetails.TraderInformation(name, address, eori) =>
            Address.prismAddressToConsigneeAddress.getOption(address).map {
              case ConsigneeAddress(addressLine1, addressLine2, addressLine3, country) =>
                TraderConsignee(name, addressLine1, addressLine3, addressLine2, country.code.code, eori.map(_.value))
            }
          case _ =>
            logger.error(s"headerConsignee failed to get name and address")
            None
        }

    def detailsAtBorderMode(detailsAtBorder: DetailsAtBorder, inlandCode: Int): String =
      detailsAtBorder match {
        case DetailsAtBorder.NewDetailsAtBorder(mode, _, _) => mode
        case SameDetailsAtBorder                            => inlandCode.toString
      }

    def customsOfficeTransit(transitInformation: NonEmptyList[TransitInformation]): Seq[CustomsOfficeTransit] =
      transitInformation.map {
        case TransitInformation(office, arrivalTime) => CustomsOfficeTransit(office, arrivalTime)
      }.toList

    // TODO confirm if authorisedLocationCode is the same thing (else last case just returns None)
    def customsSubPlace(goodsSummary: GoodsSummary): Option[String] =
      goodsSummary.goodSummaryDetails match {
        case GoodsSummary.GoodSummaryNormalDetails(customsApprovedLocation) =>
          customsApprovedLocation
        case GoodsSummary.GoodSummarySimplifiedDetails(authorisedLocationCode, _) =>
          Some(authorisedLocationCode)
      }

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
          case ItemTraderDetails.RequiredDetails(name, address, eori) =>
            Address.prismAddressToConsignorAddress.getOption(address).map {
              case ConsignorAddress(addressLine1, addressLine2, addressLine3, country) =>
                TraderConsignorGoodsItem(name, addressLine1, addressLine3, addressLine2, country.code.code, eori.map(_.value))
            }
          case _ =>
            logger.error(s"traderConsignor failed to get name and address")
            None
        }

    def traderConsignee(requiredDetails: Option[RequiredDetails]): Option[TraderConsigneeGoodsItem] =
      requiredDetails
        .flatMap {
          case ItemTraderDetails.RequiredDetails(name, address, eori) =>
            Address.prismAddressToConsigneeAddress.getOption(address).map {
              case ConsigneeAddress(addressLine1, addressLine2, addressLine3, country) =>
                TraderConsigneeGoodsItem(name, addressLine1, addressLine3, addressLine2, country.code.code, eori.map(_.value))
            }
          case _ =>
            logger.error(s"traderConsignee failed to get name and address")
            None
        }

    def nationalityAtDeparture(inlandMode: InlandMode): Option[String] =
      inlandMode match {
        case InlandMode.NonSpecialMode(_, nationalityAtDeparture, _) => Some(nationalityAtDeparture.get.code)
        case _                                                       => None
      }

    def identityOfTransportAtDeparture(inlandMode: InlandMode): Option[String] =
      inlandMode match {
        case InlandMode.NonSpecialMode(_, _, departureId) => departureId
        case _                                            => None
      }

    def agreedLocationOfGoods(movementDetails: MovementDetails, goodsSummaryDetails: GoodSummaryDetails): Option[String] =
      (movementDetails, goodsSummaryDetails) match {
        case (MovementDetails.NormalMovementDetails(_, prelodge, _, _, _), GoodSummaryNormalDetails(approvedLocation)) =>
          if (prelodge) Some("Pre-lodge") else approvedLocation
        case _ => None
      }

    def goodsSummarySimplifiedDetails(goodsSummaryDetails: GoodSummaryDetails): Option[GoodSummarySimplifiedDetails] =
      goodsSummaryDetails match {
        case result: GoodSummarySimplifiedDetails => Some(result)
        case _                                    => None
      }

    def safetyAndSecurityFlag(boolFlag: Boolean): Int = if (boolFlag) 1 else 0

    def safetyAndSecurityConsignee(securityTraderDetails: Option[SecurityTraderDetails]): Option[SafetyAndSecurityConsignee] =
      securityTraderDetails
        .flatMap {
          case SafetyAndSecurity.PersonalInformation(name, address) =>
            Address.prismAddressToConsigneeAddress.getOption(address).map {
              case ConsigneeAddress(addressLine1, addressLine2, addressLine3, country) =>
                SafetyAndSecurityConsigneeWithoutEori(name, addressLine1, addressLine3, addressLine2, country.code.code)
            }

          case SafetyAndSecurity.TraderEori(EoriNumber(eori)) =>
            Some(SafetyAndSecurityConsigneeWithEori(eori))
        }

    def safetyAndSecurityConsignor(securityTraderDetails: Option[SecurityTraderDetails]): Option[SafetyAndSecurityConsignor] =
      securityTraderDetails
        .flatMap {
          case SafetyAndSecurity.PersonalInformation(name, address) =>
            Address.prismAddressToConsignorAddress.getOption(address).map {
              case ConsignorAddress(addressLine1, addressLine2, addressLine3, country) =>
                SafetyAndSecurityConsignorWithoutEori(name, addressLine1, addressLine3, addressLine2, country.code.code)
            }

          case SafetyAndSecurity.TraderEori(EoriNumber(eori)) =>
            Some(SafetyAndSecurityConsignorWithEori(eori))
        }

    def carrier(securityTraderDetails: Option[SecurityTraderDetails]): Option[SafetyAndSecurityCarrier] =
      securityTraderDetails
        .flatMap {
          case SafetyAndSecurity.PersonalInformation(name, address) =>
            Address.prismAddressToCarrierAddress.getOption(address).map {
              case CarrierAddress(addressLine1, addressLine2, addressLine3, country) =>
                SafetyAndSecurityCarrierWithoutEori(name, addressLine1, addressLine3, addressLine2, country.code.code)
            }
          case SafetyAndSecurity.TraderEori(EoriNumber(eori)) =>
            Some(SafetyAndSecurityCarrierWithEori(eori))
        }

    def identityOfTransportAtCrossing(detailsAtBorder: DetailsAtBorder, inlandMode: InlandMode): Option[String] =
      detailsAtBorder match {
        case newDetailsAtBorder: NewDetailsAtBorder => Some(newDetailsAtBorder.idCrossing)
        case DetailsAtBorder.SameDetailsAtBorder    => identityOfTransportAtDeparture(inlandMode)
      }

    def nationalityAtCrossing(detailsAtBorder: DetailsAtBorder, inlandMode: InlandMode): Option[String] =
      detailsAtBorder match {
        case newDetailsAtBorder: NewDetailsAtBorder =>
          newDetailsAtBorder.modeCrossingBorder match {
            case ModeCrossingBorder.ModeExemptNationality(_)                          => None
            case ModeCrossingBorder.ModeWithNationality(nationalityCrossingBorder, _) => Some(nationalityCrossingBorder.code)
          }
        case DetailsAtBorder.SameDetailsAtBorder => nationalityAtDeparture(inlandMode)
      }

    def modeAtCrossing(detailsAtBorder: DetailsAtBorder, inlandMode: InlandMode): Int =
      detailsAtBorder match {
        case newDetailsAtBorder: NewDetailsAtBorder => newDetailsAtBorder.modeCrossingBorder.modeCode
        case DetailsAtBorder.SameDetailsAtBorder    => inlandMode.code
      }

    def itineraries(itineraries: NonEmptyList[Itinerary]): Seq[models.messages.Itinerary] =
      itineraries.toList.map(countryCode => models.messages.Itinerary(countryCode.countryCode.code))

    DeclarationRequest(
      Meta(
        interchangeControlReference = icr,
        dateOfPreparation           = dateTimeOfPrep.toLocalDate,
        timeOfPreparation           = dateTimeOfPrep.toLocalTime
      ),
      Header(
        refNumHEA4          = preTaskList.lrn.value,
        typOfDecHEA24       = movementDetails.declarationType.code,
        couOfDesCodHEA30    = Some(routeDetails.destinationCountry.code),
        agrLocOfGooCodHEA38 = None, // Not required
        agrLocOfGooHEA39    = agreedLocationOfGoods(movementDetails, goodsSummary.goodSummaryDetails),
        autLocOfGooCodHEA41 = goodsSummarySimplifiedDetails(goodsSummary.goodSummaryDetails).map(_.authorisedLocationCode),
        plaOfLoaCodHEA46    = goodsSummary.loadingPlace,
        couOfDisCodHEA55    = Some(routeDetails.countryOfDispatch.code),
        cusSubPlaHEA66      = customsSubPlace(goodsSummary),
        transportDetails = Transport(
          inlTraModHEA75        = Some(transportDetails.inlandMode.code),
          traModAtBorHEA76      = Some(detailsAtBorderMode(transportDetails.detailsAtBorder, transportDetails.inlandMode.code)),
          ideOfMeaOfTraAtDHEA78 = identityOfTransportAtDeparture(transportDetails.inlandMode),
          natOfMeaOfTraAtDHEA80 = nationalityAtDeparture(transportDetails.inlandMode),
          ideOfMeaOfTraCroHEA85 = identityOfTransportAtCrossing(transportDetails.detailsAtBorder, transportDetails.inlandMode),
          natOfMeaOfTraCroHEA87 = nationalityAtCrossing(transportDetails.detailsAtBorder, transportDetails.inlandMode),
          typOfMeaOfTraCroHEA88 = Some(modeAtCrossing(transportDetails.detailsAtBorder, transportDetails.inlandMode))
        ),
        conIndHEA96        = booleanToInt(movementDetails.containersUsed),
        totNumOfIteHEA305  = itemDetails.size,
        totNumOfPacHEA306  = goodsSummary.numberOfPackages,
        totGroMasHEA307    = goodsSummary.totalMass,
        decDatHEA383       = dateTimeOfPrep.toLocalDate,
        decPlaHEA394       = movementDetails.declarationPlacePage,
        speCirIndHEA1      = safetyAndSecurity.flatMap(_.circumstanceIndicator),
        traChaMetOfPayHEA1 = safetyAndSecurity.flatMap(_.paymentMethod),
        comRefNumHEA       = safetyAndSecurity.flatMap(_.commercialReferenceNumber),
        secHEA358 = if (preTaskList.addSecurityDetails) {
          Some(safetyAndSecurityFlag(preTaskList.addSecurityDetails))
        } else {
          None
        },
        conRefNumHEA  = safetyAndSecurity.flatMap(_.conveyanceReferenceNumber),
        codPlUnHEA357 = safetyAndSecurity.flatMap(_.placeOfUnloading)
      ),
      principalTrader(traderDetails),
      headerConsignor(traderDetails),
      headerConsignee(traderDetails),
      None, // not required
      CustomsOfficeDeparture(
        referenceNumber = routeDetails.officeOfDeparture.id
      ),
      customsOfficeTransit(routeDetails.transitInformation),
      CustomsOfficeDestination(
        referenceNumber = routeDetails.destinationOffice.id
      ),
      goodsSummarySimplifiedDetails(goodsSummary.goodSummaryDetails).map(x => ControlResult(x.controlResultDateLimit)),
      representative(movementDetails),
      headerSeals(goodsSummary.sealNumbers),
      guaranteeDetails(guarantee),
      goodsItems(journeyDomain.itemDetails, guarantee),
      safetyAndSecurity.map(sas => itineraries(sas.itineraryList)).getOrElse(Seq.empty),
      safetyAndSecurity.flatMap(sas => carrier(sas.carrier)),
      safetyAndSecurity.flatMap(sas => safetyAndSecurityConsignor(sas.consignor)),
      safetyAndSecurity.flatMap(sas => safetyAndSecurityConsignee(sas.consignee))
    )
  }
}
