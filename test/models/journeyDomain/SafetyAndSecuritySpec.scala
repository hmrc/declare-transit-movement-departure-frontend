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

import base.{GeneratorSpec, SpecBase}
import cats.data.NonEmptyList
import commonTestUtils.UserAnswersSpecHelper
import generators.JourneyModelGenerators
import models.domain.Address
import models.journeyDomain.PackagesSpec.UserAnswersSpecHelperOps
import models.journeyDomain.SafetyAndSecurity.{PersonalInformation, TraderEori}
import models.reference.{Country, CountryCode}
import models.{CommonAddress, EoriNumber, Index, UserAnswers}
import org.scalacheck.Gen
import org.scalatest.TryValues
import pages.ModeAtBorderPage
import pages.safetyAndSecurity._

class SafetyAndSecuritySpec extends SpecBase with GeneratorSpec with TryValues with JourneyModelGenerators {

  private val fullSafetyAndSecurityUa = emptyUserAnswers
    .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
    .unsafeSetVal(CircumstanceIndicatorPage)("circumstanceIndicator")
    .unsafeSetVal(AddTransportChargesPaymentMethodPage)(true)
    .unsafeSetVal(TransportChargesPaymentMethodPage)("transportChargesPaymentMethod")
    .unsafeSetVal(AddCommercialReferenceNumberAllItemsPage)(true)
    .unsafeSetVal(AddCommercialReferenceNumberPage)(true)
    .unsafeSetVal(CommercialReferenceNumberAllItemsPage)("commercialRefNumber")
    .unsafeSetVal(AddConveyanceReferenceNumberPage)(true)
    .unsafeSetVal(ConveyanceReferenceNumberPage)("conveyanceRefNumber")
    .unsafeSetVal(PlaceOfUnloadingCodePage)("placeOfUnloading")
    .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(false)
    .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(false)
    .unsafeSetVal(AddCarrierPage)(false)
    .unsafeSetVal(CountryOfRoutingPage(Index(0)))(CountryCode("GB"))

  "SafetyAndSecurity" - {

    "can be parsed for UserAnswers" - {

      "when the minimal answers are defined" in {

        val expectedResult =
          SafetyAndSecurity(None, None, None, None, Some("placeOfUnloading"), None, None, None, NonEmptyList.fromListUnsafe(List(Itinerary(CountryCode("GB")))))

        val minimalSafetyAndSecurityUa = emptyUserAnswers
          .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
          .unsafeSetVal(AddTransportChargesPaymentMethodPage)(false)
          .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
          .unsafeSetVal(AddCommercialReferenceNumberAllItemsPage)(false)
          .unsafeSetVal(AddConveyanceReferenceNumberPage)(false)
          .unsafeSetVal(PlaceOfUnloadingCodePage)("placeOfUnloading")
          .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(false)
          .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(false)
          .unsafeSetVal(AddCarrierPage)(false)
          .unsafeSetVal(CountryOfRoutingPage(Index(0)))(CountryCode("GB"))

        val result = UserAnswersReader[SafetyAndSecurity].run(minimalSafetyAndSecurityUa).right.value

        result mustBe expectedResult
      }

      "when all answers are defined" - {

        "commercialReferenceNumber" - {

          "must be defined when AddCommercialReferenceNumberPage is true and AddCommercialReferenceNumberAllItemsPage is true" in {

            val userAnswers = fullSafetyAndSecurityUa
              .unsafeSetVal(AddCommercialReferenceNumberPage)(true)
              .unsafeSetVal(AddCommercialReferenceNumberAllItemsPage)(true)
              .unsafeSetVal(CommercialReferenceNumberAllItemsPage)("commercialRefNumber")

            val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).right.value

            result.commercialReferenceNumber.value mustBe "commercialRefNumber"
          }

          "must not be defined when AddCommercialReferenceNumberPage is false and AddCommercialReferenceNumberAllItemsPage is true" in {

            val userAnswers = fullSafetyAndSecurityUa
              .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
              .unsafeSetVal(AddCommercialReferenceNumberAllItemsPage)(true)

            val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).right.value

            result.commercialReferenceNumber mustBe None
          }

          "must not be defined when AddCommercialReferenceNumberPage is true and AddCommercialReferenceNumberAllItemsPage is false" in {

            val userAnswers = fullSafetyAndSecurityUa
              .unsafeSetVal(AddCommercialReferenceNumberPage)(true)
              .unsafeSetVal(AddCommercialReferenceNumberAllItemsPage)(false)

            val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).right.value

            result.commercialReferenceNumber mustBe None
          }
        }

        "ConveyanceReferenceNumber" - {

          "must be defined when modeAtBorder is 4 or 40" in {

            val modeAtBorder = Gen.oneOf(Seq("4", "40")).sample.value

            val userAnswers = fullSafetyAndSecurityUa
              .unsafeSetVal(ModeAtBorderPage)(modeAtBorder)
              .unsafeSetVal(ConveyanceReferenceNumberPage)("conveyanceReferenceNumber")

            val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).right.value

            result.conveyanceReferenceNumber.value mustBe "conveyanceReferenceNumber"

          }

          "must not be defined when modeAtBorder is not 4 or 40 and AddConveyanceReferenceNumber is false" in {

            val modeAtBorder = arb[String]
              .suchThat(
                mode => mode != "4" && mode != "40"
              )
              .sample
              .value

            val userAnswers = fullSafetyAndSecurityUa
              .unsafeSetVal(ModeAtBorderPage)(modeAtBorder)
              .unsafeSetVal(AddConveyanceReferenceNumberPage)(false)

            val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).right.value

            result.conveyanceReferenceNumber mustBe None
          }
        }

        "PlaceOfUnloadingCode" - {

          "must be defined when addCircumstanceIndicator is 'E' and AddPlaceOfUnloadingCodePage is true" in {

            val userAnswers = fullSafetyAndSecurityUa
              .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
              .unsafeSetVal(CircumstanceIndicatorPage)("E")
              .unsafeSetVal(AddPlaceOfUnloadingCodePage)(true)
              .unsafeSetVal(PlaceOfUnloadingCodePage)("placeOfUnloading")

            val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).right.value

            result.placeOfUnloading.value mustBe "placeOfUnloading"
          }

          "must be defined when addCircumstanceIndicator is not 'E' " in {

            val circumstanceIndicator = arb[String].suchThat(_ != "E").sample.value

            val userAnswers = fullSafetyAndSecurityUa
              .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
              .unsafeSetVal(CircumstanceIndicatorPage)(circumstanceIndicator)
              .unsafeSetVal(PlaceOfUnloadingCodePage)("placeOfUnloading")

            val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).right.value

            result.placeOfUnloading.value mustBe "placeOfUnloading"
          }

          "must not defined when addCircumstanceIndicator is 'E' and AddPlaceOfUnloadingCodePage is false" in {

            val userAnswers = fullSafetyAndSecurityUa
              .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
              .unsafeSetVal(CircumstanceIndicatorPage)("E")
              .unsafeSetVal(AddPlaceOfUnloadingCodePage)(false)

            val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).right.value

            result.placeOfUnloading mustBe None
          }
        }

        "consignorDetails" - {

          "must be defined with Eori number when AddSafetyAndSecurityConsignorPage is true and AddSafetyAndSecurityConsignorEoriPage is true" in {

            val expectedResult = TraderEori(EoriNumber("eoriNumber"))

            val userAnswers = fullSafetyAndSecurityUa
              .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(true)
              .unsafeSetVal(AddSafetyAndSecurityConsignorEoriPage)(true)
              .unsafeSetVal(SafetyAndSecurityConsignorEoriPage)("eoriNumber")

            val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).right.value

            result.consignor.value mustBe expectedResult
          }

          "must be defined with Name and address when AddSafetyAndSecurityConsignorPage is true AddSafetyAndSecurityConsignorPage is false" in {

            val expectedResult = PersonalInformation("consignorName", Address("line1", "line2", "postalCode", Some(Country(CountryCode("GB"), "description"))))

            val userAnswers = fullSafetyAndSecurityUa
              .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(true)
              .unsafeSetVal(AddSafetyAndSecurityConsignorEoriPage)(false)
              .unsafeSetVal(SafetyAndSecurityConsignorNamePage)("consignorName")
              .unsafeSetVal(SafetyAndSecurityConsignorAddressPage)(CommonAddress("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))

            val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).right.value

            result.consignor.value mustBe expectedResult
          }

          "must not be defined when AddSafetyAndSecurityConsignorPage is false" in {

            val userAnswers = fullSafetyAndSecurityUa
              .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(false)

            val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).right.value

            result.consignor mustBe None
          }
        }
      }
    }
  }

}

object SafetyAndSecuritySpec extends UserAnswersSpecHelper {

  def setSafetyAndSecurity(safetyAndSecurity: SafetyAndSecurity)(startUserAnswers: UserAnswers): UserAnswers = {
    val ua = startUserAnswers
      // Set summary details
      .unsafeSetVal(AddCircumstanceIndicatorPage)(safetyAndSecurity.circumstanceIndicator.isDefined)
      .unsafeSetOpt(CircumstanceIndicatorPage)(safetyAndSecurity.circumstanceIndicator)
      .unsafeSetVal(AddTransportChargesPaymentMethodPage)(safetyAndSecurity.paymentMethod.isDefined)
      .unsafeSetOpt(TransportChargesPaymentMethodPage)(safetyAndSecurity.paymentMethod)
      .unsafeSetVal(AddCommercialReferenceNumberPage)(safetyAndSecurity.commercialReferenceNumber.isDefined)
      .unsafeSetVal(AddCommercialReferenceNumberAllItemsPage)(safetyAndSecurity.commercialReferenceNumber.isDefined)
      .unsafeSetOpt(CommercialReferenceNumberAllItemsPage)(safetyAndSecurity.commercialReferenceNumber)
      .unsafeSetVal(AddPlaceOfUnloadingCodePage)(safetyAndSecurity.placeOfUnloading.isDefined)
      .unsafeSetOpt(PlaceOfUnloadingCodePage)(safetyAndSecurity.placeOfUnloading)
      // Set Consignor
      .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(safetyAndSecurity.consignor.isDefined)
      .unsafeSetPFn(AddSafetyAndSecurityConsignorEoriPage)(safetyAndSecurity.consignor)({
        case Some(TraderEori(_)) => true
        case Some(_)             => false
      })
      .unsafeSetPFn(SafetyAndSecurityConsignorEoriPage)(safetyAndSecurity.consignor)({
        case Some(TraderEori(eori)) => eori.value
      })
      .unsafeSetPFn(SafetyAndSecurityConsignorNamePage)(safetyAndSecurity.consignor)({
        case Some(PersonalInformation(name, _)) => name
      })
      .unsafeSetPFn(SafetyAndSecurityConsignorAddressPage)(safetyAndSecurity.consignor)({
        case Some(PersonalInformation(_, address)) =>
          Address.prismAddressToCommonAddress.getOption(address).get
      })
      // Set Consignee
      .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(safetyAndSecurity.consignee.isDefined)
      .unsafeSetPFn(AddSafetyAndSecurityConsigneeEoriPage)(safetyAndSecurity.consignee)({
        case Some(TraderEori(_)) => true
        case Some(_)             => false
      })
      .unsafeSetPFn(SafetyAndSecurityConsigneeEoriPage)(safetyAndSecurity.consignee)({
        case Some(TraderEori(eori)) => eori.value
      })
      .unsafeSetPFn(SafetyAndSecurityConsigneeNamePage)(safetyAndSecurity.consignee)({
        case Some(PersonalInformation(name, _)) => name
      })
      .unsafeSetPFn(SafetyAndSecurityConsigneeAddressPage)(safetyAndSecurity.consignee)({
        case Some(PersonalInformation(_, address)) =>
          Address.prismAddressToCommonAddress.getOption(address).get
      })
      // Set Carrier
      .unsafeSetVal(AddCarrierPage)(safetyAndSecurity.carrier.isDefined)
      .unsafeSetPFn(AddCarrierEoriPage)(safetyAndSecurity.carrier)({
        case Some(TraderEori(_)) => true
        case Some(_)             => false
      })
      .unsafeSetPFn(CarrierEoriPage)(safetyAndSecurity.carrier)({
        case Some(TraderEori(eori)) => eori.value
      })
      .unsafeSetPFn(CarrierNamePage)(safetyAndSecurity.carrier)({
        case Some(PersonalInformation(name, _)) => name
      })
      .unsafeSetPFn(CarrierAddressPage)(safetyAndSecurity.carrier)({
        case Some(PersonalInformation(_, address)) =>
          Address.prismAddressToCommonAddress.getOption(address).get
      })

    val updatedUserAnswers = ua.get(ModeAtBorderPage) match {
      case Some("4") | Some("40") =>
        ua.unsafeSetOpt(ConveyanceReferenceNumberPage)(safetyAndSecurity.conveyanceReferenceNumber)
      case _ =>
        ua.unsafeSetVal(AddConveyanceReferenceNumberPage)(safetyAndSecurity.conveyanceReferenceNumber.isDefined)
          .unsafeSetOpt(ConveyanceReferenceNumberPage)(safetyAndSecurity.conveyanceReferenceNumber)
    }

    ItinerarySpec.setItineraries(safetyAndSecurity.itineraryList.toList)(updatedUserAnswers)

  }
}
