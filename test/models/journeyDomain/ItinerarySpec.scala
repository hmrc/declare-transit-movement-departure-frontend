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
import models.Index
import models.reference.CountryCode
import pages.safetyAndSecurity.CountryOfRoutingPage

class ItinerarySpec extends SpecBase with GeneratorSpec with UserAnswersSpecHelper {

  private val itineraryUa = emptyUserAnswers
    .unsafeSetVal(CountryOfRoutingPage(index))(CountryCode("GB"))

  "Itinerary" - {
    "can be parsed from UserAnswers" - {
      "when all mandatory answer have been answered" in {

        val expectedResult = Itinerary(CountryCode("GB"))

        val result = UserAnswersReader[Itinerary](Itinerary.itineraryReader(index)).run(itineraryUa)

        result.right.value mustEqual expectedResult
      }

      "when there are multiple countries of routing" in {

        val expectedResult = NonEmptyList(
          Itinerary(CountryCode("GB")),
          List(Itinerary(CountryCode("IT")))
        )

        val userAnswers = itineraryUa
          .unsafeSetVal(CountryOfRoutingPage(Index(1)))(CountryCode("IT"))

        val result = UserAnswersReader[NonEmptyList[Itinerary]](Itinerary.readItineraries).run(userAnswers)

        result.right.value mustEqual expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {
      "when a mandatory answer is missing" in {

        val userAnswers = itineraryUa
          .unsafeRemove(CountryOfRoutingPage(index))

        val result = UserAnswersReader[Itinerary](Itinerary.itineraryReader(index)).run(userAnswers)

        result.left.value.page mustEqual CountryOfRoutingPage(index)
      }
    }
  }
}
