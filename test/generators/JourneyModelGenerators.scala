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

package generators

import cats.data.NonEmptyList
import models.DeclarationType.Option2
import models._
import models.domain.{Address, SealDomain}
import models.journeyDomain.GoodsSummary.{
  GoodSummaryDetails,
  GoodSummaryNormalDetailsWithPreLodge,
  GoodSummaryNormalDetailsWithoutPreLodge,
  GoodSummarySimplifiedDetails
}
import models.journeyDomain.GuaranteeDetails.{GuaranteeOther, GuaranteeReference}
import models.journeyDomain.MovementDetails.{
  DeclarationForSelf,
  DeclarationForSomeoneElse,
  DeclarationForSomeoneElseAnswer,
  NormalMovementDetails,
  SimplifiedMovementDetails
}
import models.journeyDomain.Packages.{BulkPackages, OtherPackages, UnpackedPackages}
import models.journeyDomain.RouteDetails.TransitInformation
import models.journeyDomain.SafetyAndSecurity.SecurityTraderDetails
import models.journeyDomain.TransportDetails.DetailsAtBorder.{NewDetailsAtBorder, SameDetailsAtBorder}
import models.journeyDomain.TransportDetails.InlandMode.{Mode5or7, NonSpecialMode, Rail}
import models.journeyDomain.TransportDetails.ModeCrossingBorder.{ModeExemptNationality, ModeWithNationality}
import models.journeyDomain.TransportDetails.{DetailsAtBorder, InlandMode, ModeCrossingBorder}
import models.journeyDomain.addItems.{ItemsSecurityTraderDetails, SecurityPersonalInformation, SecurityTraderEori}
import models.journeyDomain.traderDetails.{TraderDetails, _}
import models.journeyDomain.{traderDetails, _}
import models.reference.{SpecialMention => _, _}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

import java.time.{LocalDate, LocalDateTime}

trait JourneyModelGenerators {
  self: Generators =>

  val maxNumberOfItemsLength = 2

  implicit def arbitraryJourneyDomain: Arbitrary[JourneyDomain] = Arbitrary(Gen.oneOf(arbitrarySimplifiedJourneyDomain, arbitraryNormalJourneyDomain))

  lazy val arbitrarySimplifiedJourneyDomain: Gen[JourneyDomain] =
    for {
      preTaskList <- arbitrary[PreTaskListDetails]
      simplifiedTaskList = preTaskList.copy(procedureType = ProcedureType.Simplified)
      movementDetails <- arbitrary[SimplifiedMovementDetails]
      isSecurityDetailsRequired = preTaskList.addSecurityDetails
      routeDetails      <- arbitraryRouteDetails(isSecurityDetailsRequired).arbitrary
      transportDetails  <- arbitrary[TransportDetails]
      traderDetails     <- genTraderDetailsSimplified
      safetyAndSecurity <- arbitrary[SafetyAndSecurity]
      itemDetails       <- genItemSection(movementDetails.containersUsed, isSecurityDetailsRequired, safetyAndSecurity, movementDetails, routeDetails)
      goodsSummary      <- arbitraryGoodsSummary(isSecurityDetailsRequired, ProcedureType.Simplified).arbitrary
      guarantees        <- nonEmptyListOf[GuaranteeDetails](3)
    } yield JourneyDomain(
      simplifiedTaskList,
      movementDetails,
      routeDetails,
      transportDetails,
      traderDetails,
      NonEmptyList(itemDetails, List(itemDetails)),
      goodsSummary,
      guarantees,
      if (isSecurityDetailsRequired) Some(safetyAndSecurity) else None,
      None
    )

  lazy val arbitraryNormalJourneyDomain: Gen[JourneyDomain] =
    for {
      preTaskList <- arbitrary[PreTaskListDetails]
      simplifiedTaskList = preTaskList.copy(procedureType = ProcedureType.Normal)
      movementDetails <- arbitrary[NormalMovementDetails]
      isSecurityDetailsRequired = preTaskList.addSecurityDetails
      routeDetails      <- arbitraryRouteDetails(isSecurityDetailsRequired).arbitrary
      transportDetails  <- arbitrary[TransportDetails]
      traderDetails     <- genTraderDetailsNormal
      safetyAndSecurity <- arbitrary[SafetyAndSecurity]
      itemDetails       <- genItemSection(movementDetails.containersUsed, isSecurityDetailsRequired, safetyAndSecurity, movementDetails, routeDetails)
      goodsSummary      <- arbitraryGoodsSummary(isSecurityDetailsRequired, ProcedureType.Normal).arbitrary
      guarantees        <- nonEmptyListOf[GuaranteeDetails](3)
    } yield JourneyDomain(
      simplifiedTaskList,
      movementDetails,
      routeDetails,
      transportDetails,
      traderDetails,
      NonEmptyList(itemDetails, List(itemDetails)),
      goodsSummary,
      guarantees,
      if (isSecurityDetailsRequired) Some(safetyAndSecurity) else None,
      None
    )

  implicit lazy val arbitrarySecurityDetails: Arbitrary[SafetyAndSecurity] = {

    val commonAddress = Arbitrary(arbitrary[CommonAddress].map(Address.prismAddressToCommonAddress.reverseGet))

    Arbitrary {
      for {
        addCircumstanceIndicator   <- Gen.some(nonEmptyString)
        paymentMethod              <- Gen.some(nonEmptyString)
        commercialReference        <- Gen.some(nonEmptyString)
        convenyanceReferenceNumber <- Gen.some(nonEmptyString)
        placeOfUnloading           <- Gen.some(nonEmptyString)
        consignorAddress           <- Gen.some(arbitrarySecurityTraderDetails(commonAddress).arbitrary)
        consigneeAddress           <- Gen.some(arbitrarySecurityTraderDetails(commonAddress).arbitrary)
        carrierAddress             <- Gen.some(arbitrarySecurityTraderDetails(commonAddress).arbitrary)
        itineraries                <- nonEmptyListOf[Itinerary](5)
      } yield {
        val placeOfUnloadingData = addCircumstanceIndicator match {
          case Some("E") => placeOfUnloading
          case _         => Some(placeOfUnloading.getOrElse("sampleData"))
        }
        SafetyAndSecurity(
          addCircumstanceIndicator,
          paymentMethod,
          commercialReference,
          convenyanceReferenceNumber,
          placeOfUnloadingData,
          consignorAddress,
          consigneeAddress,
          carrierAddress,
          itineraries
        )
      }
    }
  }

  implicit lazy val arbitraryItinerary: Arbitrary[Itinerary] =
    Arbitrary {
      for {
        countryCode <- arbitrary[CountryCode]
      } yield Itinerary(countryCode)
    }

  def genSecurityDetails(modeAtBorder: Gen[String]): Gen[SafetyAndSecurity] = {

    val commonAddress = Arbitrary(arbitrary[CommonAddress].map(Address.prismAddressToCommonAddress.reverseGet))

    val genConvenyanceReferenceNumber: Gen[Option[String]] = modeAtBorder.flatMap {
      case "4" | "40" => nonEmptyString.map(Some(_))
      case _          => Gen.some(nonEmptyString)
    }

    for {
      addCircumstanceIndicator   <- Gen.option(Gen.oneOf(CircumstanceIndicator.conditionalIndicators))
      paymentMethod              <- Gen.some(nonEmptyString)
      commercialReference        <- Gen.some(nonEmptyString)
      convenyanceReferenceNumber <- genConvenyanceReferenceNumber
      placeOfUnloading           <- Gen.some(nonEmptyString)
      consignorAddress           <- Gen.some(arbitrarySecurityTraderDetails(commonAddress).arbitrary)
      consigneeAddress           <- Gen.some(arbitrarySecurityTraderDetails(commonAddress).arbitrary)
      carrierAddress             <- Gen.some(arbitrarySecurityTraderDetails(commonAddress).arbitrary)
      itineraries                <- nonEmptyListOf[Itinerary](5)
    } yield {
      val placeOfUnloadingData = addCircumstanceIndicator match {
        case Some("E") => placeOfUnloading
        case _         => Some(placeOfUnloading.getOrElse("sampleData"))
      }

      SafetyAndSecurity(
        addCircumstanceIndicator,
        paymentMethod,
        commercialReference,
        convenyanceReferenceNumber,
        placeOfUnloadingData,
        consignorAddress,
        consigneeAddress,
        carrierAddress,
        itineraries
      )
    }
  }

  implicit def arbitrarySecurityTraderDetails(implicit arbAddress: Arbitrary[Address]): Arbitrary[SecurityTraderDetails] =
    Arbitrary(Gen.oneOf(Arbitrary.arbitrary[SafetyAndSecurity.PersonalInformation], Arbitrary.arbitrary[SafetyAndSecurity.TraderEori]))

  implicit lazy val arbitrarySafetyAndSecurityTraderEori: Arbitrary[SafetyAndSecurity.TraderEori] =
    Arbitrary(Arbitrary.arbitrary[EoriNumber].map(SafetyAndSecurity.TraderEori))

  implicit def arbitrarySafetyAndSecurityPersonalInformation(implicit arbAddress: Arbitrary[Address]): Arbitrary[SafetyAndSecurity.PersonalInformation] =
    Arbitrary {
      for {
        name    <- stringsWithMaxLength(stringMaxLength)
        address <- arbAddress.arbitrary
      } yield SafetyAndSecurity.PersonalInformation(name, Address(address.line1, "", "", Some(Country(CountryCode("GB"), ""))))
    }

  implicit lazy val arbitraryModeCrossingBorder: Arbitrary[ModeCrossingBorder] =
    Arbitrary(
      Gen.oneOf(
        arbitrary[ModeExemptNationality],
        arbitrary[ModeWithNationality]
      )
    )

  implicit lazy val arbitraryModeExemptNationality: Arbitrary[ModeExemptNationality] =
    Arbitrary {
      for {
        codeMode <- genExemptNationalityCode
      } yield ModeExemptNationality(codeMode)
    }

  implicit lazy val arbitraryModeWithNationality: Arbitrary[ModeWithNationality] =
    Arbitrary {
      for {
        cc <- arbitrary[CountryCode]
        codeMode <- arbitrary[Int].suchThat(
          num => !ModeCrossingBorder.isExemptFromNationality(num.toString)
        )
        idCrossing <- stringsWithMaxLength(stringMaxLength)
      } yield ModeWithNationality(cc, codeMode, idCrossing)
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
        modeCrossingBorder <- arbitrary[ModeCrossingBorder]
      } yield NewDetailsAtBorder(
        mode,
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
        code        <- Gen.oneOf(Rail.Constants.codes)
        departureId <- Gen.some(stringsWithMaxLength(stringMaxLength))
      } yield Rail(code, departureId)
    }

  implicit lazy val arbitraryMode5or7: Arbitrary[Mode5or7] =
    Arbitrary {
      for {
        code <- Gen.oneOf(Mode5or7.Constants.codes)
      } yield Mode5or7(code)
    }

  implicit lazy val arbitraryNonSpecialMode: Arbitrary[NonSpecialMode] =
    Arbitrary {
      for {
        code                   <- Gen.const(42)
        nationalityAtDeparture <- arbitrary[CountryCode]
        departureId            <- Gen.some(stringsWithMaxLength(stringMaxLength))
      } yield NonSpecialMode(
        code,
        Some(nationalityAtDeparture),
        departureId
      )
    }

  implicit lazy val arbitraryTransportDetails: Arbitrary[TransportDetails] =
    Arbitrary {
      for {
        inlandMode      <- arbitrary[InlandMode]
        detailsAtBorder <- arbitrary[DetailsAtBorder]
      } yield TransportDetails(
        inlandMode,
        detailsAtBorder
      )
    }

  lazy val genTraderDetailsSimplified: Gen[TraderDetails] =
    for {
      principalTraderDetails <- arbitrary[PrincipalTraderEoriInfo]
      consignor              <- Gen.option(arbitrary[ConsignorDetails])
      consignee              <- Gen.option(arbitrary[ConsigneeDetails])
    } yield traderDetails.TraderDetails(principalTraderDetails, consignor, consignee)

  lazy val genTraderDetailsNormal: Gen[TraderDetails] =
    for {
      principalTraderDetails <- arbitrary[PrincipalTraderDetails]
      consignor              <- Gen.some(arbitrary[ConsignorDetails])
      consignee              <- Gen.some(arbitrary[ConsigneeDetails])
    } yield TraderDetails(principalTraderDetails, consignor, consignee)

  implicit val arbitraryItemSecurityTraderDetails: Arbitrary[ItemsSecurityTraderDetails] = {
    val commonAddress = Arbitrary(arbitrary[CommonAddress].map(Address.prismAddressToCommonAddress.reverseGet))

    Arbitrary {
      for {
        methodOfPayment           <- Gen.some(nonEmptyString)
        commercialReferenceNumber <- Gen.some(nonEmptyString)
        dangerousGoodsCode        <- Gen.some(nonEmptyString)
        consignor                 <- Gen.some(arbitraryItemsSecurityTraderDetails(commonAddress).arbitrary)
        consignee                 <- Gen.some(arbitraryItemsSecurityTraderDetails(commonAddress).arbitrary)
      } yield ItemsSecurityTraderDetails(methodOfPayment, commercialReferenceNumber, dangerousGoodsCode, consignor, consignee)
    }
  }

  implicit def arbitraryItemsSecurityTraderDetails(implicit arbAddress: Arbitrary[Address]): Arbitrary[models.journeyDomain.addItems.SecurityTraderDetails] =
    Arbitrary(Gen.oneOf(Arbitrary.arbitrary[SecurityPersonalInformation], Arbitrary.arbitrary[SecurityTraderEori]))

  implicit lazy val arbitraryItemsSecurityTraderEori: Arbitrary[SecurityTraderEori] =
    Arbitrary(Arbitrary.arbitrary[EoriNumber].map(SecurityTraderEori(_)))

  implicit def arbitraryItemsSecurityPersonalInformation(implicit arbAddress: Arbitrary[Address]): Arbitrary[SecurityPersonalInformation] =
    Arbitrary {
      for {
        name    <- stringsWithMaxLength(stringMaxLength)
        address <- arbAddress.arbitrary
      } yield SecurityPersonalInformation(name, address)
    }

  implicit lazy val arbitraryTraderEori: Arbitrary[PrincipalTraderEoriInfo] =
    Arbitrary(
      arbitrary[EoriNumber].map(
        x => PrincipalTraderEoriInfo(EoriNumber(s"GB${x.value}"))
      )
    )

  implicit lazy val arbitraryPrincipalTraderPersonalInfo: Arbitrary[PrincipalTraderPersonalInfo] =
    Arbitrary {
      for {
        name             <- stringsWithMaxLength(stringMaxLength)
        principalAddress <- arbitrary[CommonAddress]
        address = Address.prismAddressToCommonAddress.reverseGet(principalAddress)
      } yield PrincipalTraderPersonalInfo(name, address)
    }

  implicit lazy val arbitraryPrincipalEoriTraderPersonalInfo: Arbitrary[PrincipalTraderEoriPersonalInfo] =
    Arbitrary {
      for {
        eori             <- arbitrary[EoriNumber]
        name             <- stringsWithMaxLength(stringMaxLength)
        principalAddress <- arbitrary[CommonAddress]
        address = Address.prismAddressToCommonAddress.reverseGet(principalAddress)
      } yield PrincipalTraderEoriPersonalInfo(eori, name, address)
    }

  implicit lazy val arbitraryRequiredDetails: Arbitrary[PrincipalTraderDetails] =
    Arbitrary {
      Gen.oneOf(Arbitrary.arbitrary[PrincipalTraderPersonalInfo],
                Arbitrary.arbitrary[PrincipalTraderEoriInfo],
                Arbitrary.arbitrary[PrincipalTraderEoriPersonalInfo]
      )
    }

  val genConsignorDetailsWithEori: Gen[ConsignorDetails] =
    for {
      name          <- stringsWithMaxLength(stringMaxLength)
      commonAddress <- arbitrary[CommonAddress]
      eori          <- arbitrary[EoriNumber]
      address = Address.prismAddressToCommonAddress.reverseGet(commonAddress)
    } yield ConsignorDetails(name, address, Some(eori))

  val genConsignorDetailsWithoutEori: Gen[ConsignorDetails] =
    for {
      name          <- stringsWithMaxLength(stringMaxLength)
      commonAddress <- arbitrary[CommonAddress]
      address = Address.prismAddressToCommonAddress.reverseGet(commonAddress)
    } yield ConsignorDetails(name, address, None)

  implicit def arbitraryConsignorDetails: Arbitrary[ConsignorDetails] =
    Arbitrary {
      Gen.oneOf(genConsignorDetailsWithEori, genConsignorDetailsWithoutEori)
    }

  val genConsigneeDetailsWithEori: Gen[ConsigneeDetails] =
    for {
      name          <- stringsWithMaxLength(stringMaxLength)
      commonAddress <- arbitrary[CommonAddress]
      eori          <- arbitrary[EoriNumber]
      address = Address.prismAddressToCommonAddress.reverseGet(commonAddress)
    } yield ConsigneeDetails(name, address, Some(eori))

  val genConsigneeDetailsWithoutEori: Gen[ConsigneeDetails] =
    for {
      name          <- stringsWithMaxLength(stringMaxLength)
      commonAddress <- arbitrary[CommonAddress]
      address = Address.prismAddressToCommonAddress.reverseGet(commonAddress)
    } yield ConsigneeDetails(name, address, None)

  implicit lazy val arbitraryConsigneeDetails: Arbitrary[ConsigneeDetails] =
    Arbitrary {
      Gen.oneOf(genConsigneeDetailsWithEori, genConsigneeDetailsWithoutEori)
    }

  implicit def arbitraryItemRequiredDetails(implicit arbAddress: Arbitrary[Address]): Arbitrary[models.journeyDomain.ItemTraderDetails.RequiredDetails] =
    Arbitrary {
      for {
        name    <- stringsWithMaxLength(stringMaxLength)
        address <- arbAddress.arbitrary
        eori    <- Gen.some(arbitrary[EoriNumber])
      } yield models.journeyDomain.ItemTraderDetails.RequiredDetails(name, address, eori)
    }

  implicit def arbitraryItemSection: Arbitrary[ItemSection] =
    Arbitrary {
      for {
        containersUsed            <- arbitrary[Boolean]
        isSecurityDetailsRequired <- arbitrary[Boolean]
        addDocument               <- arbitrary[Boolean]
        otherIndicator            <- nonEmptyString
        circumstanceIndicator <-
          if (isSecurityDetailsRequired) { Gen.oneOf(CircumstanceIndicator.conditionalIndicators :+ otherIndicator).map(Some(_)) }
          else
            Gen.const(None)
        itemSection <- genItemSectionOld(containersUsed, addDocument, circumstanceIndicator)
      } yield itemSection
    }

  def genItemSection(
    containersUsed: Boolean,
    addSafetyAndSecurity: Boolean,
    safetyAndSecurity: SafetyAndSecurity,
    movementDetails: MovementDetails,
    routeDetails: RouteDetails
  ): Gen[ItemSection] = {
    val commonAddress = Arbitrary(arbitrary[CommonAddress].map(Address.prismAddressToCommonAddress.reverseGet))

    val isDocumentTypeMandatory = addSafetyAndSecurity &&
      safetyAndSecurity.commercialReferenceNumber.isDefined

    val isPreviousReferenceMandatory: Boolean = (movementDetails.declarationType, routeDetails.countryOfDispatch) match {
      case (Option2, CountryOfDispatch(_, true)) => true
      case _                                     => false
    }

    for {
      itemDetail    <- arbitrary[ItemDetails]
      itemConsignor <- Gen.some(arbitraryItemRequiredDetails(commonAddress).arbitrary)
      itemConsignee <- Gen.some(arbitraryItemRequiredDetails(commonAddress).arbitrary)
      packages      <- nonEmptyListOf[Packages](1)
      containers <-
        if (containersUsed) { nonEmptyListOf[Container](1).map(Some(_)) }
        else Gen.const(None)
      specialMentions <- Gen.some(nonEmptyListOf[SpecialMentionDomain](1))
      producedDocuments <-
        if (isDocumentTypeMandatory) { nonEmptyListOf[ProducedDocument](1).map(Some(_)) }
        else Gen.const(None)
      methodOfPayment           <- arbitrary[String]
      commercialReferenceNumber <- arbitrary[String]
      previousReferences        <- Gen.some(nonEmptyListOf[PreviousReferences](1))
      itemSecurityTraderDetails <-
        if (addSafetyAndSecurity) arbitrary[ItemsSecurityTraderDetails].map {
          itemsSecurityTraderDetails =>
            val setMethodOfPayment = safetyAndSecurity.paymentMethod match {
              case None    => Some(methodOfPayment)
              case Some(_) => None
            }

            val setCommercialReferenceNumber = safetyAndSecurity.commercialReferenceNumber match {
              case None    => Some(commercialReferenceNumber)
              case Some(_) => None
            }

            Some(itemsSecurityTraderDetails.copy(methodOfPayment = setMethodOfPayment, commercialReferenceNumber = setCommercialReferenceNumber))
        }
        else Gen.const(None)
    } yield ItemSection(itemDetail,
                        itemConsignor,
                        itemConsignee,
                        packages,
                        containers,
                        specialMentions,
                        producedDocuments,
                        itemSecurityTraderDetails,
                        previousReferences
    )
  }

  def genItemSectionOld(
    containersUsed: Boolean = false,
    addDocument: Boolean = false,
    circumstanceIndicator: Option[String] = None
  ): Gen[ItemSection] = {

    val commonAddress = Arbitrary(arbitrary[CommonAddress].map(Address.prismAddressToCommonAddress.reverseGet))

    val documentTypeIsMandatory = circumstanceIndicator.fold(addDocument)(CircumstanceIndicator.conditionalIndicators.contains(_))

    for {
      itemDetail    <- arbitrary[ItemDetails]
      itemConsignor <- Gen.some(arbitraryItemRequiredDetails(commonAddress).arbitrary)
      itemConsignee <- Gen.some(arbitraryItemRequiredDetails(commonAddress).arbitrary)
      packages      <- nonEmptyListOf[Packages](1)
      containers <-
        if (containersUsed) { nonEmptyListOf[Container](1).map(Some(_)) }
        else Gen.const(None)
      specialMentions <- Gen.some(nonEmptyListOf[SpecialMentionDomain](1))
      producedDocuments <-
        if (documentTypeIsMandatory) { nonEmptyListOf[ProducedDocument](1).map(Some(_)) }
        else Gen.const(None)
      previousReferences <- Gen.some(nonEmptyListOf[PreviousReferences](1))
      itemSecurityTraderDetails <-
        if (documentTypeIsMandatory) { Gen.some(arbitrary[ItemsSecurityTraderDetails]) }
        else Gen.const(None)

    } yield ItemSection(itemDetail,
                        itemConsignor,
                        itemConsignee,
                        packages,
                        containers,
                        specialMentions,
                        producedDocuments,
                        itemSecurityTraderDetails,
                        previousReferences
    )
  }

  implicit lazy val arbitraryPreTaskListDetails: Arbitrary[PreTaskListDetails] =
    Arbitrary {
      for {
        lrn                <- arbitrary[LocalReferenceNumber]
        procedureType      <- arbitrary[ProcedureType]
        officeOfDeparture  <- arbitrary[CustomsOffice]
        addSecurityDetails <- arbitrary[Boolean]
      } yield PreTaskListDetails(lrn, procedureType, officeOfDeparture, addSecurityDetails)
    }

  implicit lazy val arbitraryGuaranteeDetails: Arbitrary[GuaranteeDetails] =
    Arbitrary(Gen.oneOf(arbitrary[GuaranteeReference], arbitrary[GuaranteeOther]))

  implicit lazy val arbitraryGuaranteeOther: Arbitrary[GuaranteeOther] =
    Arbitrary {
      for {
        guaranteeType  <- Gen.oneOf(GuaranteeType.nonGuaranteeReferenceRoute)
        otherReference <- nonEmptyString
      } yield GuaranteeOther(guaranteeType, otherReference)
    }

  implicit lazy val arbitraryLiabilityAmount: Arbitrary[LiabilityAmount] =
    Arbitrary {
      for {
        amount   <- nonEmptyString
        currency <- Gen.const(CurrencyCode.GBP)
      } yield OtherLiabilityAmount(amount, currency)
    }

  implicit lazy val arbitraryGuaranteeReference: Arbitrary[GuaranteeReference] =
    Arbitrary {
      for {
        guaranteeType            <- Gen.oneOf(GuaranteeType.guaranteeReferenceRoute)
        guaranteeReferenceNumber <- nonEmptyString
        liabilityAmount          <- Arbitrary.arbitrary[LiabilityAmount]
        accessCode               <- nonEmptyString
      } yield GuaranteeReference(guaranteeType, guaranteeReferenceNumber, liabilityAmount, accessCode)
    }

  implicit lazy val arbitraryPackages: Arbitrary[Packages] =
    Arbitrary(Gen.oneOf(arbitrary[UnpackedPackages], arbitrary[BulkPackages], arbitrary[OtherPackages]))

  implicit lazy val arbitraryUnpackedPackages: Arbitrary[UnpackedPackages] =
    Arbitrary {
      for {
        packageType  <- arbitraryUnPackedPackageType.arbitrary
        totalPieces  <- Gen.choose(1, 10)
        markOrNumber <- Gen.some(nonEmptyString)
      } yield UnpackedPackages(packageType, totalPieces, markOrNumber)
    }

  implicit lazy val arbitraryBulkPackage: Arbitrary[BulkPackages] =
    Arbitrary {
      for {
        bulkPackage  <- arbitraryBulkPackageType.arbitrary
        markOrNumber <- Gen.some(nonEmptyString)
      } yield BulkPackages(bulkPackage, markOrNumber)
    }

  implicit lazy val arbitraryOtherPackage: Arbitrary[OtherPackages] =
    Arbitrary {
      for {
        code                <- nonEmptyString
        description         <- nonEmptyString
        howManyPackagesPage <- Gen.choose(1, 10)
        markOrNumber        <- nonEmptyString
      } yield OtherPackages(PackageType(code, description), howManyPackagesPage, markOrNumber)
    }

  implicit lazy val arbitraryItemDetails: Arbitrary[ItemDetails] =
    Arbitrary {
      for {
        itemDescription <- nonEmptyString
        totalGrossMass  <- genNumberString
        totalNetMass    <- Gen.some(genNumberString)
        commodityCode   <- Gen.some(nonEmptyString)
      } yield ItemDetails(itemDescription, totalGrossMass, totalNetMass, commodityCode)
    }

  implicit lazy val arbitrarySpecialMention: Arbitrary[SpecialMentionDomain] =
    Arbitrary {
      for {
        specialMentionType <- nonEmptyString
        additionalInfo     <- nonEmptyString
      } yield SpecialMentionDomain(specialMentionType, additionalInfo)
    }

  implicit lazy val arbitraryProducedDocument: Arbitrary[ProducedDocument] =
    Arbitrary {
      for {
        documentType      <- nonEmptyString
        documentReference <- nonEmptyString
        extraInformation  <- Gen.some(nonEmptyString)
      } yield ProducedDocument(documentType, documentReference, extraInformation)
    }

  implicit lazy val arbitraryContainer: Arbitrary[Container] =
    Arbitrary {
      for {
        containerNumber <- nonEmptyString
      } yield Container(containerNumber)
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
      } yield SimplifiedMovementDetails(
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
      } yield MovementDetails.NormalMovementDetails(
        declarationType,
        preLodge,
        containersUsed,
        declarationPlacePage,
        declarationForSomeoneElse
      )
    }

  implicit def arbitraryMovementDetails(procedureType: ProcedureType): Arbitrary[MovementDetails] =
    if (procedureType == ProcedureType.Normal) {
      Arbitrary(arbitrary[NormalMovementDetails])
    } else {
      Arbitrary(arbitrary[SimplifiedMovementDetails])
    }

  val genTransitInformationWithoutArrivalTime =
    for {
      transitOffice <- stringsWithMaxLength(stringMaxLength)
    } yield TransitInformation(
      transitOffice,
      None
    )

  val genTransitInformationWithArrivalTime =
    for {
      transitOffice <- stringsWithMaxLength(stringMaxLength)
      arrivalTime   <- arbitrary[LocalDateTime]
    } yield TransitInformation(
      transitOffice,
      Some(arrivalTime)
    )

  implicit lazy val arbitraryTransitInformation: Arbitrary[TransitInformation] =
    Arbitrary(Gen.oneOf(genTransitInformationWithoutArrivalTime, genTransitInformationWithArrivalTime))

  // TODO: refactor this. Remove parameter, make all transit informations consistent with security flag and create generator.
  implicit def arbitraryRouteDetails(safetyAndSecurityFlag: Boolean): Arbitrary[RouteDetails] =
    Arbitrary {
      for {
        countryOfDispatch  <- arbitrary[CountryOfDispatch]
        destinationCountry <- arbitrary[CountryCode]
        destinationOffice  <- arbitrary[CustomsOffice]
        transitInformation <- transitInformation(safetyAndSecurityFlag)
      } yield RouteDetails(
        countryOfDispatch,
        destinationCountry,
        destinationOffice,
        transitInformation
      )
    }

  private def transitInformation(safetyAndSecurityFlag: Boolean): Gen[NonEmptyList[TransitInformation]] =
    for {
      transitInformation <- Gen.listOfN(2, arbitrary[TransitInformation])
      dateTime           <- arbitrary[LocalDateTime]
      updatedTransitInformation = {
        if (safetyAndSecurityFlag) {
          transitInformation.map(_.copy(arrivalTime = Some(dateTime)))
        } else {
          transitInformation.map(_.copy(arrivalTime = None))
        }
      }
    } yield NonEmptyList(updatedTransitInformation.head, updatedTransitInformation.tail)

  implicit lazy val arbitraryGoodSummarySimplifiedDetails: Arbitrary[GoodSummarySimplifiedDetails] =
    Arbitrary {
      for {
        authorisedLocationCode <- stringsWithMaxLength(stringMaxLength)
        controlResultDateLimit <- arbitrary[LocalDate]
      } yield GoodSummarySimplifiedDetails(authorisedLocationCode, controlResultDateLimit)
    }

  implicit lazy val arbitraryGoodSummaryNormalDetailsWithoutPreLodge: Arbitrary[GoodSummaryNormalDetailsWithoutPreLodge] =
    Arbitrary {
      for {
        customsApprovedLocation <- Gen.some(stringsWithMaxLength(stringMaxLength))
        agreedLocationOfGoods   <- Gen.some(stringsWithMaxLength(stringMaxLength))
      } yield GoodSummaryNormalDetailsWithoutPreLodge(agreedLocationOfGoods, customsApprovedLocation)
    }

  implicit lazy val arbitraryGoodSummaryNormalDetailsWithPrelodge: Arbitrary[GoodSummaryNormalDetailsWithPreLodge] =
    Arbitrary {
      for {
        customsApprovedLocation <- Gen.some(stringsWithMaxLength(stringMaxLength))
      } yield GoodSummaryNormalDetailsWithPreLodge(customsApprovedLocation)
    }

  implicit def arbitraryGoodSummaryDetails(procedureType: ProcedureType): Arbitrary[GoodSummaryDetails] =
    Arbitrary {
      if (procedureType == ProcedureType.Normal) {
        Gen.oneOf(arbitrary[GoodSummaryNormalDetailsWithPreLodge], arbitrary[GoodSummaryNormalDetailsWithoutPreLodge])
      } else {
        arbitrary[GoodSummarySimplifiedDetails]
      }
    }

  implicit def arbitraryGoodsSummary(safetyAndSecurity: Boolean, procedureType: ProcedureType): Arbitrary[GoodsSummary] =
    Arbitrary {
      for {
        loadingPlace <-
          if (safetyAndSecurity) { nonEmptyString.map(Some(_)) }
          else { Gen.const(None) }
        numberOfPackages   <- Gen.choose(1, 100)
        totalMass          <- Gen.choose(1, 100).map(_.toString)
        goodSummaryDetails <- arbitraryGoodSummaryDetails(procedureType).arbitrary
        sealNumbers        <- listWithMaxLength[SealDomain](10)
      } yield GoodsSummary(
        numberOfPackages,
        loadingPlace,
        goodSummaryDetails,
        sealNumbers
      )
    }

  implicit lazy val arbitraryPreviousReference: Arbitrary[PreviousReferences] =
    Arbitrary {
      for {
        referenceType     <- nonEmptyString
        previousReference <- nonEmptyString
        extraInformation  <- Gen.some(nonEmptyString)
      } yield PreviousReferences(referenceType, previousReference, extraInformation)
    }

}
