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

package models

import cats.implicits._
import models.journeyDomain.traderDetails.TraderDetails
import models.journeyDomain.{MovementDetails, RouteDetails, SafetyAndSecurity, TransportDetails, UserAnswersReader}
import pages.AddSecurityDetailsPage

sealed trait DependentSection

object DependentSection {

  case object TransportDetails extends DependentSection
  case object GuaranteeDetails extends DependentSection
  case object SafetyAndSecurity extends DependentSection
  case object GoodsSummary extends DependentSection
  case object ItemDetails extends DependentSection

  private def itemsDependentSections(userAnswers: UserAnswers): UserAnswersReader[_] = {
    val commonSection = for {
      _            <- UserAnswersReader[MovementDetails]
      _            <- UserAnswersReader[TraderDetails]
      routeDetails <- UserAnswersReader[RouteDetails]
    } yield routeDetails

    if (userAnswers.get(AddSecurityDetailsPage).contains(true)) {
      for {
        _     <- commonSection
        sAndS <- UserAnswersReader[SafetyAndSecurity]
      } yield sAndS
    } else {
      commonSection
    }
  }

  def dependentSectionReader(section: DependentSection, userAnswers: UserAnswers): UserAnswersReader[_] =
    section match {
      case TransportDetails  => UserAnswersReader[MovementDetails]
      case GoodsSummary      => UserAnswersReader[MovementDetails]
      case SafetyAndSecurity => UserAnswersReader[TransportDetails]
      case GuaranteeDetails  => UserAnswersReader[RouteDetails]
      case ItemDetails       => itemsDependentSections(userAnswers)
    }
}
