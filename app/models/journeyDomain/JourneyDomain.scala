/*
 * Copyright 2023 HM Revenue & Customs
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
import pages.AddSecurityDetailsPage

case class ItemSections(itemDetails: NonEmptyList[ItemSection]) {

  val totalGrossMassDouble: Double = itemDetails.foldLeft(0.000) {
    (x, y) => y.itemDetails.itemTotalGrossMass.toDouble + x
  }

  val totalGrossMassFormatted = f"$totalGrossMassDouble%.3f"

  val totalPackages = itemDetails.foldLeft(0) {
    (total, itemSection) =>
      itemSection.packages.foldLeft(total) {
        (itemTotal, packageType) =>
          packageType match {
            case Packages.UnpackedPackages(packageType, totalPieces, markOrNumber)      => itemTotal + totalPieces
            case Packages.BulkPackages(packageType, markOrNumber)                       => itemTotal + 1
            case Packages.OtherPackages(packageType, howManyPackagesPage, markOrNumber) => itemTotal + howManyPackagesPage
          }
      }
  }
}

case class JourneyDomain(
  preTaskList: PreTaskListDetails,
  movementDetails: MovementDetails,
  routeDetails: RouteDetails,
  transportDetails: TransportDetails,
  traderDetails: TraderDetails,
  itemDetails: NonEmptyList[ItemSection],
  goodsSummary: GoodsSummary,
  guarantee: NonEmptyList[GuaranteeDetails],
  safetyAndSecurity: Option[SafetyAndSecurity]
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
    } yield JourneyDomain(
      preTaskList,
      movementDetails,
      routeDetails,
      transportDetails,
      traderDetails,
      itemDetails,
      goodsSummary,
      guarantee,
      safetyAndSecurity
    )
  }
}
