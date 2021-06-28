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
import cats.data.NonEmptyList
import commonTestUtils.UserAnswersSpecHelper
import generators.UserAnswersGenerator
import models.Index
import models.userAnswerScenarios.Scenario1
import pages.{AddSecurityDetailsPage, ItemTotalGrossMassPage}

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

      "ItemSections" - {
        "Must submit the correct amount for total gross mass" in {

          val updatedUserAnswer = Scenario1.userAnswers
            .unsafeSetVal(ItemTotalGrossMassPage(Index(0)))(100.123)
            .unsafeSetVal(ItemTotalGrossMassPage(Index(1)))(200.123)

          val itemSectionList = UserAnswersReader[NonEmptyList[ItemSection]].run(updatedUserAnswer).right.value

          val result = ItemSections(itemSectionList)

          result.totalGrossMassFormatted mustBe "300.246"
        }
      }
    }
  }
}
