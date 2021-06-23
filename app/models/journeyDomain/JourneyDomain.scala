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

import cats.data._
import cats.implicits._
import models.journeyDomain.traderDetails.TraderDetails
import models.reference.CountryCode
import pages.{AddSecurityDetailsPage, TotalGrossMassPage}

case class JourneyDomain(
  preTaskList: PreTaskListDetails,
  movementDetails: MovementDetails,
  routeDetails: RouteDetails,
  transportDetails: TransportDetails,
  traderDetails: TraderDetails,
  itemDetails: NonEmptyList[ItemSection],
  goodsSummary: GoodsSummary,
  guarantee: NonEmptyList[GuaranteeDetails],
  safetyAndSecurity: Option[SafetyAndSecurity],
  grossMass: Option[String] //TODO remove once deployed and journeys completed by current users
)

object JourneyDomain {

  object Constants {

    val principalTraderCountryCode: CountryCode = CountryCode("GB")

  }

  implicit def userAnswersReader: UserAnswersReader[JourneyDomain] = {

    val safetyAndSecurityReader: UserAnswersReader[Option[SafetyAndSecurity]] = AddSecurityDetailsPage.reader
      .flatMap {
        case true  => UserAnswersReader[SafetyAndSecurity].map(_.some)
        case false => none[SafetyAndSecurity].pure[UserAnswersReader]
      }

    for {
      preTaskList       <- UserAnswersReader[PreTaskListDetails]
      movementDetails   <- UserAnswersReader[MovementDetails]
      routeDetails      <- UserAnswersReader[RouteDetails]
      transportDetails  <- UserAnswersReader[TransportDetails]
      traderDetails     <- UserAnswersReader[TraderDetails]
      itemDetails       <- UserAnswersReader[NonEmptyList[ItemSection]]
      goodsSummary      <- UserAnswersReader[GoodsSummary]
      guarantee         <- UserAnswersReader[NonEmptyList[GuaranteeDetails]]
      safetyAndSecurity <- safetyAndSecurityReader
      grossMass         <- TotalGrossMassPage.optionalReader //TODO remove once deployed and journeys completed by current users
    } yield JourneyDomain(
      preTaskList,
      movementDetails,
      routeDetails,
      transportDetails,
      traderDetails,
      itemDetails,
      goodsSummary,
      guarantee,
      safetyAndSecurity,
      grossMass //TODO remove once deployed and journeys completed by current users
    )
  }
}
