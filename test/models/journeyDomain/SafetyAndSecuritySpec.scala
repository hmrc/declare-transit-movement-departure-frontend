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
import models.journeyDomain.SafetyAndSecurity.{PersonalInformation, TraderEori}
import models.reference.{Country, CountryCode, CustomsOffice, MethodOfPayment}
import models.{CommonAddress, EoriNumber, Index}
import org.scalacheck.Gen
import org.scalatest.TryValues
import pages.safetyAndSecurity._
import pages.{ModeAtBorderPage, OfficeOfDeparturePage, QuestionPage}

class SafetyAndSecuritySpec extends SpecBase with GeneratorSpec with TryValues with UserAnswersSpecHelper {

  private val fullSafetyAndSecurityUa = emptyUserAnswers
    .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("GB"), None))
    .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
    .unsafeSetVal(CircumstanceIndicatorPage)("circumstanceIndicator")
    .unsafeSetVal(AddTransportChargesPaymentMethodPage)(true)
    .unsafeSetVal(TransportChargesPaymentMethodPage)(MethodOfPayment("code", Some("description")))
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

    "can parse from UserAnswers" - {

      "when all mandatory answers are defined" in {

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

      "when optional answers are defined" in {

        val expectedResult = SafetyAndSecurity(
          Some("circumstanceIndicator"),
          Some(MethodOfPayment("code", Some("description"))),
          Some("commercialRefNumber"),
          Some("conveyanceRefNumber"),
          Some("placeOfUnloading"),
          None,
          None,
          None,
          NonEmptyList.fromListUnsafe(List(Itinerary(CountryCode("GB"))))
        )

        val result = UserAnswersReader[SafetyAndSecurity].run(fullSafetyAndSecurityUa).right.value

        result mustBe expectedResult
      }

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

          val expectedResult = PersonalInformation("consignorName", CommonAddress("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))

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

      "consigneeDetails" - {

        "must be defined with Eori number " +
          "when AddSafetyAndSecurityConsigneePage is true " +
          "and CircumstanceIndicatorPage is 'E' " +
          "and departure office is not XI" in {

            val expectedResult = TraderEori(EoriNumber("eoriNumber"))

            val userAnswers = fullSafetyAndSecurityUa
              .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(true)
              .unsafeSetVal(CircumstanceIndicatorPage)("E")
              .unsafeSetVal(AddPlaceOfUnloadingCodePage)(false)
              .unsafeSetVal(SafetyAndSecurityConsigneeEoriPage)("eoriNumber")

            val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).right.value

            result.consignee.value mustBe expectedResult
          }

        "must be defined with Eori number " +
          "when AddSafetyAndSecurityConsigneePage is true " +
          "and CircumstanceIndicatorPage is 'E' " +
          "and departure office is XI " +
          "and AddSafetyAndSecurityConsigneeEoriPage is true" in {

            val expectedResult = TraderEori(EoriNumber("eoriNumber"))

            val userAnswers = fullSafetyAndSecurityUa
              .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(true)
              .unsafeSetVal(CircumstanceIndicatorPage)("E")
              .unsafeSetVal(AddPlaceOfUnloadingCodePage)(false)
              .unsafeSetVal(SafetyAndSecurityConsigneeEoriPage)("eoriNumber")
              .unsafeSetVal(AddSafetyAndSecurityConsigneeEoriPage)(true)
              .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("XI"), None))

            val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).right.value

            result.consignee.value mustBe expectedResult
          }

        "must be defined with name and address " +
          "when AddSafetyAndSecurityConsigneePage is true " +
          "and CircumstanceIndicatorPage is 'E' " +
          "and departure office is XI " +
          "and AddSafetyAndSecurityConsigneeEoriPage is false" in {

            val expectedResult = PersonalInformation("consigneeName", CommonAddress("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))

            val userAnswers = fullSafetyAndSecurityUa
              .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("XI"), None))
              .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(true)
              .unsafeSetVal(AddSafetyAndSecurityConsigneeEoriPage)(false)
              .unsafeSetVal(CircumstanceIndicatorPage)("E")
              .unsafeSetVal(AddPlaceOfUnloadingCodePage)(false)
              .unsafeSetVal(SafetyAndSecurityConsigneeNamePage)("consigneeName")
              .unsafeSetVal(SafetyAndSecurityConsigneeAddressPage)(CommonAddress("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))

            val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).right.value

            result.consignee.value mustBe expectedResult
          }

        "must be defined with Eori number when AddSafetyAndSecurityConsigneePage is true and AddSafetyAndSecurityConsigneeEoriPage is true" in {

          val expectedResult = TraderEori(EoriNumber("eoriNumber"))

          val userAnswers = fullSafetyAndSecurityUa
            .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(true)
            .unsafeSetVal(AddSafetyAndSecurityConsigneeEoriPage)(true)
            .unsafeSetVal(SafetyAndSecurityConsigneeEoriPage)("eoriNumber")

          val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).right.value

          result.consignee.value mustBe expectedResult
        }

        "must be defined with Name and address when AddSafetyAndSecurityConsigneePage is true AddSafetyAndSecurityConsigneePage is false" in {

          val expectedResult = PersonalInformation("consigneeName", CommonAddress("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))

          val userAnswers = fullSafetyAndSecurityUa
            .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(true)
            .unsafeSetVal(AddSafetyAndSecurityConsigneeEoriPage)(false)
            .unsafeSetVal(SafetyAndSecurityConsigneeNamePage)("consigneeName")
            .unsafeSetVal(SafetyAndSecurityConsigneeAddressPage)(CommonAddress("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))

          val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).right.value

          result.consignee.value mustBe expectedResult
        }

        "must not be defined when AddSafetyAndSecurityConsigneePage is false" in {

          val userAnswers = fullSafetyAndSecurityUa
            .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(false)

          val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).right.value

          result.consignee mustBe None
        }
      }

      "carrierDetails" - {

        "must be defined with Eori number when AddCarrierPage is true and AddCarrierEoriPage is true" in {

          val expectedResult = TraderEori(EoriNumber("eoriNumber"))

          val userAnswers = fullSafetyAndSecurityUa
            .unsafeSetVal(AddCarrierPage)(true)
            .unsafeSetVal(AddCarrierEoriPage)(true)
            .unsafeSetVal(CarrierEoriPage)("eoriNumber")

          val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).right.value

          result.carrier.value mustBe expectedResult
        }

        "must be defined with Name and address when AddCarrierPage is true AddCarrierEoriPage is false" in {

          val expectedResult = PersonalInformation("carrierName", CommonAddress("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))

          val userAnswers = fullSafetyAndSecurityUa
            .unsafeSetVal(AddCarrierPage)(true)
            .unsafeSetVal(AddCarrierEoriPage)(false)
            .unsafeSetVal(CarrierNamePage)("carrierName")
            .unsafeSetVal(CarrierAddressPage)(CommonAddress("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))

          val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).right.value

          result.carrier.value mustBe expectedResult
        }

        "must not be defined when AddCarrierPage is false" in {

          val userAnswers = fullSafetyAndSecurityUa
            .unsafeSetVal(AddCarrierPage)(false)

          val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).right.value

          result.carrier mustBe None
        }
      }
    }

    "cannot parse from UserAnswers" - {

      "when a mandatory page is missing" in {

        val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
          AddCircumstanceIndicatorPage,
          AddTransportChargesPaymentMethodPage,
          AddCommercialReferenceNumberPage,
          AddSafetyAndSecurityConsignorPage,
          AddSafetyAndSecurityConsigneePage,
          AddCarrierPage
        )

        forAll(mandatoryPages) {
          mandatoryPage =>
            val invalidUa = fullSafetyAndSecurityUa.unsafeRemove(mandatoryPage)
            val result    = UserAnswersReader[SafetyAndSecurity].run(invalidUa).left.value

            result.page mustBe mandatoryPage
        }
      }

      "conveyanceReferenceNumber" - {

        "when ModeAtBorderPage is not '4' or '40' and AddConveyanceReferenceNumberPage is true and ConveyanceReferenceNumberPage is empty" in {

          val modeAtBorder = arb[String]
            .suchThat(
              mode => mode != "4" && mode != "40"
            )
            .sample
            .value

          val invalidUa = fullSafetyAndSecurityUa
            .unsafeSetVal(ModeAtBorderPage)(modeAtBorder)
            .unsafeSetVal(AddConveyanceReferenceNumberPage)(true)
            .unsafeRemove(ConveyanceReferenceNumberPage)

          val result = UserAnswersReader[SafetyAndSecurity].run(invalidUa).left.value

          result.page mustBe ConveyanceReferenceNumberPage
        }

        "when ModeAtBorderPage is '4' or '40' and ConveyanceReferenceNumberPage is empty" in {

          val modeAtBorder = Gen.oneOf(Seq("4", "40")).sample.value

          val invalidUa = fullSafetyAndSecurityUa
            .unsafeSetVal(ModeAtBorderPage)(modeAtBorder)
            .unsafeRemove(ConveyanceReferenceNumberPage)

          val result = UserAnswersReader[SafetyAndSecurity].run(invalidUa).left.value

          result.page mustBe ConveyanceReferenceNumberPage
        }
      }

      "placeOfUnloading" - {

        "when addCircumstanceIndicator is 'E' and AddPlaceOfUnloadingCodePage is empty" in {

          val invalidUa = fullSafetyAndSecurityUa
            .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
            .unsafeSetVal(CircumstanceIndicatorPage)("E")
            .unsafeRemove(AddPlaceOfUnloadingCodePage)

          val result = UserAnswersReader[SafetyAndSecurity].run(invalidUa).left.value

          result.page mustBe AddPlaceOfUnloadingCodePage
        }

        "when addCircumstanceIndicator is 'E' and AddPlaceOfUnloadingCodePage is true and PlaceOfUnloadingCodePage is empty" in {

          val invalidUa = fullSafetyAndSecurityUa
            .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
            .unsafeSetVal(CircumstanceIndicatorPage)("E")
            .unsafeSetVal(AddPlaceOfUnloadingCodePage)(true)
            .unsafeRemove(PlaceOfUnloadingCodePage)

          val result = UserAnswersReader[SafetyAndSecurity].run(invalidUa).left.value

          result.page mustBe PlaceOfUnloadingCodePage
        }

        "when addCircumstanceIndicator is not 'E' and PlaceOfUnloadingCodePage is empty" in {

          val invalidUa = fullSafetyAndSecurityUa
            .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
            .unsafeSetVal(CircumstanceIndicatorPage)("A")
            .unsafeRemove(PlaceOfUnloadingCodePage)

          val result = UserAnswersReader[SafetyAndSecurity].run(invalidUa).left.value

          result.page mustBe PlaceOfUnloadingCodePage
        }
      }

      "consignorDetails" - {

        "when AddSafetyAndSecurityConsignorPage is true and AddSafetyAndSecurityConsignorEoriPage is true and SafetyAndSecurityConsignorEoriPage is empty" in {

          val userAnswers = fullSafetyAndSecurityUa
            .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(true)
            .unsafeSetVal(AddSafetyAndSecurityConsignorEoriPage)(true)
            .unsafeRemove(SafetyAndSecurityConsignorEoriPage)

          val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).left.value

          result.page mustBe SafetyAndSecurityConsignorEoriPage
        }

        "when AddSafetyAndSecurityConsignorPage is true and AddSafetyAndSecurityConsignorEoriPage is false and SafetyAndSecurityConsignorName is empty" in {

          val userAnswers = fullSafetyAndSecurityUa
            .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(true)
            .unsafeSetVal(AddSafetyAndSecurityConsignorEoriPage)(false)
            .unsafeSetVal(SafetyAndSecurityConsignorAddressPage)(CommonAddress("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))
            .unsafeRemove(SafetyAndSecurityConsignorNamePage)

          val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).left.value

          result.page mustBe SafetyAndSecurityConsignorNamePage
        }

        "when AddSafetyAndSecurityConsignorPage is true and AddSafetyAndSecurityConsignorEoriPage is false and SafetyAndSecurityConsignorAddress is empty" in {

          val userAnswers = fullSafetyAndSecurityUa
            .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(true)
            .unsafeSetVal(AddSafetyAndSecurityConsignorEoriPage)(false)
            .unsafeSetVal(SafetyAndSecurityConsignorNamePage)("consignorName")
            .unsafeRemove(SafetyAndSecurityConsignorAddressPage)

          val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).left.value

          result.page mustBe SafetyAndSecurityConsignorAddressPage
        }
      }

      "consigneeDetails" - {

        "when AddSafetyAndSecurityConsigneePage is true and AddSafetyAndSecurityConsigneeEoriPage is true and SafetyAndSecurityConsigneeEoriPage is empty" in {

          val userAnswers = fullSafetyAndSecurityUa
            .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(true)
            .unsafeSetVal(AddSafetyAndSecurityConsigneeEoriPage)(true)
            .unsafeRemove(SafetyAndSecurityConsigneeEoriPage)

          val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).left.value

          result.page mustBe SafetyAndSecurityConsigneeEoriPage
        }

        "when AddSafetyAndSecurityConsigneePage is true and AddSafetyAndSecurityConsigneeEoriPage is false and SafetyAndSecurityConsigneeName is empty" in {

          val userAnswers = fullSafetyAndSecurityUa
            .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(true)
            .unsafeSetVal(AddSafetyAndSecurityConsigneeEoriPage)(false)
            .unsafeSetVal(SafetyAndSecurityConsigneeAddressPage)(CommonAddress("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))
            .unsafeRemove(SafetyAndSecurityConsigneeNamePage)

          val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).left.value

          result.page mustBe SafetyAndSecurityConsigneeNamePage
        }

        "when AddSafetyAndSecurityConsigneePage is true and AddSafetyAndSecurityConsigneeEoriPage is false and SafetyAndSecurityConsigneeAddress is empty" in {

          val userAnswers = fullSafetyAndSecurityUa
            .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(true)
            .unsafeSetVal(AddSafetyAndSecurityConsigneeEoriPage)(false)
            .unsafeSetVal(SafetyAndSecurityConsigneeNamePage)("consigneeName")
            .unsafeRemove(SafetyAndSecurityConsigneeAddressPage)

          val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).left.value

          result.page mustBe SafetyAndSecurityConsigneeAddressPage
        }
      }

      "carrierDetails" - {

        "when AddCarrierPage is true and AddCarrierEoriPage is true and CarrierEoriPage is empty" in {

          val userAnswers = fullSafetyAndSecurityUa
            .unsafeSetVal(AddCarrierPage)(true)
            .unsafeSetVal(AddCarrierEoriPage)(true)
            .unsafeRemove(CarrierEoriPage)

          val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).left.value

          result.page mustBe CarrierEoriPage
        }

        "when AddCarrierPage is true and AddCarrierEoriPage is false and CarrierName is empty" in {

          val userAnswers = fullSafetyAndSecurityUa
            .unsafeSetVal(AddCarrierPage)(true)
            .unsafeSetVal(AddCarrierEoriPage)(false)
            .unsafeSetVal(CarrierAddressPage)(CommonAddress("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))
            .unsafeRemove(CarrierNamePage)

          val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).left.value

          result.page mustBe CarrierNamePage
        }

        "when AddCarrierPage is true and AddCarrierEoriPage is false and CarrierAddressPage is empty" in {

          val userAnswers = fullSafetyAndSecurityUa
            .unsafeSetVal(AddCarrierPage)(true)
            .unsafeSetVal(AddCarrierEoriPage)(false)
            .unsafeSetVal(CarrierNamePage)("consigneeName")
            .unsafeRemove(CarrierAddressPage)

          val result = UserAnswersReader[SafetyAndSecurity].run(userAnswers).left.value

          result.page mustBe CarrierAddressPage
        }
      }
    }
  }
}
