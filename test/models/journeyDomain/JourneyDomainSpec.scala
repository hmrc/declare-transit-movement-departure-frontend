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

import base.{GeneratorSpec, SpecBase}
import controllers.traderDetails.ConsignorAddressControllerSpec
import generators.JourneyModelGenerators
import models.{ConsigneeAddress, ConsignorAddress, PrincipalAddress, UserAnswers}
import models.domain.Address
import models.journeyDomain.RouteDetails.TransitInformation
import org.scalacheck.Arbitrary

class JourneyDomainSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {

  "JourneyDomain" - {
    "can be parsed UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(arb[JourneyDomain], arb[UserAnswers]) {
          case (journeyDomain, userAnswers) =>
            val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(journeyDomain)(userAnswers)

            val result = UserAnswersReader[JourneyDomain].run(updatedUserAnswer)

            result.value mustEqual journeyDomain
        }
      }
    }

    "cannot be parsed" - {
      "when some answers is missing" in {
        forAll(arb[ItemSection], arb[UserAnswers]) {
          case (itemSection, ua) =>
            val userAnswers                 = ItemDetailsSpec.setItemDetailsUserAnswers(itemSection.itemDetails, index)(ua)
            val result: Option[ItemSection] = ItemSection.readerItemSection(index).run(userAnswers)

            result mustBe None
        }
      }
    }
  }
}

object JourneyDomainSpec {

  def setJourneyDomain(journeyDomain: JourneyDomain)(startUserAnswers: UserAnswers): UserAnswers =
    (
      PreTaskListDetailsSpec.setPreTaskListDetails(journeyDomain.preTaskList) _ andThen
        MovementDetailsSpec.setMovementDetails(journeyDomain.movementDetails) andThen
        RouteDetailsSpec.setRouteDetails(journeyDomain.routeDetails, Some(journeyDomain.preTaskList.addSecurityDetails)) andThen
        TransportDetailsSpec.setTransportDetail(journeyDomain.transportDetails) andThen
        TraderDetailsSpec.setTraderDetails(journeyDomain.traderDetails) andThen
        ItemSectionSpec.setItemSections(journeyDomain.itemDetails.toList) andThen
        GoodsSummarySpec.setGoodsSummary(journeyDomain.goodsSummary, Some(journeyDomain.preTaskList.addSecurityDetails)) andThen
        GuaranteeDetailsSpec.setGuaranteeDetails(journeyDomain.guarantee)
    )(startUserAnswers)

}
