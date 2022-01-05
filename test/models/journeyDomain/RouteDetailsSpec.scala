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

package models.journeyDomain

import base.{GeneratorSpec, SpecBase}
import cats.data.NonEmptyList
import commonTestUtils.UserAnswersSpecHelper
import models.DeclarationType
import models.journeyDomain.RouteDetailsWithTransitInformation.TransitInformation
import models.reference.{CountryCode, CountryOfDispatch, CustomsOffice}
import org.scalacheck.Gen
import pages._
import pages.routeDetails._

import java.time.LocalDateTime

class RouteDetailsSpec extends SpecBase with GeneratorSpec with UserAnswersSpecHelper {

  "RouteDetailsReader" - {
    "Can parse a route detail long journey" in {
      val dateNow = LocalDateTime.now()

      val generatedOption = Gen.oneOf(DeclarationType.Option1, DeclarationType.Option2, DeclarationType.Option3).sample.value

      val expectedResult = RouteDetailsWithTransitInformation(
        CountryOfDispatch(CountryCode("GB"), true),
        CountryCode("IT"),
        CustomsOffice("id", "name", CountryCode("IT"), None),
        Some(NonEmptyList(TransitInformation("transitOffice", Some(dateNow)), List.empty))
      )

      val userAnswers = emptyUserAnswers
        .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("GB"), None))
        .unsafeSetVal(AddSecurityDetailsPage)(true)
        .unsafeSetVal(DestinationOfficePage)(CustomsOffice("GB", "Name", CountryCode("GB"), None))
        .unsafeSetVal(CountryOfDispatchPage)(CountryOfDispatch(CountryCode("GB"), true))
        .unsafeSetVal(DestinationCountryPage)(CountryCode("IT"))
        .unsafeSetVal(DestinationOfficePage)(CustomsOffice("id", "name", CountryCode("IT"), None))
        .unsafeSetVal(AddAnotherTransitOfficePage(index))("transitOffice")
        .unsafeSetVal(DeclarationTypePage)(generatedOption)
        .unsafeSetVal(ArrivalDatesAtOfficePage(index))(dateNow)

      val result = UserAnswersReader[RouteDetails].run(userAnswers).value

      result mustBe expectedResult
    }

    "Can parse a route detail short journey" in {

      val expectedResult = RouteDetailsWithoutTransitInformation(
        CountryOfDispatch(CountryCode("GB"), true),
        CountryCode("IT"),
        CustomsOffice("id", "name", CountryCode("IT"), None)
      )

      val userAnswers = emptyUserAnswers
        .unsafeSetVal(CountryOfDispatchPage)(CountryOfDispatch(CountryCode("GB"), true))
        .unsafeSetVal(DestinationCountryPage)(CountryCode("IT"))
        .unsafeSetVal(DestinationOfficePage)(CustomsOffice("id", "name", CountryCode("IT"), None))
        .unsafeSetVal(DeclarationTypePage)(DeclarationType.Option4)

      val result = UserAnswersReader[RouteDetails].run(userAnswers).value

      result mustBe expectedResult
    }
  }

  "RouteDetailsLongJourney" - {

    "can be parsed from UserAnswers" - {

      "when safetyAndSecurityFlag is true and arrival time is added when office of transit is added" in {

        val dateNow = LocalDateTime.now()

        val expectedResult = RouteDetailsWithTransitInformation(
          CountryOfDispatch(CountryCode("GB"), true),
          CountryCode("IT"),
          CustomsOffice("id", "name", CountryCode("IT"), None),
          Some(NonEmptyList(TransitInformation("transitOffice", Some(dateNow)), List.empty))
        )

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("GB"), None))
          .unsafeSetVal(AddSecurityDetailsPage)(true)
          .unsafeSetVal(DestinationOfficePage)(CustomsOffice("GB", "Name", CountryCode("GB"), None))
          .unsafeSetVal(CountryOfDispatchPage)(CountryOfDispatch(CountryCode("GB"), true))
          .unsafeSetVal(DestinationCountryPage)(CountryCode("IT"))
          .unsafeSetVal(DestinationOfficePage)(CustomsOffice("id", "name", CountryCode("IT"), None))
          .unsafeSetVal(AddAnotherTransitOfficePage(index))("transitOffice")
          .unsafeSetVal(ArrivalDatesAtOfficePage(index))(dateNow)

        val result = UserAnswersReader[RouteDetailsWithTransitInformation].run(userAnswers).value

        result mustBe expectedResult
      }

      "when safetyAndSecurityFlag is true when office of transit is not added" in {

        val expectedResult = RouteDetailsWithTransitInformation(
          CountryOfDispatch(CountryCode("GB"), true),
          CountryCode("IT"),
          CustomsOffice("XI", "name", CountryCode("XI"), None),
          None
        )

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("XI"), None))
          .unsafeSetVal(AddSecurityDetailsPage)(true)
          .unsafeSetVal(DestinationOfficePage)(CustomsOffice("XI", "name", CountryCode("XI"), None))
          .unsafeSetVal(CountryOfDispatchPage)(CountryOfDispatch(CountryCode("GB"), true))
          .unsafeSetVal(DestinationCountryPage)(CountryCode("IT"))
          .unsafeSetVal(AddOfficeOfTransitPage)(false)

        val result = UserAnswersReader[RouteDetailsWithTransitInformation].run(userAnswers).value

        result mustBe expectedResult
      }

      "when safetyAndSecurityFlag is false and arrival time is not added" in {

        val expectedResult = RouteDetailsWithTransitInformation(
          CountryOfDispatch(CountryCode("GB"), true),
          CountryCode("IT"),
          CustomsOffice("id", "name", CountryCode("IT"), None),
          None
        )

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("XI"), None))
          .unsafeSetVal(AddSecurityDetailsPage)(false)
          .unsafeSetVal(CountryOfDispatchPage)(CountryOfDispatch(CountryCode("GB"), true))
          .unsafeSetVal(DestinationCountryPage)(CountryCode("IT"))
          .unsafeSetVal(DestinationOfficePage)(CustomsOffice("id", "name", CountryCode("IT"), None))
          .unsafeSetVal(AddAnotherTransitOfficePage(index))("transitOffice")
          .unsafeSetVal(AddOfficeOfTransitPage)(false)

        val result = UserAnswersReader[RouteDetailsWithTransitInformation].run(userAnswers).value

        result mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when safetyAndSecurityFlag is true and a mandatory page is missing" in {

        val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
          AddSecurityDetailsPage,
          CountryOfDispatchPage,
          DestinationCountryPage,
          DestinationOfficePage,
          AddAnotherTransitOfficePage(index),
          ArrivalDatesAtOfficePage(index)
        )

        forAll(mandatoryPages) {
          mandatoryPage =>
            val dateNow = LocalDateTime.now()

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("GB"), None))
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(CountryOfDispatchPage)(CountryOfDispatch(CountryCode("GB"), true))
              .unsafeSetVal(DestinationCountryPage)(CountryCode("IT"))
              .unsafeSetVal(DestinationOfficePage)(CustomsOffice("id", "name", CountryCode("IT"), None))
              .unsafeSetVal(AddAnotherTransitOfficePage(index))("transitOffice")
              .unsafeSetVal(ArrivalDatesAtOfficePage(index))(dateNow)
              .unsafeRemove(mandatoryPage)

            val result = UserAnswersReader[RouteDetailsWithTransitInformation].run(userAnswers).left.value

            result.page mustBe mandatoryPage
        }

      }

      "when safetyAndSecurityFlag is false and a mandatory page is missing" in {

        val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
          AddSecurityDetailsPage,
          CountryOfDispatchPage,
          DestinationCountryPage,
          DestinationOfficePage,
          AddAnotherTransitOfficePage(index)
        )

        forAll(mandatoryPages) {
          mandatoryPage =>
            val userAnswers = emptyUserAnswers
              .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("GB"), None))
              .unsafeSetVal(AddSecurityDetailsPage)(false)
              .unsafeSetVal(CountryOfDispatchPage)(CountryOfDispatch(CountryCode("GB"), true))
              .unsafeSetVal(DestinationCountryPage)(CountryCode("IT"))
              .unsafeSetVal(DestinationOfficePage)(CustomsOffice("id", "name", CountryCode("IT"), None))
              .unsafeSetVal(AddAnotherTransitOfficePage(index))("transitOffice")
              .unsafeRemove(mandatoryPage)

            val result = UserAnswersReader[RouteDetailsWithTransitInformation].run(userAnswers).left.value

            result.page mustBe mandatoryPage
        }
      }
    }
  }

  "RouteDetailsShortJourney" - {

    "can be parsed from UserAnswers" - {

      "when all questions have been answered" in {

        val expectedResult = RouteDetailsWithoutTransitInformation(
          CountryOfDispatch(CountryCode("GB"), true),
          CountryCode("IT"),
          CustomsOffice("id", "name", CountryCode("IT"), None)
        )

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(CountryOfDispatchPage)(CountryOfDispatch(CountryCode("GB"), true))
          .unsafeSetVal(DestinationCountryPage)(CountryCode("IT"))
          .unsafeSetVal(DestinationOfficePage)(CustomsOffice("id", "name", CountryCode("IT"), None))

        val result = UserAnswersReader[RouteDetailsWithoutTransitInformation].run(userAnswers).value

        result mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when a mandatory page is missing" in {

        val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
          CountryOfDispatchPage,
          DestinationCountryPage,
          DestinationOfficePage
        )

        forAll(mandatoryPages) {
          mandatoryPage =>
            val userAnswers = emptyUserAnswers
              .unsafeSetVal(CountryOfDispatchPage)(CountryOfDispatch(CountryCode("GB"), true))
              .unsafeSetVal(DestinationCountryPage)(CountryCode("IT"))
              .unsafeSetVal(DestinationOfficePage)(CustomsOffice("id", "name", CountryCode("IT"), None))
              .unsafeRemove(mandatoryPage)

            val result = UserAnswersReader[RouteDetailsWithTransitInformation].run(userAnswers).left.value

            result.page mustBe mandatoryPage
        }
      }
    }
  }
}
