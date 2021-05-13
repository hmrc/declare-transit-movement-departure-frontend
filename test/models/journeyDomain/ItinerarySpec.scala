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
import commonTestUtils.UserAnswersSpecHelper
import generators.JourneyModelGenerators
import models.journeyDomain.ItinerarySpec.setItineraryUserAnswers
import models.{Index, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.safetyAndSecurity.CountryOfRoutingPage

class ItinerarySpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {

  "Itinerary" - {
    "can be parsed from UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(arbitrary[Itinerary], arbitrary[UserAnswers]) {
          case (itinerary, userAnswers) =>
            val updatedUserAnswers = setItineraryUserAnswers(itinerary, index)(userAnswers)
            val result             = UserAnswersReader[Itinerary](Itinerary.itineraryReader(index)).run(updatedUserAnswers)

            result.right.value mustEqual itinerary
        }
      }
    }

    "cannot be parsed from UserAnswers" - {
      "when a mandatory answer is missing" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val updatedUserAnswers = userAnswers.remove(CountryOfRoutingPage(index)).success.value
            val result: EitherType[Itinerary] =
              UserAnswersReader[Itinerary](Itinerary.itineraryReader(index)).run(updatedUserAnswers)

            result.left.value.page mustBe CountryOfRoutingPage(index)
        }
      }

    }
  }
}

object ItinerarySpec extends UserAnswersSpecHelper {

  def setItineraries(itineraries: Seq[Itinerary])(userAnswers: UserAnswers): UserAnswers =
    userAnswers.unsafeSetSeqIndex(CountryOfRoutingPage)(itineraries.map(_.countryCode))

  def setItineraryUserAnswers(itinerary: Itinerary, index: Index)(userAnswers: UserAnswers): UserAnswers =
    userAnswers
      .unsafeSetVal(CountryOfRoutingPage(index))(itinerary.countryCode)

}
