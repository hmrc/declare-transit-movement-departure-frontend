/*
 * Copyright 2020 HM Revenue & Customs
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

import java.time.LocalDateTime

import base.{GeneratorSpec, SpecBase, UserAnswersSpecHelper}
import generators.JourneyModelGenerators
import models.journeyDomain.RouteDetails.TransitInformation
import models.{Index, LocalDateTimeWithAMPM, UserAnswers}
import org.scalacheck.Arbitrary
import pages._

class RouteDetailsSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {
  import RouteDetailsSpec._

  "RouteDetails" - {
    "can be constructed when all the answers have been answered" in {
      forAll(arb[Option[LocalDateTime]]) {
        dateTime =>
          implicit val constTransitInformation =
            Arbitrary(arb[TransitInformation].map(_.copy(arrivalTime = dateTime)))

          forAll(arbitraryRouteDetails(constTransitInformation).arbitrary, arb[UserAnswers]) {
            (expected, baseUserAnswers) =>
              val userAnswers = setRouteDetails(expected, None)(baseUserAnswers)

              val result = UserAnswersParser[Option, RouteDetails].run(userAnswers).value

              result mustEqual expected
          }
      }
    }
  }

}

object RouteDetailsSpec extends UserAnswersSpecHelper {

  def setRouteDetails(routeDetails: RouteDetails, addSecurityDetails: Option[Boolean])(startUserAnswers: UserAnswers): UserAnswers = {
    val interstitialUserAnswers =
      startUserAnswers
        .set(CountryOfDispatchPage, routeDetails.countryOfDispatch)
        .toOption
        .get
        .set(OfficeOfDeparturePage, routeDetails.officeOfDeparture)
        .toOption
        .get
        .set(DestinationCountryPage, routeDetails.destinationCountry)
        .toOption
        .get
        .set(DestinationOfficePage, routeDetails.destinationOffice)
        .toOption
        .get

    val userAnswers = routeDetails.transitInformation.zipWithIndex.foldLeft(interstitialUserAnswers) {
      case (ua, (TransitInformation(transitOffice, arrivalTime), index)) =>
        ua.unsafeSetVal(AddAnotherTransitOfficePage(Index(index)))(transitOffice)
          .unsafeSetVal(AddSecurityDetailsPage)(addSecurityDetails.getOrElse(arrivalTime.isDefined))
          .unsafeSetOpt(ArrivalTimesAtOfficePage(Index(index)))(arrivalTime.map(LocalDateTimeWithAMPM(_, "PM")))

    }

    userAnswers
  }
}
