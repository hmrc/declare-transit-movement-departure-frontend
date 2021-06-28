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
import models.journeyDomain.RouteDetails.TransitInformation
import models.reference.{CountryCode, CountryOfDispatch, CustomsOffice}
import org.scalacheck.Gen
import pages._

import java.time.LocalDateTime

class RouteDetailsSpec extends SpecBase with GeneratorSpec with UserAnswersSpecHelper {

  "RouteDetails" - {

    "can be parsed from UserAnswers" - {

      "when safetyAndSecurityFlag is true and arrival time is added" in {

        val dateNow = LocalDateTime.now()

        val expectedResult = RouteDetails(
          CountryOfDispatch(CountryCode("GB"), true),
          CountryCode("IT"),
          CustomsOffice("id", "name", CountryCode("IT"), Seq.empty, None),
          NonEmptyList(TransitInformation("transitOffice", Some(dateNow)), List.empty)
        )

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(AddSecurityDetailsPage)(true)
          .unsafeSetVal(CountryOfDispatchPage)(CountryOfDispatch(CountryCode("GB"), true))
          .unsafeSetVal(DestinationCountryPage)(CountryCode("IT"))
          .unsafeSetVal(DestinationOfficePage)(CustomsOffice("id", "name", CountryCode("IT"), Seq.empty, None))
          .unsafeSetVal(AddAnotherTransitOfficePage(index))("transitOffice")
          .unsafeSetVal(ArrivalTimesAtOfficePage(index))(dateNow)

        val result = UserAnswersReader[RouteDetails].run(userAnswers).right.value

        result mustBe expectedResult
      }

      "when safetyAndSecurityFlag is false and arrival time is not added" in {

        val expectedResult = RouteDetails(
          CountryOfDispatch(CountryCode("GB"), true),
          CountryCode("IT"),
          CustomsOffice("id", "name", CountryCode("IT"), Seq.empty, None),
          NonEmptyList(TransitInformation("transitOffice", None), List.empty)
        )

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(AddSecurityDetailsPage)(false)
          .unsafeSetVal(CountryOfDispatchPage)(CountryOfDispatch(CountryCode("GB"), true))
          .unsafeSetVal(DestinationCountryPage)(CountryCode("IT"))
          .unsafeSetVal(DestinationOfficePage)(CustomsOffice("id", "name", CountryCode("IT"), Seq.empty, None))
          .unsafeSetVal(AddAnotherTransitOfficePage(index))("transitOffice")

        val result = UserAnswersReader[RouteDetails].run(userAnswers).right.value

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
          ArrivalTimesAtOfficePage(index)
        )

        forAll(mandatoryPages) {
          mandatoryPage =>
            val dateNow = LocalDateTime.now()

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(CountryOfDispatchPage)(CountryOfDispatch(CountryCode("GB"), true))
              .unsafeSetVal(DestinationCountryPage)(CountryCode("IT"))
              .unsafeSetVal(DestinationOfficePage)(CustomsOffice("id", "name", CountryCode("IT"), Seq.empty, None))
              .unsafeSetVal(AddAnotherTransitOfficePage(index))("transitOffice")
              .unsafeSetVal(ArrivalTimesAtOfficePage(index))(dateNow)
              .unsafeRemove(mandatoryPage)

            val result = UserAnswersReader[RouteDetails].run(userAnswers).left.value

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
              .unsafeSetVal(AddSecurityDetailsPage)(false)
              .unsafeSetVal(CountryOfDispatchPage)(CountryOfDispatch(CountryCode("GB"), true))
              .unsafeSetVal(DestinationCountryPage)(CountryCode("IT"))
              .unsafeSetVal(DestinationOfficePage)(CustomsOffice("id", "name", CountryCode("IT"), Seq.empty, None))
              .unsafeSetVal(AddAnotherTransitOfficePage(index))("transitOffice")
              .unsafeRemove(mandatoryPage)

            val result = UserAnswersReader[RouteDetails].run(userAnswers).left.value

            result.page mustBe mandatoryPage
        }
      }
    }
  }
}
