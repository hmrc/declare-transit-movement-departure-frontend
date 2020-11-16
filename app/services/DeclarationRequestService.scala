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

package services

import java.time.LocalDateTime

import cats.data.NonEmptyList
import cats.implicits._
import javax.inject.Inject
import models.domain.Address
import models.journeyDomain.JourneyDomain.Constants
import models.journeyDomain.TransportDetails.DetailsAtBorder
import models.journeyDomain.TransportDetails.DetailsAtBorder.SameDetailsAtBorder
import models.journeyDomain.{GuaranteeDetails, ItemSection, JourneyDomain, Packages, TraderDetails, UserAnswersReader}
import models.messages.customsoffice.{CustomsOfficeDeparture, CustomsOfficeDestination, CustomsOfficeTransit}
import models.messages.goodsitem.{BulkPackage, GoodsItem, RegularPackage, UnpackedPackage}
import models.messages.guarantee.{Guarantee, GuaranteeReferenceWithGrn, GuaranteeReferenceWithOther}
import models.messages.header.{Header, Transport}
import models.messages.trader.{TraderConsignor, TraderPrincipal, TraderPrincipalWithEori, TraderPrincipalWithoutEori}
import models.messages.{booleanToInt, DeclarationRequest, InterchangeControlReference, Meta}
import models.{ConsignorAddress, EoriNumber, UserAnswers}
import repositories.InterchangeControlReferenceIdRepository

import scala.concurrent.{ExecutionContext, Future}

trait DeclarationRequestServiceInt {
  def convert(userAnswers: UserAnswers): Future[Option[DeclarationRequest]]
}

class DeclarationRequestService @Inject()(
  icrRepository: InterchangeControlReferenceIdRepository,
  dateTimeService: DateTimeService
)(implicit ec: ExecutionContext)
    extends DeclarationRequestServiceInt {

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
      guarantee
    ) = journeyDomain

    def guaranteeDetails(guaranteeDetails: GuaranteeDetails): Guarantee =
      guaranteeDetails match {
        case GuaranteeDetails.GuaranteeReference(guaranteeType, guaranteeReferenceNumber, _, accessCode) =>
          val guaranteeReferenceWithGrn = GuaranteeReferenceWithGrn(guaranteeReferenceNumber, accessCode)
          Guarantee(guaranteeType.toString, Seq(guaranteeReferenceWithGrn))
        case GuaranteeDetails.GuaranteeOther(guaranteeType, otherReference, _) =>
          val guaranteeReferenceOther = GuaranteeReferenceWithOther(otherReference, None)
          Guarantee(guaranteeType.toString, Seq(guaranteeReferenceOther))
      }

    def packages(packages: NonEmptyList[Packages]): NonEmptyList[models.messages.goodsitem.Package] =
      packages.map {
        case Packages.UnpackedPackages(packageType, _, totalPieces, markOrNumber) =>
          UnpackedPackage(packageType.toString, totalPieces, markOrNumber)
        case Packages.BulkPackages(packageType, _, markOrNumber) =>
          BulkPackage(packageType.toString, markOrNumber)
        case Packages.OtherPackages(packageType, howManyPackagesPage, markOrNumber) =>
          RegularPackage(packageType.toString, howManyPackagesPage, markOrNumber)
      }

    def goodsItems(goodsItems: NonEmptyList[ItemSection]): NonEmptyList[GoodsItem] =
      goodsItems.zipWithIndex.map {
        case (itemSection, index) =>
          GoodsItem(
            itemNumber                       = index + 1,
            commodityCode                    = itemSection.itemDetails.commodityCode,
            declarationType                  = None,
            description                      = itemSection.itemDetails.itemDescription,
            grossMass                        = Some(BigDecimal(itemSection.itemDetails.totalGrossMass)), //TODO Pass this as a string rather than BigDecimal
            netMass                          = itemSection.itemDetails.totalNetMass.map(BigDecimal(_)), //TODO same here
            countryOfDispatch                = None,
            countryOfDestination             = None,
            methodOfPayment                  = None,
            commercialReferenceNumber        = None,
            dangerousGoodsCode               = None,
            previousAdministrativeReferences = Seq.empty,
            producedDocuments                = Seq.empty,
            specialMention                   = Seq.empty,
            traderConsignorGoodsItem         = None,
            traderConsigneeGoodsItem         = None,
            containers                       = Seq.empty,
            packages                         = packages(itemSection.packages).toList,
            sensitiveGoodsInformation        = Seq.empty
          )
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
          TraderPrincipalWithEori(eori = traderEori.toString, None, None, None, None, None)
      }

    def headerConsignor(traderDetails: TraderDetails): Option[TraderConsignor] =
      traderDetails.consignor
        .flatMap {
          case TraderDetails.PersonalInformation(name, address) =>
            Address.prismAddressToConsignorAddress.getOption(address).map {
              case ConsignorAddress(addressLine1, addressLine2, addressLine3, country) =>
                TraderConsignor(name, addressLine1, addressLine3, addressLine2, country.code.code, None)
            }

          case TraderDetails.TraderEori(EoriNumber(eori)) =>
            Some(TraderConsignor("???", "???", "???", "???", "???", Some(eori))) //TODO populate this
        }

    def detailsAtBorderMode(detailsAtBorder: DetailsAtBorder): Option[String] =
      detailsAtBorder match {
        case SameDetailsAtBorder                            => None
        case DetailsAtBorder.NewDetailsAtBorder(mode, _, _) => Some(mode)
      }

    def detailsAtBorderIdCrossing(detailsAtBorder: DetailsAtBorder): Option[String] =
      detailsAtBorder match {
        case SameDetailsAtBorder                                  => None
        case DetailsAtBorder.NewDetailsAtBorder(_, idCrossing, _) => Some(idCrossing)
      }

    DeclarationRequest(
      Meta(
        interchangeControlReference = icr,
        dateOfPreparation           = dateTimeOfPrep.toLocalDate,
        timeOfPreparation           = dateTimeOfPrep.toLocalTime
      ),
      Header(
        refNumHEA4          = preTaskList.lrn.toString,
        typOfDecHEA24       = movementDetails.declarationType.code,
        couOfDesCodHEA30    = Some(routeDetails.destinationCountry.code),
        agrLocOfGooCodHEA38 = None,
        agrLocOfGooHEA39    = None,
        autLocOfGooCodHEA41 = None,
        plaOfLoaCodHEA46    = None,
        couOfDisCodHEA55    = Some(routeDetails.countryOfDispatch.code),
        cusSubPlaHEA66      = None,
        transportDetails = Transport(
          inlTraModHEA75        = Some(transportDetails.inlandMode.code),
          traModAtBorHEA76      = detailsAtBorderMode(transportDetails.detailsAtBorder),
          ideOfMeaOfTraAtDHEA78 = detailsAtBorderIdCrossing(transportDetails.detailsAtBorder),
          natOfMeaOfTraAtDHEA80 = None,
          ideOfMeaOfTraCroHEA85 = None,
          natOfMeaOfTraCroHEA87 = None,
          typOfMeaOfTraCroHEA88 = None
        ),
        conIndHEA96        = booleanToInt(movementDetails.containersUsed),
        totNumOfIteHEA305  = itemDetails.size,
        totNumOfPacHEA306  = None,
        totGroMasHEA307    = goodsSummary.totalMass,
        decDatHEA383       = dateTimeOfPrep.toLocalDate,
        decPlaHEA394       = movementDetails.declarationPlacePage,
        speCirIndHEA1      = None,
        traChaMetOfPayHEA1 = None,
        comRefNumHEA       = None,
        secHEA358          = None,
        conRefNumHEA       = None,
        codPlUnHEA357      = None
      ),
      principalTrader(traderDetails),
      headerConsignor(traderDetails),
      None,
      None,
      CustomsOfficeDeparture(
        referenceNumber = routeDetails.officeOfDeparture
      ),
      Seq.empty[CustomsOfficeTransit],
      CustomsOfficeDestination(
        referenceNumber = routeDetails.destinationOffice
      ),
      None,
      None,
      None,
      guaranteeDetails(guarantee),
      goodsItems(journeyDomain.itemDetails)
    )
  }
}
