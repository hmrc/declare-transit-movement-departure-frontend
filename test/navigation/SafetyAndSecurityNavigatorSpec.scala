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

package navigation

import base.SpecBase
import controllers.safetyAndSecurity.routes
import generators.Generators
import models.reference.{CountryCode, MethodOfPayment}
import models.{CheckMode, Index, NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck._
import pages.ModeAtBorderPage
import pages.safetyAndSecurity._
import queries.CountriesOfRoutingQuery

class SafetyAndSecurityNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {
  // format: off
  val navigator = new SafetyAndSecurityNavigator

  "SafetyAndSecurity section" - {

    "in NormalMode" - {

      "must go from AddCircumstanceIndicator page to CircumstanceIndicator page if 'true'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddCircumstanceIndicatorPage, true).success.value

            navigator
              .nextPage(AddCircumstanceIndicatorPage, NormalMode, updatedAnswers)
              .mustBe(routes.CircumstanceIndicatorController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from AddCircumstanceIndicator to AddTransportChargesPaymentMethod if 'false'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddCircumstanceIndicatorPage, false).success.value

            navigator
              .nextPage(AddCircumstanceIndicatorPage, NormalMode, updatedAnswers)
              .mustBe(routes.AddTransportChargesPaymentMethodController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from CircumstanceIndicator to AddTransportChargesPaymentMethod" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(CircumstanceIndicatorPage, NormalMode, answers)
              .mustBe(routes.AddTransportChargesPaymentMethodController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from AddTransportChargesPaymentMethod to TransportChargesPaymentMethod if 'true'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddTransportChargesPaymentMethodPage, true).success.value

            navigator
              .nextPage(AddTransportChargesPaymentMethodPage, NormalMode, updatedAnswers)
              .mustBe(routes.TransportChargesPaymentMethodController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from AddTransportChargesPaymentMethod to AddCommercialReferenceNumber if 'false'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddTransportChargesPaymentMethodPage, false).success.value

            navigator
              .nextPage(AddTransportChargesPaymentMethodPage, NormalMode, updatedAnswers)
              .mustBe(routes.AddCommercialReferenceNumberController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from TransportChargesPaymentMethod to AddCommercialReferenceNumber" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(TransportChargesPaymentMethodPage, NormalMode, answers)
              .mustBe(routes.AddCommercialReferenceNumberController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from AddCommercialReferenceNumber to AddCommercialReferenceNumberAllItems if 'true'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddCommercialReferenceNumberPage, true).success.value

            navigator
              .nextPage(AddCommercialReferenceNumberPage, NormalMode, updatedAnswers)
              .mustBe(routes.AddCommercialReferenceNumberAllItemsController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from AddCommercialReferenceNumber to AddConveyanceReferenceNumber if 'false' and if transport mode at border is not 4 or 40" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddCommercialReferenceNumberPage, false).success.value
              .set(ModeAtBorderPage, "1").success.value

            navigator
              .nextPage(AddCommercialReferenceNumberPage, NormalMode, updatedAnswers)
              .mustBe(routes.AddConveyanceReferenceNumberController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from AddCommercialReferenceNumber to ConveyanceReferenceNumber if 'false' and if transport mode at border 4 or 40" in {

        val genTransportMode: Gen[String] = Gen.oneOf(Seq("4", "40"))

        forAll(arbitrary[UserAnswers], genTransportMode) {
          (answers, transportMode) =>

            val updatedAnswers = answers
              .set(AddCommercialReferenceNumberPage, false).success.value
              .set(ModeAtBorderPage, transportMode).success.value

            navigator
              .nextPage(AddCommercialReferenceNumberPage, NormalMode, updatedAnswers)
              .mustBe(routes.ConveyanceReferenceNumberController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from AddCommercialReferenceNumberAllItems to CommercialReferenceNumberAllItems when 'true'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddCommercialReferenceNumberAllItemsPage, true).success.value

            navigator
              .nextPage(AddCommercialReferenceNumberAllItemsPage, NormalMode, updatedAnswers)
              .mustBe(routes.CommercialReferenceNumberAllItemsController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from AddCommercialReferenceNumberAllItems to AddConveyanceReferenceNumber when 'false' and if transport mode at border is not 4 or 40" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddCommercialReferenceNumberAllItemsPage, false).success.value
              .set(ModeAtBorderPage, "1").success.value

            navigator
              .nextPage(AddCommercialReferenceNumberAllItemsPage, NormalMode, updatedAnswers)
              .mustBe(routes.AddConveyanceReferenceNumberController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from AddCommercialReferenceNumberAllItems to ConveyanceReferenceNumber when 'false' and if transport mode at border is 4 or 40" in {

        val genTransportMode: Gen[String] = Gen.oneOf(Seq("4", "40"))

        forAll(arbitrary[UserAnswers], genTransportMode) {
          (answers, transportMode) =>

            val updatedAnswers = answers
              .set(AddCommercialReferenceNumberAllItemsPage, false).success.value
              .set(ModeAtBorderPage, transportMode).success.value

            navigator
              .nextPage(AddCommercialReferenceNumberAllItemsPage, NormalMode, updatedAnswers)
              .mustBe(routes.ConveyanceReferenceNumberController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from commercialReferenceNumberAllItems to ConveyanceReferenceNumber if transport mode at border is 4 or 40" in {

        val genTransportMode: Gen[String] = Gen.oneOf(Seq("4", "40"))

        forAll(arbitrary[UserAnswers], genTransportMode) {
          (answers, transportMode) =>

            val updatedAnswers = answers
              .set(ModeAtBorderPage, transportMode).success.value

            navigator
              .nextPage(CommercialReferenceNumberAllItemsPage, NormalMode, updatedAnswers)
              .mustBe(routes.ConveyanceReferenceNumberController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from commercialReferenceNumberAllItems to AddConveyanceReferenceNumber if transport mode at border is not 4 or 40" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(ModeAtBorderPage, "1").success.value

            navigator
              .nextPage(CommercialReferenceNumberAllItemsPage, NormalMode, updatedAnswers)
              .mustBe(routes.AddConveyanceReferenceNumberController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from commercialReferenceNumberAllItems to AddConveyanceReferenceNumber if transport mode at border is not completed" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .remove(ModeAtBorderPage).success.value

            navigator
              .nextPage(CommercialReferenceNumberAllItemsPage, NormalMode, updatedAnswers)
              .mustBe(routes.AddConveyanceReferenceNumberController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from AddConveyanceReferenceNumber to ConveyanceReferenceNumber if 'true'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddConveyanceReferenceNumberPage, true).success.value

            navigator
              .nextPage(AddConveyanceReferenceNumberPage, NormalMode, updatedAnswers)
              .mustBe(routes.ConveyanceReferenceNumberController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from AddConveyanceReferenceNumber to AddPlaceOfUnloadingCode if 'false' and CircumstanceIndicator is '(E) Authorised Economic Operators'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddConveyanceReferenceNumberPage, false).success.value
              .set(CircumstanceIndicatorPage, "E").success.value

            navigator
              .nextPage(AddConveyanceReferenceNumberPage, NormalMode, updatedAnswers)
              .mustBe(routes.AddPlaceOfUnloadingCodeController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from AddConveyanceReferenceNumber to PlaceOfUnloadingCode if 'false' and CircumstanceIndicator is not '(E) Authorised Economic Operators'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddConveyanceReferenceNumberPage, false).success.value
              .set(CircumstanceIndicatorPage, "A").success.value

            navigator
              .nextPage(AddConveyanceReferenceNumberPage, NormalMode, updatedAnswers)
              .mustBe(routes.PlaceOfUnloadingCodeController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from ConveyanceReferenceNumber to AddPlaceOfUnloadingCode if 'false' and CircumstanceIndicator is '(E) Authorised Economic Operators'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(ConveyanceReferenceNumberPage, "answer").success.value
              .set(CircumstanceIndicatorPage, "E").success.value

            navigator
              .nextPage(ConveyanceReferenceNumberPage, NormalMode, updatedAnswers)
              .mustBe(routes.AddPlaceOfUnloadingCodeController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from ConveyanceReferenceNumber to PlaceOfUnloadingCode if 'false' and CircumstanceIndicator is not '(E) Authorised Economic Operators'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(ConveyanceReferenceNumberPage, "answer").success.value
              .set(CircumstanceIndicatorPage, "A").success.value

            navigator
              .nextPage(ConveyanceReferenceNumberPage, NormalMode, updatedAnswers)
              .mustBe(routes.PlaceOfUnloadingCodeController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from AddPlaceOfUnloadingCode to PlaceOfUnloadingCode if 'true'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddPlaceOfUnloadingCodePage, true).success.value

            navigator
              .nextPage(AddPlaceOfUnloadingCodePage, NormalMode, updatedAnswers)
              .mustBe(routes.PlaceOfUnloadingCodeController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from AddPlaceOfUnloadingCode to CountryOfRouting if 'false'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .remove(CountryOfRoutingPage(index)).success.value
              .set(AddPlaceOfUnloadingCodePage, false).success.value

            navigator
              .nextPage(AddPlaceOfUnloadingCodePage, NormalMode, updatedAnswers)
              .mustBe(routes.CountryOfRoutingController.onPageLoad(answers.lrn, index, NormalMode))
        }
      }

      "must go from PlaceOfUnloadingCode to CountryOfRouting if there is no specified CountryOfRouting already" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(PlaceOfUnloadingCodePage, "answer").success.value
              .remove(CountryOfRoutingPage(index)).success.value

            navigator
              .nextPage(PlaceOfUnloadingCodePage, NormalMode, updatedAnswers)
              .mustBe(routes.CountryOfRoutingController.onPageLoad(answers.lrn, index, NormalMode))
        }
      }

      "must go from PlaceOfUnloadingCode to AddAnotherCountryOfRouting if there is a specified CountryOfRouting already" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(PlaceOfUnloadingCodePage, "answer").success.value
              .set(CountryOfRoutingPage(index), CountryCode("GB")).success.value

            navigator
              .nextPage(PlaceOfUnloadingCodePage, NormalMode, updatedAnswers)
              .mustBe(routes.AddAnotherCountryOfRoutingController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from CountryOfRouting to AddAnotherCountryOfRouting" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(CountryOfRoutingPage(index), NormalMode, answers)
              .mustBe(routes.AddAnotherCountryOfRoutingController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from AddAnotherCountryOfRouting to CountryOfRouting if 'true'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .remove(CountryOfRoutingPage(index)).success.value
              .set(AddAnotherCountryOfRoutingPage, true).success.value

            navigator
              .nextPage(AddAnotherCountryOfRoutingPage, NormalMode, updatedAnswers)
              .mustBe(routes.CountryOfRoutingController.onPageLoad(answers.lrn, index, NormalMode))
        }
      }

      "must go from AddAnotherCountryOfRouting to AddSafetyAndSecurityConsignor if 'false'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddAnotherCountryOfRoutingPage, false).success.value

            navigator
              .nextPage(AddAnotherCountryOfRoutingPage, NormalMode, updatedAnswers)
              .mustBe(routes.AddSafetyAndSecurityConsignorController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from ConfirmRemoveCountry page to " - {

        "AddAnotherCountryOfRouting page when 'No' is selected and there are more than one country" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
            val updatedAnswers = answers
              .set(CountryOfRoutingPage(index), CountryCode("GB")).success.value
              .set(CountryOfRoutingPage(Index(1)), CountryCode("IT")).success.value
              .set(AddAnotherCountryOfRoutingPage, true).success.value
              .set(ConfirmRemoveCountryPage, false).success.value
              navigator
                .nextPage(ConfirmRemoveCountryPage, NormalMode, updatedAnswers)
                .mustBe(routes.AddAnotherCountryOfRoutingController.onPageLoad(updatedAnswers.lrn, NormalMode))
          }
        }

        "AddAnotherCountryOfRouting page when 'Yes' is selected and there are more than one country" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(CountryOfRoutingPage(index), CountryCode("GB")).success.value
                .set(CountryOfRoutingPage(Index(1)), CountryCode("IT")).success.value
                .set(ConfirmRemoveCountryPage, true).success.value
              navigator
                .nextPage(ConfirmRemoveCountryPage, NormalMode, updatedAnswers)
                .mustBe(routes.AddAnotherCountryOfRoutingController.onPageLoad(updatedAnswers.lrn, NormalMode))
          }
        }

        "CountryOfRouting page when 'Yes' is selected and and when all the countries are removed" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .remove(CountriesOfRoutingQuery()).success.value
              navigator
                .nextPage(ConfirmRemoveCountryPage, NormalMode, updatedAnswers)
                .mustBe(routes.CountryOfRoutingController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
          }
        }
      }
    }

    "in CheckMode" - {

      "must go from AddCircumstanceIndicator page to" - {
        "CircumstanceIndicator page if 'true'" in {

          val updatedAnswers = emptyUserAnswers
            .set(AddCircumstanceIndicatorPage, true).success.value

          navigator
            .nextPage(AddCircumstanceIndicatorPage, CheckMode, updatedAnswers)
            .mustBe(routes.CircumstanceIndicatorController.onPageLoad(updatedAnswers.lrn, CheckMode))
        }

        "CheckYourAnswers page if 'true' and CircumstanceIndicator answer exists" in {

          val updatedAnswers = emptyUserAnswers
            .set(CircumstanceIndicatorPage, "value").success.value
            .set(AddCircumstanceIndicatorPage, true).success.value

          navigator
            .nextPage(AddCircumstanceIndicatorPage, CheckMode, updatedAnswers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }

        "CheckYourAnswers if 'false'" in {

          val updatedAnswers = emptyUserAnswers
            .set(AddCircumstanceIndicatorPage, false).success.value

          navigator
            .nextPage(AddCircumstanceIndicatorPage, CheckMode, updatedAnswers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }
      }

      "must go from CircumstanceIndicator page to CheckYourAnswers" in {

        navigator
          .nextPage(CircumstanceIndicatorPage, CheckMode, emptyUserAnswers)
          .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(emptyUserAnswers.lrn))
      }

      "must go from AddTransportChargesPaymentMethod page to" - {
        "TransportChargesPaymentMethod page if 'true'" in {

          val updatedAnswers = emptyUserAnswers
            .set(AddTransportChargesPaymentMethodPage, true).success.value

          navigator
            .nextPage(AddTransportChargesPaymentMethodPage, CheckMode, updatedAnswers)
            .mustBe(routes.TransportChargesPaymentMethodController.onPageLoad(updatedAnswers.lrn, CheckMode))
        }

        "CheckYourAnswers page if 'true' and TransportChargesPaymentMethod answer exists" in {

          val updatedAnswers = emptyUserAnswers
            .set(TransportChargesPaymentMethodPage, MethodOfPayment("code", "description")).success.value
            .set(AddTransportChargesPaymentMethodPage, true).success.value

          navigator
            .nextPage(AddTransportChargesPaymentMethodPage, CheckMode, updatedAnswers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }

        "CheckYourAnswers if 'false'" in {

          val updatedAnswers = emptyUserAnswers
            .set(AddTransportChargesPaymentMethodPage, false).success.value

          navigator
            .nextPage(AddTransportChargesPaymentMethodPage, CheckMode, updatedAnswers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }
      }

      "must go from TransportChargesPaymentMethod page to CheckYourAnswers" in {

        navigator
          .nextPage(TransportChargesPaymentMethodPage, CheckMode, emptyUserAnswers)
          .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(emptyUserAnswers.lrn))
      }

      "must go from AddCommercialReferenceNumber page to" - {
        "AddCommercialReferenceNumberAllItems page if 'true'" in {

          val updatedAnswers = emptyUserAnswers
            .set(AddCommercialReferenceNumberPage, true).success.value

          navigator
            .nextPage(AddCommercialReferenceNumberPage, CheckMode, updatedAnswers)
            .mustBe(routes.AddCommercialReferenceNumberAllItemsController.onPageLoad(updatedAnswers.lrn, CheckMode))
        }

        "CheckYourAnswers page if 'true' and AddCommercialReferenceNumberAllItems answer is 'true'" in {

          val updatedAnswers = emptyUserAnswers
            .set(AddCommercialReferenceNumberAllItemsPage, true).success.value
            .set(AddCommercialReferenceNumberPage, true).success.value

          navigator
            .nextPage(AddCommercialReferenceNumberPage, CheckMode, updatedAnswers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }

        "CheckYourAnswers page if 'true' and AddCommercialReferenceNumberAllItems answer is 'false'" in {

          val updatedAnswers = emptyUserAnswers
            .set(AddCommercialReferenceNumberAllItemsPage, false).success.value
            .set(AddCommercialReferenceNumberPage, true).success.value

          navigator
            .nextPage(AddCommercialReferenceNumberPage, CheckMode, updatedAnswers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }

        "CheckYourAnswers if 'false'" in {

          val updatedAnswers = emptyUserAnswers
            .set(AddCommercialReferenceNumberPage, false).success.value

          navigator
            .nextPage(AddCommercialReferenceNumberPage, CheckMode, updatedAnswers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }
      }

      "must go from AddCommercialReferenceNumberAllItems page to" - {
        "CommercialReferenceNumberAllItems page if 'true'" in {

          val updatedAnswers = emptyUserAnswers
            .set(AddCommercialReferenceNumberAllItemsPage, true).success.value

          navigator
            .nextPage(AddCommercialReferenceNumberAllItemsPage, CheckMode, updatedAnswers)
            .mustBe(routes.CommercialReferenceNumberAllItemsController.onPageLoad(updatedAnswers.lrn, CheckMode))
        }

        "CheckYourAnswers page if 'true' and CommercialReferenceNumberAllItems answer exists" in {

          val updatedAnswers = emptyUserAnswers
            .set(CommercialReferenceNumberAllItemsPage, "value").success.value
            .set(AddCommercialReferenceNumberAllItemsPage, true).success.value

          navigator
            .nextPage(AddCommercialReferenceNumberAllItemsPage, CheckMode, updatedAnswers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }

        "CheckYourAnswers if 'false'" in {

          val updatedAnswers = emptyUserAnswers
            .set(AddCommercialReferenceNumberAllItemsPage, false).success.value

          navigator
            .nextPage(AddCommercialReferenceNumberAllItemsPage, CheckMode, updatedAnswers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }
      }

      "must go from CommercialReferenceNumberAllItems page to CheckYourAnswers" in {

        navigator
          .nextPage(CommercialReferenceNumberAllItemsPage, CheckMode, emptyUserAnswers)
          .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(emptyUserAnswers.lrn))
      }

      "must go from AddConveyanceReferenceNumber page to" - {
        "ConveyanceReferenceNumber page if 'true'" in {

          val updatedAnswers = emptyUserAnswers
            .set(AddConveyanceReferenceNumberPage, true).success.value

          navigator
            .nextPage(AddConveyanceReferenceNumberPage, CheckMode, updatedAnswers)
            .mustBe(routes.ConveyanceReferenceNumberController.onPageLoad(updatedAnswers.lrn, CheckMode))
        }

        "CheckYourAnswers page if 'true' and ConveyanceReferenceNumber answer exists" in {

          val updatedAnswers = emptyUserAnswers
            .set(ConveyanceReferenceNumberPage, "value").success.value
            .set(AddConveyanceReferenceNumberPage, true).success.value

          navigator
            .nextPage(AddConveyanceReferenceNumberPage, CheckMode, updatedAnswers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }

        "CheckYourAnswers if 'false'" in {

          val updatedAnswers = emptyUserAnswers
            .set(AddConveyanceReferenceNumberPage, false).success.value

          navigator
            .nextPage(AddConveyanceReferenceNumberPage, CheckMode, updatedAnswers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }
      }

      "must go from ConveyanceReferenceNumber page to CheckYourAnswers" in {

        navigator
          .nextPage(ConveyanceReferenceNumberPage, CheckMode, emptyUserAnswers)
          .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(emptyUserAnswers.lrn))
      }

      "must go from AddPlaceOfUnloadingCode page to" - {
        "PlaceOfUnloadingCode page if 'true'" in {

          val updatedAnswers = emptyUserAnswers
            .set(AddPlaceOfUnloadingCodePage, true).success.value

          navigator
            .nextPage(AddPlaceOfUnloadingCodePage, CheckMode, updatedAnswers)
            .mustBe(routes.PlaceOfUnloadingCodeController.onPageLoad(updatedAnswers.lrn, CheckMode))
        }

        "CheckYourAnswers page if 'true' and PlaceOfUnloadingCode answer exists" in {

          val updatedAnswers = emptyUserAnswers
            .set(PlaceOfUnloadingCodePage, "value").success.value
            .set(AddPlaceOfUnloadingCodePage, true).success.value

          navigator
            .nextPage(AddPlaceOfUnloadingCodePage, CheckMode, updatedAnswers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }

        "CheckYourAnswers if 'false'" in {

          val updatedAnswers = emptyUserAnswers
            .set(AddPlaceOfUnloadingCodePage, false).success.value

          navigator
            .nextPage(AddPlaceOfUnloadingCodePage, CheckMode, updatedAnswers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }
      }

      "must go from PlaceOfUnloadingCode page to CheckYourAnswers" in {

        navigator
          .nextPage(PlaceOfUnloadingCodePage, CheckMode, emptyUserAnswers)
          .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(emptyUserAnswers.lrn))
      }

      "must go from CountryOfRouting page to CheckYourAnswers" in {

        navigator
          .nextPage(CountryOfRoutingPage(index), CheckMode, emptyUserAnswers)
          .mustBe(routes.AddAnotherCountryOfRoutingController.onPageLoad(emptyUserAnswers.lrn, CheckMode))
      }

      "must go from AddAnotherCountryOfRouting page to" - {
        "CountryOfRouting page if 'true'" in {

          val updatedAnswers = emptyUserAnswers
            .set(AddAnotherCountryOfRoutingPage, true).success.value

          navigator
            .nextPage(AddAnotherCountryOfRoutingPage, CheckMode, updatedAnswers)
            .mustBe(routes.CountryOfRoutingController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
        }

        "CheckYourAnswers if 'false'" in {

          val updatedAnswers = emptyUserAnswers
            .set(AddAnotherCountryOfRoutingPage, false).success.value

          navigator
            .nextPage(AddAnotherCountryOfRoutingPage, CheckMode, updatedAnswers)
            .mustBe(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }
      }
    }
    // format: on
  }
}
