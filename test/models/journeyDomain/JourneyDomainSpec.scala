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
import models.journeyDomain.traderDetails.TraderDetailsSpec
import models.{Scenario1, Scenario2, Scenario3, Scenario4, UserAnswerScenario, UserAnswers}
import pages.AddSecurityDetailsPage

class JourneyDomainSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators with UserAnswersSpecHelper {

  "JourneyDomain" - {
    "can be parsed UserAnswers" - {
      "when all details for section have been answered" in {

        forAll(arb[UserAnswerScenario]) {
          userAnswerScenario =>
            val result = UserAnswersReader[JourneyDomain].run(userAnswerScenario.userAnswers).isRight

            result mustBe true
        }
      }

      "Scenario 1" in {

        val result = UserAnswersReader[JourneyDomain].run(Scenario1.userAnswers)

        result.right.value mustBe Scenario1.toModel
      }

      "Scenario 2" in {

        val result = UserAnswersReader[JourneyDomain].run(Scenario2.userAnswers)

        result.right.value mustBe Scenario2.toModel
      }

      "Scenario 3" in {

        val result = UserAnswersReader[JourneyDomain].run(Scenario3.userAnswers)

        result.right.value mustBe Scenario3.toModel
      }

      "Scenario 4" in {

        val result = UserAnswersReader[JourneyDomain].run(Scenario4.userAnswers)

//        result.right.value mustBe Scenario4.toModel

        result.right.value.routeDetails mustEqual Scenario4.toModel.routeDetails
        result.right.value.movementDetails mustEqual Scenario4.toModel.movementDetails
        result.right.value.transportDetails mustEqual Scenario4.toModel.transportDetails
        result.right.value.traderDetails mustEqual Scenario4.toModel.traderDetails
        result.right.value.itemDetails mustEqual Scenario4.toModel.itemDetails
        result.right.value.goodsSummary mustEqual Scenario4.toModel.goodsSummary
        result.right.value.guarantee mustEqual Scenario4.toModel.guarantee
        result.right.value.grossMass mustEqual Scenario4.toModel.grossMass
        result.right.value.safetyAndSecurity mustEqual Scenario4.toModel.safetyAndSecurity
        result.right.value.preTaskList mustEqual Scenario4.toModel.preTaskList
      }

      "cannot be parsed from UserAnswers" - {

        "when a safety and security is missing" in {

          forAll(arb[UserAnswerScenario]) {
            userAnswerScenario =>
              val userAnswers = userAnswerScenario.userAnswers
                .unsafeRemove(AddSecurityDetailsPage)

              val result = UserAnswersReader[JourneyDomain].run(userAnswers).left.value

              result.page mustBe AddSecurityDetailsPage
          }
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
