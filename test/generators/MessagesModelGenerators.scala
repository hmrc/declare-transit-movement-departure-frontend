/*
 * Copyright 2022 HM Revenue & Customs
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

import models.messages._
import models.messages.customsoffice.{CustomsOffice, CustomsOfficeDeparture, CustomsOfficeDestination, CustomsOfficeTransit}
import models.messages.goodsitem._
import models.messages.guarantee.{Guarantee, GuaranteeReference, GuaranteeReferenceWithGrn, GuaranteeReferenceWithOther}
import models.messages.header.{Header, Transport}
import models.messages.safetyAndSecurity._
import models.messages.trader._
import models.reference.CountryCode
import models.{InvalidGuaranteeCode, InvalidGuaranteeReasonCode, LocalReferenceNumber}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen.{alphaNumChar, choose}
import org.scalacheck.{Arbitrary, Gen}
import utils.Format.dateFormatted

import java.time.{LocalDate, LocalTime}

trait MessagesModelGenerators extends ModelGenerators with Generators {

  implicit lazy val arbitrarySafetyAndSecurityCarrier: Arbitrary[SafetyAndSecurityCarrier] = Arbitrary {
    Gen.oneOf[SafetyAndSecurityCarrier](arbitrary[SafetyAndSecurityCarrierWithEori], arbitrary[SafetyAndSecurityCarrierWithoutEori])
  }

  implicit lazy val securityCarrierEori: Arbitrary[SafetyAndSecurityCarrierWithEori] = Arbitrary {
    for {
      eori <- arbitrary[String]
    } yield SafetyAndSecurityCarrierWithEori(eori)
  }

  implicit lazy val securityCarrierNoEori: Arbitrary[SafetyAndSecurityCarrierWithoutEori] = Arbitrary {
    for {
      name            <- arbitrary[String]
      streetAndNumber <- arbitrary[String]
      postcode        <- arbitrary[String]
      city            <- arbitrary[String]
      countryCode     <- arbitrary[String]
    } yield SafetyAndSecurityCarrierWithoutEori(name, streetAndNumber, postcode, city, countryCode)
  }

  implicit lazy val arbitrarySafetyAndSecurityConsignee: Arbitrary[SafetyAndSecurityConsignee] = Arbitrary {
    Gen.oneOf[SafetyAndSecurityConsignee](arbitrary[SafetyAndSecurityConsigneeWithEori], arbitrary[SafetyAndSecurityConsigneeWithoutEori])
  }

  implicit lazy val securityConsigneeEori: Arbitrary[SafetyAndSecurityConsigneeWithEori] = Arbitrary {
    for {
      eori <- arbitrary[String]
    } yield SafetyAndSecurityConsigneeWithEori(eori)
  }

  implicit lazy val securityConsigneeNoEori: Arbitrary[SafetyAndSecurityConsigneeWithoutEori] = Arbitrary {
    for {
      name            <- arbitrary[String]
      streetAndNumber <- arbitrary[String]
      postcode        <- arbitrary[String]
      city            <- arbitrary[String]
      countryCode     <- arbitrary[String]
    } yield SafetyAndSecurityConsigneeWithoutEori(name, streetAndNumber, postcode, city, countryCode)
  }

  implicit lazy val arbitrarySafetyAndSecurityConsignor: Arbitrary[SafetyAndSecurityConsignor] = Arbitrary {
    Gen.oneOf[SafetyAndSecurityConsignor](arbitrary[SafetyAndSecurityConsignorWithEori], arbitrary[SafetyAndSecurityConsignorWithoutEori])
  }

  implicit lazy val securityConsignorEori: Arbitrary[SafetyAndSecurityConsignorWithEori] = Arbitrary {
    for {
      eori <- arbitrary[String]
    } yield SafetyAndSecurityConsignorWithEori(eori)
  }

  implicit lazy val securityConsignorNoEori: Arbitrary[SafetyAndSecurityConsignorWithoutEori] = Arbitrary {
    for {
      name            <- arbitrary[String]
      streetAndNumber <- arbitrary[String]
      postcode        <- arbitrary[String]
      city            <- arbitrary[String]
      countryCode     <- arbitrary[String]
    } yield SafetyAndSecurityConsignorWithoutEori(name, streetAndNumber, postcode, city, countryCode)
  }

  implicit lazy val arbitraryItinerary: Arbitrary[Itinerary] =
    Arbitrary {
      for {
        country <- arbitrary[CountryCode]
      } yield Itinerary(country.code)
    }

  implicit lazy val arbitraryInterchangeControlReference: Arbitrary[InterchangeControlReference] =
    Arbitrary {
      for {
        date  <- localDateGen
        index <- Gen.posNum[Int]
      } yield InterchangeControlReference(dateFormatted(date), index)
    }

  implicit lazy val arbitraryMeta: Arbitrary[Meta] =
    Arbitrary {
      for {
        interchangeControlReference <- arbitrary[InterchangeControlReference]
        date                        <- arbitrary[LocalDate]
        time                        <- arbitrary[LocalTime]
      } yield Meta(
        interchangeControlReference,
        date,
        LocalTime.of(time.getHour, time.getMinute),
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None
      )
    }

  implicit lazy val arbitraryCustomsOfficeTransit: Arbitrary[CustomsOfficeTransit] =
    Arbitrary {
      for {
        customsOffice   <- Gen.pick(CustomsOffice.Constants.length, 'A' to 'Z')
        arrivalDateTime <- Gen.option(arbitrary(arbitraryLocalDateTime))
      } yield CustomsOfficeTransit(customsOffice.mkString, arrivalDateTime)
    }

  implicit lazy val arbitrarySecurityDetailsSubmission: Arbitrary[SecurityDetailsSubmission] =
    Arbitrary {
      for {
        transportMethodOfPayment  <- Gen.option(arbitrary[String])
        commercialReferenceNumber <- Gen.option(arbitrary[String])
        unDangerouGoodsCode       <- Gen.option(arbitrary[String])
      } yield SecurityDetailsSubmission(transportMethodOfPayment, commercialReferenceNumber, unDangerouGoodsCode)
    }

  implicit lazy val arbitraryDeclarationRequest: Arbitrary[DeclarationRequest] =
    Arbitrary {
      for {
        meta                      <- arbitrary[Meta]
        header                    <- arbitrary[Header]
        traderPrinciple           <- Gen.oneOf(arbitrary[TraderPrincipalWithEori], arbitrary[TraderPrincipalWithoutEori])
        traderConsignor           <- Gen.option(arbitrary[TraderConsignor])
        traderConsignee           <- Gen.option(arbitrary[TraderConsignee])
        traderAuthorisedConsignee <- Gen.option(arbitrary[TraderAuthorisedConsignee])
        customsOfficeDeparture    <- Gen.pick(CustomsOffice.Constants.length, 'A' to 'Z')
        customsOfficeTransit      <- listWithMaxLength[CustomsOfficeTransit](CustomsOffice.Constants.transitOfficeCount)
        customsOfficeDestination  <- Gen.pick(CustomsOffice.Constants.length, 'A' to 'Z')
        controlResult             <- Gen.option(arbitrary[ControlResult])
        representative            <- Gen.option(arbitrary[Representative])
        seals                     <- Gen.option(arbitrary[Seals])
        guarantees                <- nonEmptyListWithMaxSize(10, arbitrary[Guarantee])
        goodsItems                <- nonEmptyListWithMaxSize(10, arbitrary[GoodsItem])
        itinerary                 <- listWithMaxLength[Itinerary](10)
      } yield DeclarationRequest(
        meta,
        header,
        traderPrinciple,
        traderConsignor,
        traderConsignee,
        traderAuthorisedConsignee,
        CustomsOfficeDeparture(customsOfficeDeparture.mkString),
        customsOfficeTransit,
        CustomsOfficeDestination(customsOfficeDestination.mkString),
        controlResult,
        representative,
        seals,
        guarantees,
        goodsItems,
        itinerary,
        None,
        None,
        None
      )
    }

  implicit lazy val arbitraryTransport: Arbitrary[Transport] =
    Arbitrary {
      for {
        inlTraModHEA75        <- Gen.option(choose(min = 1: Int, 99: Int))
        traModAtBorHEA76      <- Gen.option(nonEmptyString)
        ideOfMeaOfTraAtDHEA78 <- Gen.option(stringsWithMaxLength(Transport.Constants.identityMeansOfTransport, alphaNumChar))
        natOfMeaOfTraAtDHEA80 <- Gen.option(stringsWithMaxLength(Header.Constants.countryLength, alphaNumChar))
        ideOfMeaOfTraCroHEA85 <- Gen.option(stringsWithMaxLength(Transport.Constants.identityMeansOfTransport, alphaNumChar))
        natOfMeaOfTraCroHEA87 <- Gen.option(stringsWithMaxLength(Header.Constants.countryLength, alphaNumChar))
        typOfMeaOfTraCroHEA88 <- Gen.option(choose(min = 1: Int, 99: Int))
      } yield Transport(
        inlTraModHEA75,
        traModAtBorHEA76,
        ideOfMeaOfTraAtDHEA78,
        natOfMeaOfTraAtDHEA80.map(_.mkString),
        ideOfMeaOfTraCroHEA85,
        natOfMeaOfTraCroHEA87.map(_.mkString),
        typOfMeaOfTraCroHEA88
      )
    }

  implicit lazy val arbitraryHeader: Arbitrary[Header] =
    Arbitrary {
      for {
        refNumHEA4          <- arbitrary[LocalReferenceNumber].map(_.toString())
        typOfDecHEA24       <- Gen.pick(Header.Constants.typeOfDeclarationLength, 'A' to 'Z')
        couOfDesCodHEA30    <- Gen.option(stringsWithMaxLength(Header.Constants.countryLength, alphaNumChar))
        agrLocOfGooCodHEA38 <- Gen.option(stringsWithMaxLength(Header.Constants.agreedLocationOfGoodsCodeLength, alphaNumChar))
        agrLocOfGooHEA39    <- Gen.option(stringsWithMaxLength(Header.Constants.agreedLocationOfGoodsLength, alphaNumChar))
        autLocOfGooCodHEA41 <- Gen.option(stringsWithMaxLength(Header.Constants.authorisedLocationOfGoodsCodeLength, alphaNumChar))
        plaOfLoaCodHEA46    <- Gen.option(stringsWithMaxLength(Header.Constants.placeOfLoadingGoodsCodeLength, alphaNumChar))
        couOfDisCodHEA55    <- Gen.option(stringsWithMaxLength(Header.Constants.countryLength, alphaNumChar))
        cusSubPlaHEA66      <- Gen.option(stringsWithMaxLength(Header.Constants.customsSubPlaceLength, alphaNumChar))
        transportDetails    <- arbitrary[Transport]
        conIndHEA96         <- choose(min = 0: Int, 1: Int)
        totNumOfIteHEA305   <- choose(min = 1: Int, 100: Int)
        totNumOfPacHEA306   <- Gen.choose(min = 1: Int, 100: Int)
        grossMass           <- Gen.choose(0.0, 99999999.999).map(BigDecimal(_).bigDecimal.setScale(3, BigDecimal.RoundingMode.DOWN))
        decDatHEA383        <- arbitrary[LocalDate]
        decPlaHEA394        <- stringsWithMaxLength(Header.Constants.declarationPlace, alphaNumChar)
        speCirIndHEA1       <- Gen.option(Gen.pick(Header.Constants.specificCircumstanceIndicatorLength, 'A' to 'Z').map(_.mkString))
        traChaMetOfPayHEA1  <- Gen.option(Gen.pick(Header.Constants.methodOfPaymentLength, 'A' to 'Z').map(_.mkString))
        comRefNumHEA        <- Gen.option(stringsWithMaxLength(Header.Constants.commercialReferenceNumberLength, alphaNumChar))
        secHEA358           <- Gen.option(choose(min = 0: Int, 9: Int))
        conRefNumHEA        <- Gen.option(stringsWithMaxLength(Header.Constants.conveyanceReferenceNumberLength, alphaNumChar))
        codPlUnHEA357       <- Gen.option(stringsWithMaxLength(Header.Constants.placeOfUnloadingCodeLength, alphaNumChar))
      } yield Header(
        refNumHEA4,
        typOfDecHEA24.mkString,
        couOfDesCodHEA30.map(_.mkString),
        agrLocOfGooCodHEA38,
        agrLocOfGooHEA39,
        autLocOfGooCodHEA41,
        plaOfLoaCodHEA46,
        couOfDisCodHEA55.map(_.mkString),
        cusSubPlaHEA66,
        transportDetails,
        conIndHEA96,
        totNumOfIteHEA305,
        totNumOfPacHEA306,
        grossMass.toString,
        decDatHEA383,
        decPlaHEA394,
        speCirIndHEA1,
        traChaMetOfPayHEA1,
        comRefNumHEA,
        secHEA358,
        conRefNumHEA,
        codPlUnHEA357
      )
    }

  implicit lazy val arbitraryTraderPrincipalWithEori: Arbitrary[TraderPrincipalWithEori] =
    Arbitrary {
      for {
        eori                 <- stringsWithMaxLength(Trader.Constants.eoriLength, alphaNumChar)
        name                 <- Gen.option(stringsWithMaxLength(Trader.Constants.nameLength, alphaNumChar))
        streetAndNumber      <- Gen.option(stringsWithMaxLength(Trader.Constants.streetAndNumberLength, alphaNumChar))
        postCode             <- Gen.option(stringsWithMaxLength(Trader.Constants.postCodeLength, alphaNumChar))
        city                 <- Gen.option(stringsWithMaxLength(Trader.Constants.cityLength, alphaNumChar))
        countryCode          <- Gen.option(Gen.pick(2, 'A' to 'Z'))
        principalTirHolderId <- Gen.option("GBR/123/1234567")
      } yield TraderPrincipalWithEori(eori, name, streetAndNumber, postCode, city, countryCode.map(_.mkString), principalTirHolderId)
    }

  implicit lazy val arbitraryItemsSecurityConsignorWithEori: Arbitrary[ItemsSecurityConsignorWithEori] =
    Arbitrary {
      for {
        eori <- stringsWithMaxLength(Trader.Constants.eoriLength, alphaNumChar)

      } yield ItemsSecurityConsignorWithEori(eori)
    }

  implicit lazy val arbitraryItemsSecurityConsigneeWithoutEori: Arbitrary[ItemsSecurityConsigneeWithoutEori] =
    Arbitrary {
      for {
        name            <- stringsWithMaxLength(Trader.Constants.nameLength, alphaNumChar)
        streetAndNumber <- stringsWithMaxLength(Trader.Constants.streetAndNumberLength, alphaNumChar)
        postCode        <- stringsWithMaxLength(Trader.Constants.postCodeLength, alphaNumChar)
        city            <- stringsWithMaxLength(Trader.Constants.cityLength, alphaNumChar)
        countryCode     <- Gen.pick(2, 'A' to 'Z')
      } yield ItemsSecurityConsigneeWithoutEori(name, streetAndNumber, postCode, city, countryCode.mkString)
    }

  implicit lazy val arbitraryItemsSecurityConsigneeWithEori: Arbitrary[ItemsSecurityConsigneeWithEori] =
    Arbitrary {
      for {
        eori <- stringsWithMaxLength(Trader.Constants.eoriLength, alphaNumChar)

      } yield ItemsSecurityConsigneeWithEori(eori)
    }

  implicit lazy val arbitraryItemsSecurityConsignorWithoutEori: Arbitrary[ItemsSecurityConsignorWithoutEori] =
    Arbitrary {
      for {
        name            <- stringsWithMaxLength(Trader.Constants.nameLength, alphaNumChar)
        streetAndNumber <- stringsWithMaxLength(Trader.Constants.streetAndNumberLength, alphaNumChar)
        postCode        <- stringsWithMaxLength(Trader.Constants.postCodeLength, alphaNumChar)
        city            <- stringsWithMaxLength(Trader.Constants.cityLength, alphaNumChar)
        countryCode     <- Gen.pick(2, 'A' to 'Z')
      } yield ItemsSecurityConsignorWithoutEori(name, streetAndNumber, postCode, city, countryCode.mkString)
    }

  implicit lazy val arbitraryTraderPrincipalWithoutEori: Arbitrary[TraderPrincipalWithoutEori] =
    Arbitrary {
      for {
        name                 <- stringsWithMaxLength(Trader.Constants.nameLength, alphaNumChar)
        streetAndNumber      <- stringsWithMaxLength(Trader.Constants.streetAndNumberLength, alphaNumChar)
        postCode             <- stringsWithMaxLength(Trader.Constants.postCodeLength, alphaNumChar)
        city                 <- stringsWithMaxLength(Trader.Constants.cityLength, alphaNumChar)
        countryCode          <- Gen.pick(2, 'A' to 'Z')
        principalTirHolderId <- Gen.option("GBR/123/1234567")
      } yield TraderPrincipalWithoutEori(name, streetAndNumber, postCode, city, countryCode.mkString, principalTirHolderId)
    }

  implicit lazy val arbitraryTraderConsignor: Arbitrary[TraderConsignor] =
    Arbitrary {
      for {
        name            <- stringsWithMaxLength(Trader.Constants.nameLength, alphaNumChar)
        streetAndNumber <- stringsWithMaxLength(Trader.Constants.streetAndNumberLength, alphaNumChar)
        postCode        <- stringsWithMaxLength(Trader.Constants.postCodeLength, alphaNumChar)
        city            <- stringsWithMaxLength(Trader.Constants.cityLength, alphaNumChar)
        countryCode     <- Gen.pick(2, 'A' to 'Z')
        eori            <- Gen.option(stringsWithMaxLength(Trader.Constants.eoriLength, alphaNumChar))
      } yield TraderConsignor(name, streetAndNumber, postCode, city, countryCode.mkString, eori)
    }

  implicit lazy val arbitraryTraderConsignee: Arbitrary[TraderConsignee] =
    Arbitrary {
      for {
        name            <- stringsWithMaxLength(Trader.Constants.nameLength, alphaNumChar)
        streetAndNumber <- stringsWithMaxLength(Trader.Constants.streetAndNumberLength, alphaNumChar)
        postCode        <- stringsWithMaxLength(Trader.Constants.postCodeLength, alphaNumChar)
        city            <- stringsWithMaxLength(Trader.Constants.cityLength, alphaNumChar)
        countryCode     <- Gen.pick(2, 'A' to 'Z')
        eori            <- Gen.option(stringsWithMaxLength(Trader.Constants.eoriLength, alphaNumChar))
      } yield TraderConsignee(name, streetAndNumber, postCode, city, countryCode.mkString, eori)
    }

  implicit lazy val arbitraryTraderConsignorGoodsItem: Arbitrary[TraderConsignorGoodsItem] =
    Arbitrary {
      for {
        traderConsignor <- arbitrary[TraderConsignor]
      } yield TraderConsignorGoodsItem(
        traderConsignor.name,
        traderConsignor.streetAndNumber,
        traderConsignor.postCode,
        traderConsignor.city,
        traderConsignor.countryCode.mkString,
        traderConsignor.eori
      )
    }

  implicit lazy val arbitraryTraderConsigneeGoodsItem: Arbitrary[TraderConsigneeGoodsItem] =
    Arbitrary {
      for {
        traderConsignee <- arbitrary[TraderConsignee]
      } yield TraderConsigneeGoodsItem(
        traderConsignee.name,
        traderConsignee.streetAndNumber,
        traderConsignee.postCode,
        traderConsignee.city,
        traderConsignee.countryCode.mkString,
        traderConsignee.eori
      )
    }

  implicit lazy val arbitraryAuthorisedConsigneeTrader: Arbitrary[TraderAuthorisedConsignee] =
    Arbitrary {
      for {
        eori <- stringsWithMaxLength(Trader.Constants.eoriLength, alphaNumChar)
      } yield TraderAuthorisedConsignee(eori)
    }

  implicit lazy val arbitraryRepresentative: Arbitrary[Representative] =
    Arbitrary {
      for {
        name     <- stringsWithMaxLength(Representative.Constants.nameLength, alphaNumChar)
        capacity <- Gen.option(stringsWithMaxLength(Representative.Constants.capacityLength, alphaNumChar))
      } yield Representative(name, capacity)
    }

  implicit lazy val arbitraryControlResult: Arbitrary[ControlResult] =
    Arbitrary {
      for {
        dateLimit <- localDateGen
      } yield ControlResult(dateLimit)
    }

  implicit lazy val arbitrarySeals: Arbitrary[Seals] =
    Arbitrary {
      for {
        numberOfSeals <- choose(min = 1: Int, 10: Int)
        sealId        <- listWithMaxLength(numberOfSeals, stringsWithMaxLength(Seals.Constants.sealIdLength, alphaNumChar))
      } yield Seals(numberOfSeals, sealId)
    }

  implicit lazy val arbitraryGuaranteeReferenceWithGrn: Arbitrary[GuaranteeReferenceWithGrn] =
    Arbitrary {
      for {
        guaranteeReferenceNumber <- stringsWithMaxLength(GuaranteeReferenceWithGrn.Constants.guaranteeReferenceNumberLength, alphaNumChar)
        accessCode               <- stringsWithMaxLength(GuaranteeReference.Constants.accessCodeLength, alphaNumChar)
      } yield GuaranteeReferenceWithGrn(guaranteeReferenceNumber, accessCode)
    }

  implicit lazy val arbitraryGuaranteeReferenceWithOther: Arbitrary[GuaranteeReferenceWithOther] =
    Arbitrary {
      for {
        guaranteeReferenceNumber <- stringsWithMaxLength(GuaranteeReferenceWithOther.Constants.otherReferenceNumberLength, alphaNumChar)
        accessCode               <- Gen.option(stringsWithMaxLength(GuaranteeReference.Constants.accessCodeLength, alphaNumChar))
      } yield GuaranteeReferenceWithOther(guaranteeReferenceNumber, accessCode)
    }

  implicit lazy val arbitraryGuaranteeReference: Arbitrary[GuaranteeReference] =
    Arbitrary {
      Gen.oneOf[GuaranteeReference](arbitrary[GuaranteeReferenceWithGrn], arbitrary[GuaranteeReferenceWithOther])
    }

  implicit lazy val arbitraryGuarantee: Arbitrary[Guarantee] =
    Arbitrary {
      for {
        guaranteeType      <- stringsWithMaxLength(GuaranteeReferenceWithOther.Constants.otherReferenceNumberLength, alphaNumChar)
        guaranteeReference <- listWithMaxLength(Guarantee.Constants.guaranteeReferenceCount, arbitrary[GuaranteeReference])
      } yield Guarantee(guaranteeType, guaranteeReference)
    }

  implicit lazy val arbitraryGoodsItem: Arbitrary[GoodsItem] =
    Arbitrary {
      for {
        itemNumber           <- Gen.choose(1, 99999)
        commodityCode        <- Gen.option(stringsWithMaxLength(GoodsItem.Constants.commodityCodeLength, alphaNumChar))
        declarationType      <- Gen.option(Gen.pick(GoodsItem.Constants.typeOfDeclarationLength, 'A' to 'Z'))
        description          <- stringsWithMaxLength(GoodsItem.Constants.descriptionLength, alphaNumChar)
        grossMass            <- Gen.option(Gen.choose(0.0, 99999999.999).map(BigDecimal(_)))
        netMass              <- Gen.option(Gen.choose(0.0, 99999999.999).map(BigDecimal(_)))
        countryOfDispatch    <- Gen.option(stringsWithMaxLength(GoodsItem.Constants.countryLength, alphaNumChar))
        countryOfDestination <- Gen.option(stringsWithMaxLength(GoodsItem.Constants.countryLength, alphaNumChar))
        metOfPayGDI12        <- Gen.option(Gen.pick(Header.Constants.methodOfPaymentLength, 'A' to 'Z').map(_.mkString))
        comRefNumGIM1        <- Gen.option(stringsWithMaxLength(Header.Constants.commercialReferenceNumberLength, alphaNumChar))
        uNDanGooCodGDI1      <- Gen.option(stringsWithMaxLength(GoodsItem.Constants.dangerousGoodsCodeLength, alphaNumChar))
        previousAdministrativeReference <- listWithMaxLength(PreviousAdministrativeReference.Constants.previousAdministrativeReferenceCount,
                                                             arbitrary[PreviousAdministrativeReference]
        )
        producedDocuments          <- listWithMaxLength(ProducedDocument.Constants.producedDocumentCount, arbitrary[ProducedDocument])
        specialMentions            <- listWithMaxLength(SpecialMention.Constants.specialMentionCount, arbitrary[SpecialMention])
        traderConsignorGoodsItem   <- Gen.option(arbitrary[TraderConsignorGoodsItem])
        traderConsigneeGoodsItem   <- Gen.option(arbitrary[TraderConsigneeGoodsItem])
        containers                 <- listWithMaxLength(Containers.Constants.containerCount, stringsWithMaxLength(Containers.Constants.containerNumberLength, alphaNumChar))
        packages                   <- listWithMaxLength(Package.Constants.packageCount, arbitrary[Package])
        sensitiveGoodsInformation  <- listWithMaxLength(SensitiveGoodsInformation.Constants.sensitiveGoodsInformationCount, arbitrary[SensitiveGoodsInformation])
        goodsItemSecurityConsignor <- Gen.option(arbitraryGoodsItemSecurityConsignor.arbitrary)
        goodsItemSecurityConsignee <- Gen.option(arbitraryGoodsItemSecurityConsignee.arbitrary)

      } yield GoodsItem(
        itemNumber,
        commodityCode,
        declarationType.map(_.mkString),
        description,
        grossMass,
        netMass,
        countryOfDispatch,
        countryOfDestination,
        metOfPayGDI12,
        comRefNumGIM1,
        uNDanGooCodGDI1,
        previousAdministrativeReference,
        producedDocuments,
        specialMentions,
        traderConsignorGoodsItem,
        traderConsigneeGoodsItem,
        containers,
        packages,
        sensitiveGoodsInformation,
        goodsItemSecurityConsignor,
        goodsItemSecurityConsignee
      )
    }

  implicit lazy val arbitraryGoodsItemSecurityConsignor: Arbitrary[GoodsItemSecurityConsignor] = Arbitrary {
    Gen.oneOf[GoodsItemSecurityConsignor](arbitrary[ItemsSecurityConsignorWithEori], arbitrary[ItemsSecurityConsignorWithoutEori])
  }

  implicit lazy val arbitraryGoodsItemSecurityConsignee: Arbitrary[GoodsItemSecurityConsignee] = Arbitrary {
    Gen.oneOf[GoodsItemSecurityConsignee](arbitrary[ItemsSecurityConsigneeWithEori], arbitrary[ItemsSecurityConsigneeWithoutEori])
  }

  implicit lazy val arbitraryPreviousAdministrativeReferences: Arbitrary[PreviousAdministrativeReference] =
    Arbitrary {
      for {
        preDocTypAR21 <- stringsWithMaxLength(PreviousAdministrativeReference.Constants.previousDocumentTypeLength, alphaNumChar)
        preDocRefAR26 <- stringsWithMaxLength(PreviousAdministrativeReference.Constants.previousDocumentReferenceLength, alphaNumChar)
        comOfInfAR29  <- Gen.option(stringsWithMaxLength(PreviousAdministrativeReference.Constants.complementOfInformationLength, alphaNumChar))
      } yield PreviousAdministrativeReference(
        preDocTypAR21,
        preDocRefAR26,
        comOfInfAR29
      )
    }

  implicit lazy val arbitraryPreviousProducedDocument: Arbitrary[ProducedDocument] =
    Arbitrary {
      for {
        documentType            <- stringsWithMaxLength(ProducedDocument.Constants.documentTypeLength, alphaNumChar)
        reference               <- Gen.option(stringsWithMaxLength(ProducedDocument.Constants.reference, alphaNumChar))
        complementOfInformation <- Gen.option(stringsWithMaxLength(ProducedDocument.Constants.complementOfInformation, alphaNumChar))
      } yield ProducedDocument(
        documentType,
        reference,
        complementOfInformation
      )
    }

  protected val countrySpecificCodes      = Seq("DG0", "DG1")
  protected val countrySpecificCodeGen    = Gen.oneOf(countrySpecificCodes)
  protected val nonCountrySpecificCodeGen = stringsWithMaxLength(5, alphaNumChar).retryUntil(!countrySpecificCodes.contains(_))

  protected val liabilityAmount    = Seq("5000", "10000")
  protected val liabilityAmountGen = Gen.oneOf(liabilityAmount)

  implicit lazy val arbitrarySpecialMentionGuaranteeLiabilityAmount: Arbitrary[SpecialMentionGuaranteeLiabilityAmount] =
    Arbitrary {
      liabilityAmountGen.map(SpecialMentionGuaranteeLiabilityAmount("CAL", _))
    }

  implicit lazy val arbitrarySpecialMentionExportFromGB: Arbitrary[SpecialMentionExportFromGB] =
    Arbitrary {
      for {
        addInfoCoded <- countrySpecificCodeGen
        addInfo      <- stringsWithMaxLength(70)
      } yield SpecialMentionExportFromGB(addInfoCoded, addInfo)
    }

  implicit lazy val arbitrarySpecialMentionExportFromNI: Arbitrary[SpecialMentionExportFromNI] =
    Arbitrary {
      for {
        additionalInfoCoded <- countrySpecificCodeGen
        addInfo             <- stringsWithMaxLength(70)
      } yield SpecialMentionExportFromNI(additionalInfoCoded, addInfo)
    }

  implicit lazy val arbitrarySpecialMentionNoCountry: Arbitrary[SpecialMentionNoCountry] =
    Arbitrary {
      for {
        addInfoCoded <- nonCountrySpecificCodeGen
        addInfo      <- stringsWithMaxLength(70)
      } yield SpecialMentionNoCountry(addInfoCoded, addInfo)
    }

  implicit lazy val arbitrarySpecialMention: Arbitrary[SpecialMention] =
    Arbitrary {
      Gen.oneOf(
        arbitrary[SpecialMentionGuaranteeLiabilityAmount],
        arbitrary[SpecialMentionExportFromGB],
        arbitrary[SpecialMentionExportFromNI],
        arbitrary[SpecialMentionNoCountry]
      )
    }

  implicit lazy val arbitraryBulkPackage: Arbitrary[BulkPackage] =
    Arbitrary {
      for {
        kindOfPackage   <- Gen.oneOf(BulkPackage.validCodes)
        marksAndNumbers <- Gen.option(stringsWithMaxLength(42, alphaNumChar))
      } yield BulkPackage(kindOfPackage, marksAndNumbers)
    }

  implicit lazy val arbitraryUnpackedPackage: Arbitrary[UnpackedPackage] =
    Arbitrary {
      for {
        kindOfPackage   <- Gen.oneOf(UnpackedPackage.validCodes)
        numberOfPieces  <- Gen.choose(1, 99999)
        marksAndNumbers <- Gen.option(stringsWithMaxLength(42, alphaNumChar))
      } yield UnpackedPackage(kindOfPackage, numberOfPieces, marksAndNumbers)
    }

  implicit lazy val arbitraryRegularPackage: Arbitrary[RegularPackage] =
    Arbitrary {
      for {
        kindOfPackage <- stringsWithMaxLength(3, alphaNumChar).retryUntil(
          x => !BulkPackage.validCodes.contains(x) && !UnpackedPackage.validCodes.contains(x)
        )
        numberOfPackages <- Gen.choose(0, 99999)
        marksAndNumbers  <- stringsWithMaxLength(42, alphaNumChar)
      } yield RegularPackage(kindOfPackage, numberOfPackages, marksAndNumbers)
    }

  implicit lazy val arbitraryPackage: Arbitrary[Package] =
    Arbitrary {
      Gen.oneOf(arbitrary[BulkPackage], arbitrary[UnpackedPackage], arbitrary[RegularPackage])
    }

  implicit lazy val arbitrarySensitiveGoodsInformation: Arbitrary[SensitiveGoodsInformation] =
    Arbitrary {
      for {
        goodsCode <- Gen.option(Gen.choose(0, 99))
        quantity  <- Gen.choose(0, 99999)
      } yield SensitiveGoodsInformation(
        goodsCode,
        quantity
      )
    }

  implicit lazy val arbitraryInvalidGuaranteeReasonCode: Arbitrary[InvalidGuaranteeReasonCode] =
    Arbitrary {
      for {
        guaranteeRefNumber <- stringsWithMaxLength(21, alphaNumChar)
        invalidCode        <- Gen.oneOf(InvalidGuaranteeCode.values)
        invalidReason      <- Gen.option(nonEmptyString)
      } yield InvalidGuaranteeReasonCode(guaranteeRefNumber, invalidCode, invalidReason)
    }

}
