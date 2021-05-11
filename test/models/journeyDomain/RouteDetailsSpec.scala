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
import models.journeyDomain.RouteDetails.TransitInformation
import models.{Index, UserAnswers}
import pages._

class RouteDetailsSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {
  import RouteDetailsSpec._

  "RouteDetails" - {
    "can be constructed when all the answers have been answered and safetyAndSecurityFlag is true" in {

      forAll(arbitraryRouteDetails(true).arbitrary, arb[UserAnswers]) {
        (expected, baseUserAnswers) =>
          val userAnswers = setRouteDetails(expected)(baseUserAnswers)
            .unsafeSetVal(AddSecurityDetailsPage)(true)

          val result = UserAnswersReader[RouteDetails].run(userAnswers).right.value

          result.transitInformation.forall(_.arrivalTime.isDefined) mustBe true
          result mustEqual expected
      }
    }
  }

  "can be constructed when all the answers have been answered and safetyAndSecurityFlag is false" in {

    forAll(arbitraryRouteDetails(false).arbitrary, arb[UserAnswers]) {
      (expected, baseUserAnswers) =>
        val userAnswers = setRouteDetails(expected)(baseUserAnswers)
          .unsafeSetVal(AddSecurityDetailsPage)(false)

        val result = UserAnswersReader[RouteDetails].run(userAnswers).right.value

        result.transitInformation.forall(_.arrivalTime.isDefined) mustBe false
        result mustEqual expected
    }
  }

}

object RouteDetailsSpec extends UserAnswersSpecHelper {

  def setRouteDetails(routeDetails: RouteDetails)(startUserAnswers: UserAnswers): UserAnswers = {
    val interstitialUserAnswers =
      startUserAnswers
        .unsafeSetVal(CountryOfDispatchPage)(routeDetails.countryOfDispatch)
        .unsafeSetVal(OfficeOfDeparturePage)(routeDetails.officeOfDeparture)
        .unsafeSetVal(DestinationCountryPage)(routeDetails.destinationCountry)
        .unsafeSetVal(DestinationOfficePage)(routeDetails.destinationOffice)

    // TODO replace with unsafeSetSeq

    val userAnswers = routeDetails.transitInformation.zipWithIndex.foldLeft(interstitialUserAnswers) {
      case (ua, (TransitInformation(transitOffice, arrivalTime), index)) =>
        ua.unsafeSetVal(AddAnotherTransitOfficePage(Index(index)))(transitOffice)
          .unsafeSetOpt(ArrivalTimesAtOfficePage(Index(index)))(arrivalTime)
    }

    userAnswers
  }
}
