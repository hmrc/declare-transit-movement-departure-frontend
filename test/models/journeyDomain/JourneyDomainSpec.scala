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
import generators.JourneyModelGenerators
import models.UserAnswers
import models.journeyDomain.traderDetails.TraderDetailsSpec

class JourneyDomainSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {

  "JourneyDomain" - {
    "can be parsed UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(arb[JourneyDomain]) {
          journeyDomain =>
            val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(journeyDomain)(emptyUserAnswers)

            val result = UserAnswersReader[JourneyDomain].run(updatedUserAnswer)

            result.right.value.preTaskList mustEqual journeyDomain.preTaskList
            result.right.value.movementDetails mustEqual journeyDomain.movementDetails
            result.right.value.routeDetails mustEqual journeyDomain.routeDetails
            result.right.value.transportDetails mustEqual journeyDomain.transportDetails
            result.right.value.traderDetails mustEqual journeyDomain.traderDetails
            result.right.value.goodsSummary mustEqual journeyDomain.goodsSummary
            result.right.value.guarantee mustEqual journeyDomain.guarantee
            result.right.value.safetyAndSecurity mustEqual journeyDomain.safetyAndSecurity
        }
      }
    }

    "cannot be parsed" - {
      "when some answers is missing" in {
        forAll(arb[ItemSection], arb[UserAnswers]) {
          case (itemSection, ua) =>
            val userAnswers                     = ItemDetailsSpec.setItemDetailsUserAnswers(itemSection.itemDetails, index)(ua)
            val result: EitherType[ItemSection] = ItemSection.readerItemSection(index).run(userAnswers)

            result.left.value mustBe None
        }
      }
    }
  }
}

object JourneyDomainSpec {

  def setJourneyDomain(journeyDomain: JourneyDomain)(startUserAnswers: UserAnswers): UserAnswers =
    (
      PreTaskListDetailsSpec.setPreTaskListDetails(journeyDomain.preTaskList) _ andThen
        RouteDetailsSpec.setRouteDetails(journeyDomain.routeDetails) andThen
        TransportDetailsSpec.setTransportDetail(journeyDomain.transportDetails) andThen
        ItemSectionSpec.setItemSections(journeyDomain.itemDetails.toList) andThen
        GoodsSummarySpec.setGoodsSummary(journeyDomain.goodsSummary) andThen
        GuaranteeDetailsSpec.setGuaranteeDetails(journeyDomain.guarantee) andThen
        TraderDetailsSpec.setTraderDetails(journeyDomain.traderDetails) andThen
        MovementDetailsSpec.setMovementDetails(journeyDomain.movementDetails) andThen
        safetyAndSecurity(journeyDomain.safetyAndSecurity)
    )(startUserAnswers)

  def safetyAndSecurity(safetyAndSecurity: Option[SafetyAndSecurity])(startUserAnswers: UserAnswers): UserAnswers =
    safetyAndSecurity match {
      case Some(value) => SafetyAndSecuritySpec.setSafetyAndSecurity(value)(startUserAnswers)
      case None        => startUserAnswers
    }

}
