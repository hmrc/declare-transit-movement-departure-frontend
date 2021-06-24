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
import generators.UserAnswersGenerator
import models.journeyDomain.traderDetails.TraderDetailsSpec
import models.UserAnswers
import pages.AddSecurityDetailsPage

class JourneyDomainSpec extends SpecBase with GeneratorSpec with UserAnswersGenerator with UserAnswersSpecHelper {

  "JourneyDomain" - {
    "can be parsed UserAnswers" - {
      "when all details for section have been answered" in {

        forAll(genUserAnswerScenario) {
          userAnswerScenario =>
            val result = UserAnswersReader[JourneyDomain].run(userAnswerScenario.userAnswers).right.value

            result mustBe userAnswerScenario.toModel
        }
      }

      "cannot be parsed from UserAnswers" - {

        "when a safety and security is missing" in {

          forAll(genUserAnswerScenario) {
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
