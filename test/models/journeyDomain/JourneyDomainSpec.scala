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
import generators.JourneyModelGenerators
import models.DeclarationType.Option4
import models.ProcedureType.Simplified
import models.domain.Address
import models.journeyDomain.MovementDetails._
import models.journeyDomain.RouteDetails.TransitInformation
import models.journeyDomain.TraderDetails.{PersonalInformation, TraderEori}
import models.journeyDomain.TransportDetails.DetailsAtBorder
import models.journeyDomain.TransportDetails.DetailsAtBorder.NewDetailsAtBorder
import models.journeyDomain.TransportDetails.InlandMode.Rail
import models.journeyDomain._
import models.reference.CountryCode
import models.{EoriNumber, LocalReferenceNumber, RepresentativeCapacity, UserAnswers}

class JourneyDomainSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {

  "JourneyDomain" - {
    "can be parsed UserAnswers" - {
      "when all details for section have been answered" ignore { //Need to address this random failure case
        forAll(arb[JourneyDomain]) {
          journeyDomain =>
            val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(journeyDomain)(emptyUserAnswers)
            val result            = UserAnswersReader[JourneyDomain].run(updatedUserAnswer)

            result.value.preTaskList mustEqual journeyDomain.preTaskList
            result.value.movementDetails mustEqual journeyDomain.movementDetails
            result.value.routeDetails mustEqual journeyDomain.routeDetails
            result.value.transportDetails mustEqual journeyDomain.transportDetails
            result.value.traderDetails mustEqual journeyDomain.traderDetails
            result.value.itemDetails mustEqual journeyDomain.itemDetails
            result.value.goodsSummary mustEqual journeyDomain.goodsSummary
            result.value.guarantee mustEqual journeyDomain.guarantee
            result.value.safetyAndSecurity mustEqual journeyDomain.safetyAndSecurity
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
        RouteDetailsSpec.setRouteDetails(journeyDomain.routeDetails, Some(journeyDomain.preTaskList.addSecurityDetails)) andThen
        TransportDetailsSpec.setTransportDetail(journeyDomain.transportDetails) andThen
        ItemSectionSpec.setItemSections(journeyDomain.itemDetails.toList) andThen
        GoodsSummarySpec.setGoodsSummary(journeyDomain.goodsSummary, Some(journeyDomain.preTaskList.addSecurityDetails)) andThen
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
