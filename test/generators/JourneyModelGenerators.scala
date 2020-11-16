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

package generators

import java.time.{LocalDate, LocalDateTime}

import models.domain.{Address, SealDomain}
import models.journeyDomain.GoodsSummary.{GoodSummaryDetails, GoodSummaryNormalDetails, GoodSummarySimplifiedDetails}
import models.journeyDomain.GuaranteeDetails.{GuaranteeOther, GuaranteeReference}
import models.journeyDomain.MovementDetails.{DeclarationForSelf, DeclarationForSomeoneElse, DeclarationForSomeoneElseAnswer, NormalMovementDetails, SimplifiedMovementDetails}
import models.journeyDomain.Packages.{BulkPackages, OtherPackages, UnpackedPackages}
import models.journeyDomain.RouteDetails.TransitInformation
import models.journeyDomain.TraderDetails.{PersonalInformation, RequiredDetails, TraderEori}
import models.journeyDomain.TransportDetails.DetailsAtBorder.{NewDetailsAtBorder, SameDetailsAtBorder}
import models.journeyDomain.TransportDetails.InlandMode.{Mode5or7, NonSpecialMode, Rail}
import models.journeyDomain.TransportDetails.ModeCrossingBorder.{ModeExemptNationality, ModeWithNationality}
import models.journeyDomain.TransportDetails.{DetailsAtBorder, InlandMode, ModeCrossingBorder}
import models.journeyDomain.{GoodsSummary, GuaranteeDetails, ItemDetails, ItemSection, JourneyDomain, MovementDetails, Packages, PreTaskListDetails, RouteDetails, TraderDetails, TransportDetails}
import models.reference.{CountryCode, PackageType}
import models.{ConsigneeAddress, ConsignorAddress, DeclarationType, EoriNumber, GuaranteeType, LocalReferenceNumber, PrincipalAddress, ProcedureType, RepresentativeCapacity}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait JourneyModelGenerators {
  self: Generators =>

  implicit def arbitraryJourneyDomain: Arbitrary[JourneyDomain] =
    Arbitrary {
      for {
        preTaskList <- arbitrary[PreTaskListDetails]
        isNormalMovement = preTaskList.procedureType == ProcedureType.Normal
        movementDetails <- if (isNormalMovement) {
          arbitrary[NormalMovementDetails]
        } else {
          arbitrary[SimplifiedMovementDetails]
        }

        isSecurityDetailsRequired = preTaskList.addSecurityDetails
        transitInformation = if (isSecurityDetailsRequired) {
          Arbitrary(genTransitInformationWithArrivalTime)
        } else {
          Arbitrary(genTransitInformationWithoutArrivalTime)
        }
        routeDetails     <- arbitraryRouteDetails(transitInformation).arbitrary
        transportDetails <- arbitrary[TransportDetails]
        traderDetails    <- arbitrary[TraderDetails]
        itemDetails      <- nonEmptyListOf[ItemSection](3)
        goodsummarydetaislType = if (isNormalMovement) {
          arbitrary[GoodSummaryNormalDetails]
        } else {
          arbitrary[GoodSummarySimplifiedDetails]
        }
        goodsSummary <- arbitraryGoodsSummary(Arbitrary(goodsummarydetaislType)).arbitrary
        guarantee    <- arbitrary[GuaranteeDetails]
      } yield
        JourneyDomain(
          preTaskList,
          movementDetails,
          routeDetails,
          transportDetails,
          traderDetails,
          itemDetails,
          goodsSummary,
          guarantee
        )
    }

  implicit lazy val arbitraryModeCrossingBorder: Arbitrary[ModeCrossingBorder] =
    Arbitrary(
      Gen.oneOf(
        arbitrary[ModeExemptNationality.type],
        arbitrary[ModeWithNationality]
      )
    )

  implicit lazy val arbitraryModeExemptNationality: Arbitrary[ModeExemptNationality.type] =
    Arbitrary(Gen.const(ModeExemptNationality))

  implicit lazy val arbitraryModeWithNationality: Arbitrary[ModeWithNationality] =
    Arbitrary {
      for {
        cc <- arbitrary[CountryCode]

      } yield ModeWithNationality(cc)
    }

  implicit lazy val arbitraryDetailsAtBorder: Arbitrary[DetailsAtBorder] =
    Arbitrary(
      Gen.oneOf(
        arbitrary[SameDetailsAtBorder.type],
        arbitrary[NewDetailsAtBorder]
      )
    )

  implicit lazy val arbitrarySameDetailsAtBorder: Arbitrary[SameDetailsAtBorder.type] =
    Arbitrary(Gen.const(SameDetailsAtBorder))

  implicit lazy val arbitraryNewDetailsAtBorder: Arbitrary[NewDetailsAtBorder] =
    Arbitrary {
      for {
        mode               <- genNumberString
        idCrossing         <- stringsWithMaxLength(stringMaxLength)
        modeCrossingBorder <- arbitrary[ModeCrossingBorder]
      } yield
        NewDetailsAtBorder(
          mode,
          idCrossing,
          modeCrossingBorder
        )
    }

  implicit lazy val arbitraryInlandMode: Arbitrary[InlandMode] =
    Arbitrary(
      Gen.oneOf(
        arbitrary[Rail],
        arbitrary[Mode5or7],
        arbitrary[NonSpecialMode]
      )
    )

  implicit lazy val arbitraryRail: Arbitrary[Rail] =
    Arbitrary {
      for {
        code <- Gen.oneOf(Rail.Constants.codes).map(_.toInt)
      } yield Rail(code)
    }

  implicit lazy val arbitraryMode5or7: Arbitrary[Mode5or7] =
    Arbitrary {
      for {
        code        <- Gen.oneOf(Mode5or7.Constants.codes).map(_.toInt)
        countryCode <- arbitrary[CountryCode]
      } yield Mode5or7(code, countryCode)
    }

  implicit lazy val arbitraryNonSpecialMode: Arbitrary[NonSpecialMode] =
    Arbitrary {
      for {
        code                   <- Gen.const(42)
        nationalityAtDeparture <- arbitrary[CountryCode]
        departureId            <- Gen.option(stringsWithMaxLength(stringMaxLength))
      } yield
        NonSpecialMode(
          code,
          nationalityAtDeparture,
          departureId
        )
    }

  implicit lazy val arbitraryTransportDetails: Arbitrary[TransportDetails] =
    Arbitrary {
      for {
        inlandMode      <- arbitrary[InlandMode]
        detailsAtBorder <- arbitrary[DetailsAtBorder]
      } yield
        TransportDetails(
          inlandMode,
          detailsAtBorder
        )
    }

  implicit val arbitraryTraderDetails: Arbitrary[TraderDetails] = {
    val pricipalAddress  = Arbitrary(arbitrary[PrincipalAddress].map(Address.prismAddressToPrincipalAddress.reverseGet))
    val consignorAddress = Arbitrary(arbitrary[ConsignorAddress].map(Address.prismAddressToConsignorAddress.reverseGet))
    val consigneeAddress = Arbitrary(arbitrary[ConsigneeAddress].map(Address.prismAddressToConsigneeAddress.reverseGet))

    Arbitrary {
      for {
        principalTraderDetails <- arbitraryRequiredDetails(pricipalAddress).arbitrary
        consignor              <- Gen.option(arbitraryRequiredDetails(consignorAddress).arbitrary)
        consignee              <- Gen.option(arbitraryRequiredDetails(consigneeAddress).arbitrary)
      } yield TraderDetails(principalTraderDetails, consignor, consignee)
    }
  }

  implicit def arbitraryRequiredDetails(implicit arbAddress: Arbitrary[Address]): Arbitrary[RequiredDetails] =
    Arbitrary(Gen.oneOf(Arbitrary.arbitrary[PersonalInformation], Arbitrary.arbitrary[TraderEori]))

  implicit lazy val arbitraryTraderEori: Arbitrary[TraderEori] =
    Arbitrary(Arbitrary.arbitrary[EoriNumber].map(TraderEori(_)))

  implicit def arbitraryPersonalInformation(implicit arbAddress: Arbitrary[Address]): Arbitrary[PersonalInformation] =
    Arbitrary {
      for {
        name    <- stringsWithMaxLength(stringMaxLength)
        address <- arbAddress.arbitrary
      } yield PersonalInformation(name, address)
    }

  implicit lazy val arbitraryItemSection: Arbitrary[ItemSection] =
    Arbitrary {
      for {
        itemDetail <- arbitrary[ItemDetails]
        packages   <- nonEmptyListOf[Packages](10)
      } yield ItemSection(itemDetail, packages)
    }

  implicit lazy val arbitraryPreTaskListDetails: Arbitrary[PreTaskListDetails] =
    Arbitrary {
      for {
        lrn                <- arbitrary[LocalReferenceNumber]
        procedureType      <- arbitrary[ProcedureType]
        addSecurityDetails <- arbitrary[Boolean]
      } yield PreTaskListDetails(lrn, procedureType, addSecurityDetails)
    }

  implicit lazy val arbitraryGuaranteeDetails: Arbitrary[GuaranteeDetails] =
    Arbitrary(Gen.oneOf(arbitrary[GuaranteeReference], arbitrary[GuaranteeOther]))

  implicit lazy val arbitraryGuaranteeOther: Arbitrary[GuaranteeOther] =
    Arbitrary {
      for {
        guaranteeType   <- Arbitrary.arbitrary[GuaranteeType]
        otherReference  <- nonEmptyString
        liabilityAmount <- nonEmptyString
      } yield GuaranteeOther(guaranteeType, otherReference, liabilityAmount)
    }

  implicit lazy val arbitraryGuaranteeReference: Arbitrary[GuaranteeReference] =
    Arbitrary {
      for {
        guaranteeType            <- Arbitrary.arbitrary[GuaranteeType]
        guaranteeReferenceNumber <- nonEmptyString
        liabilityAmount          <- nonEmptyString
        accessCode               <- nonEmptyString
      } yield GuaranteeReference(guaranteeType, guaranteeReferenceNumber, liabilityAmount, accessCode)
    }

  implicit lazy val arbitraryPackages: Arbitrary[Packages] =
    Arbitrary(Gen.oneOf(arbitrary[UnpackedPackages], arbitrary[BulkPackages], arbitrary[OtherPackages]))

  implicit lazy val arbitraryUnpackedPackages: Arbitrary[UnpackedPackages] =
    Arbitrary {
      for {
        packageType         <- arbitraryUnPackedPackageType.arbitrary
        howManyPackagesPage <- Gen.option(Gen.choose(1, 10))
        totalPieces         <- Gen.choose(1, 10)
        markOrNumber        <- Gen.option(arbitrary[String])
      } yield UnpackedPackages(packageType, howManyPackagesPage, totalPieces, markOrNumber)
    }

  implicit lazy val arbitraryBulkPackage: Arbitrary[BulkPackages] =
    Arbitrary {
      for {
        bulkPackage         <- arbitraryBulkPackageType.arbitrary
        howManyPackagesPage <- Gen.option(Gen.choose(1, 10))
        markOrNumber        <- Gen.option(arbitrary[String])
      } yield BulkPackages(bulkPackage, howManyPackagesPage, markOrNumber)
    }

  implicit lazy val arbitraryOtherPackage: Arbitrary[OtherPackages] =
    Arbitrary {
      for {
        code                <- nonEmptyString
        description         <- nonEmptyString
        howManyPackagesPage <- Gen.choose(1, 10)
        markOrNumber        <- arbitrary[String]
      } yield OtherPackages(PackageType(code, description), howManyPackagesPage, markOrNumber)
    }

  implicit lazy val arbitraryItemDetails: Arbitrary[ItemDetails] =
    Arbitrary {
      for {
        itemDescription <- nonEmptyString
        totalGrossMass  <- genNumberString
        totalNetMass    <- Gen.option(genNumberString)
        commodityCode   <- Gen.option(arbitrary[String])
      } yield ItemDetails(itemDescription, totalGrossMass, totalNetMass, commodityCode)
    }

  implicit lazy val arbitraryDeclarationForSelf: Arbitrary[DeclarationForSelf.type] =
    Arbitrary(Gen.const(DeclarationForSelf))

  implicit lazy val arbitraryDeclarationForSomeoneElse: Arbitrary[DeclarationForSomeoneElse] =
    Arbitrary {
      for {
        companyName <- stringsWithMaxLength(stringMaxLength)
        capacity    <- arbitrary[RepresentativeCapacity]
      } yield DeclarationForSomeoneElse(companyName, capacity)
    }

  implicit lazy val arbitraryDeclarationForSomeoneElseAnswer: Arbitrary[DeclarationForSomeoneElseAnswer] =
    Arbitrary(Gen.oneOf(arbitrary[DeclarationForSelf.type], arbitrary[DeclarationForSomeoneElse]))

  implicit lazy val arbitrarySimplifiedMovementDetails: Arbitrary[SimplifiedMovementDetails] =
    Arbitrary {
      for {
        declarationType           <- arbitrary[DeclarationType]
        containersUsed            <- arbitrary[Boolean]
        declarationPlacePage      <- stringsWithMaxLength(stringMaxLength)
        declarationForSomeoneElse <- arbitrary[DeclarationForSomeoneElseAnswer]
      } yield
        SimplifiedMovementDetails(
          declarationType,
          containersUsed,
          declarationPlacePage,
          declarationForSomeoneElse
        )
    }

  implicit lazy val arbitraryNormalMovementDetails: Arbitrary[NormalMovementDetails] =
    Arbitrary {
      for {
        declarationType           <- arbitrary[DeclarationType]
        preLodge                  <- arbitrary[Boolean]
        containersUsed            <- arbitrary[Boolean]
        declarationPlacePage      <- stringsWithMaxLength(stringMaxLength)
        declarationForSomeoneElse <- arbitrary[DeclarationForSomeoneElseAnswer]
      } yield
        MovementDetails.NormalMovementDetails(
          declarationType,
          preLodge,
          containersUsed,
          declarationPlacePage,
          declarationForSomeoneElse
        )
    }

  implicit lazy val arbitraryMovementDetails: Arbitrary[MovementDetails] =
    Arbitrary(Gen.oneOf(arbitrary[NormalMovementDetails], arbitrary[SimplifiedMovementDetails]))

  val genTransitInformationWithoutArrivalTime =
    for {
      transitOffice <- stringsWithMaxLength(stringMaxLength)
    } yield
      TransitInformation(
        transitOffice,
        None
      )

  val genTransitInformationWithArrivalTime =
    for {
      transitOffice <- stringsWithMaxLength(stringMaxLength)
      arrivalTime   <- arbitrary[LocalDateTime]
    } yield
      TransitInformation(
        transitOffice,
        Some(arrivalTime)
      )

  implicit lazy val arbitraryTransitInformation: Arbitrary[TransitInformation] =
    Arbitrary(Gen.oneOf(genTransitInformationWithoutArrivalTime, genTransitInformationWithArrivalTime))

  implicit def arbitraryRouteDetails(implicit arbTransitInformation: Arbitrary[TransitInformation]): Arbitrary[RouteDetails] =
    Arbitrary {
      for {
        countryOfDispatch  <- arbitrary[CountryCode]
        officeOfDeparture  <- stringsWithMaxLength(stringMaxLength)
        destinationCountry <- arbitrary[CountryCode]
        destinationOffice  <- stringsWithMaxLength(stringMaxLength)
        transitInformation <- nonEmptyListOf[TransitInformation](10)(arbTransitInformation)
      } yield
        RouteDetails(
          countryOfDispatch,
          officeOfDeparture,
          destinationCountry,
          destinationOffice,
          transitInformation
        )
    }

  implicit lazy val arbitraryGoodSummarySimplifiedDetails: Arbitrary[GoodSummarySimplifiedDetails] =
    Arbitrary {
      for {
        authorisedLocationCode <- stringsWithMaxLength(stringMaxLength)
        controlResultDateLimit <- arbitrary[LocalDate]
      } yield GoodSummarySimplifiedDetails(authorisedLocationCode, controlResultDateLimit)
    }

  implicit lazy val arbitraryGoodSummaryNormalDetails: Arbitrary[GoodSummaryNormalDetails] =
    Arbitrary {
      for {
        customsApprovedLocation <- Gen.option(stringsWithMaxLength(stringMaxLength))
      } yield GoodSummaryNormalDetails(customsApprovedLocation)
    }

  implicit lazy val arbitraryGoodSummaryDetails: Arbitrary[GoodSummaryDetails] =
    Arbitrary {
      Gen.oneOf(arbitrary[GoodSummaryNormalDetails], arbitrary[GoodSummarySimplifiedDetails])
    }

  implicit def arbitraryGoodsSummary(implicit arbitraryGoodSummaryDetails: Arbitrary[GoodSummaryDetails]): Arbitrary[GoodsSummary] =
    Arbitrary {
      for {
        numberOfPackages   <- Gen.option(Gen.choose(1, 100))
        totalMass          <- Gen.choose(1, 100).map(_.toString)
        loadingPlace       <- Gen.option(stringsWithMaxLength(stringMaxLength)) // TODO: awaiting implementation
        goodSummaryDetails <- arbitraryGoodSummaryDetails.arbitrary
        sealNumbers        <- listWithMaxLength[SealDomain](10)
      } yield
        GoodsSummary(
          numberOfPackages,
          totalMass,
          None, // loadingPlace,
          goodSummaryDetails,
          sealNumbers
        )
    }
}
