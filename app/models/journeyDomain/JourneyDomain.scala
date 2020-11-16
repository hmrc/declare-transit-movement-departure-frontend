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

import cats.data._
import cats.implicits._
import models.UserAnswers
import models.reference.CountryCode

case class JourneyDomain(
  preTaskList: PreTaskListDetails,
  movementDetails: MovementDetails,
  routeDetails: RouteDetails,
  transportDetails: TransportDetails,
  traderDetails: TraderDetails,
  itemDetails: NonEmptyList[ItemSection],
  goodsSummary: GoodsSummary,
  guarantee: GuaranteeDetails
)

object JourneyDomain {

  object Constants {

    val principalTraderCountryCode: CountryCode = CountryCode("GB")

  }

  implicit def userAnswersReader: UserAnswersReader[JourneyDomain] = {
    // TOOD: This is a workaround till we remove UserAnswersParser
    implicit def fromUserAnswersParser[A](implicit parser: UserAnswersParser[Option, A]): UserAnswersReader[A] =
      ReaderT[Option, UserAnswers, A](parser.run _)

    for {
      preTaskList      <- UserAnswersReader[PreTaskListDetails]
      movementDetails  <- UserAnswersReader[MovementDetails]
      routeDetails     <- UserAnswersReader[RouteDetails]
      transportDetails <- UserAnswersReader[TransportDetails]
      traderDetails    <- UserAnswersReader[TraderDetails]
      itemDetails      <- UserAnswersReader[NonEmptyList[ItemSection]]
      goodsSummary     <- UserAnswersReader[GoodsSummary]
      guarantee        <- UserAnswersReader[GuaranteeDetails]
    } yield
      JourneyDomain(
        preTaskList,
        movementDetails,
        routeDetails,
        transportDetails,
        traderDetails,
        itemDetails,
        goodsSummary,
        guarantee
      )
  }
}
