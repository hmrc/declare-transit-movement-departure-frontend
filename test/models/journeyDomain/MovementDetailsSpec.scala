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
import models.DeclarationType.Option1
import models.ProcedureType.{Normal, Simplified}
import models.RepresentativeCapacity.Direct
import models.journeyDomain.MovementDetails.{DeclarationForSelf, DeclarationForSomeoneElse, NormalMovementDetails, SimplifiedMovementDetails}
import models.journeyDomain.PackagesSpec.UserAnswersSpecHelperOps
import models.{ProcedureType, UserAnswers}
import org.scalacheck.Gen
import pages._
import pages.movementDetails.PreLodgeDeclarationPage

class MovementDetailsSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {

  "MovementDetails" - {

    "can be parsed from UserAnswers" - {

      "when procedure type is Normal" in {

        val expectedResult = NormalMovementDetails(
          Option1,
          prelodge       = false,
          containersUsed = false,
          "declarationPlace",
          DeclarationForSomeoneElse("repName", Direct)
        )

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(ProcedureTypePage)(Normal)
          .unsafeSetVal(DeclarationTypePage)(Option1)
          .unsafeSetVal(PreLodgeDeclarationPage)(false)
          .unsafeSetVal(ContainersUsedPage)(false)
          .unsafeSetVal(DeclarationPlacePage)("declarationPlace")
          .unsafeSetVal(DeclarationForSomeoneElsePage)(true)
          .unsafeSetVal(RepresentativeNamePage)("repName")
          .unsafeSetVal(RepresentativeCapacityPage)(Direct)

        val result = UserAnswersReader[MovementDetails].run(userAnswers).right.value

        result mustBe expectedResult
      }

      "when procedure type is Simplified" in {

        val expectedResult = SimplifiedMovementDetails(
          Option1,
          containersUsed = false,
          "declarationPlace",
          DeclarationForSomeoneElse("repName", Direct)
        )

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(ProcedureTypePage)(Simplified)
          .unsafeSetVal(DeclarationTypePage)(Option1)
          .unsafeSetVal(ContainersUsedPage)(false)
          .unsafeSetVal(DeclarationPlacePage)("declarationPlace")
          .unsafeSetVal(DeclarationForSomeoneElsePage)(true)
          .unsafeSetVal(RepresentativeNamePage)("repName")
          .unsafeSetVal(RepresentativeCapacityPage)(Direct)

        val result = UserAnswersReader[MovementDetails].run(userAnswers).right.value

        result mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when procedure type is not defined" in {

        val result = UserAnswersReader[MovementDetails].run(emptyUserAnswers).left.value

        result.page mustBe ProcedureTypePage
      }
    }

    "NormalMovementDetails" - {
      "can be parsed UserAnswers" - {

        "when its a declaration for someone else" - {

          "and all mandatory answers are defined" in {

            val expectedResult = NormalMovementDetails(
              Option1,
              prelodge       = false,
              containersUsed = false,
              "declarationPlace",
              DeclarationForSomeoneElse("repName", Direct)
            )

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(DeclarationTypePage)(Option1)
              .unsafeSetVal(PreLodgeDeclarationPage)(false)
              .unsafeSetVal(ContainersUsedPage)(false)
              .unsafeSetVal(DeclarationPlacePage)("declarationPlace")
              .unsafeSetVal(DeclarationForSomeoneElsePage)(true)
              .unsafeSetVal(RepresentativeNamePage)("repName")
              .unsafeSetVal(RepresentativeCapacityPage)(Direct)

            val result = UserAnswersReader[NormalMovementDetails].run(userAnswers).right.value

            result mustBe expectedResult
          }
        }

        "when its a declaration for self" - {

          "and all mandatory answers are defined" in {

            val expectedResult = NormalMovementDetails(
              Option1,
              prelodge       = false,
              containersUsed = false,
              "declarationPlace",
              DeclarationForSelf
            )

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(DeclarationTypePage)(Option1)
              .unsafeSetVal(PreLodgeDeclarationPage)(false)
              .unsafeSetVal(ContainersUsedPage)(false)
              .unsafeSetVal(DeclarationPlacePage)("declarationPlace")
              .unsafeSetVal(DeclarationForSomeoneElsePage)(false)

            val result = UserAnswersReader[NormalMovementDetails].run(userAnswers).right.value

            result mustBe expectedResult
          }
        }
      }

      "cannot be parse UserAnswers" - {

        "when its a declaration for someone else" - {

          "and a mandatory page is missing" in {

            val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
              DeclarationTypePage,
              PreLodgeDeclarationPage,
              ContainersUsedPage,
              DeclarationPlacePage,
              DeclarationForSomeoneElsePage,
              RepresentativeNamePage,
              RepresentativeCapacityPage
            )

            forAll(mandatoryPages) {
              mandatoryPage =>
                val userAnswers = emptyUserAnswers
                  .unsafeSetVal(DeclarationTypePage)(Option1)
                  .unsafeSetVal(PreLodgeDeclarationPage)(false)
                  .unsafeSetVal(ContainersUsedPage)(false)
                  .unsafeSetVal(DeclarationPlacePage)("declarationPlace")
                  .unsafeSetVal(DeclarationForSomeoneElsePage)(true)
                  .unsafeSetVal(RepresentativeNamePage)("repName")
                  .unsafeSetVal(RepresentativeCapacityPage)(Direct)
                  .unsafeRemove(mandatoryPage)

                val result = UserAnswersReader[NormalMovementDetails].run(userAnswers).left.value

                result.page mustBe mandatoryPage
            }
          }
        }

        "when its a declaration for self" - {

          "and a mandatory page is missing" in {

            val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
              DeclarationTypePage,
              PreLodgeDeclarationPage,
              ContainersUsedPage,
              DeclarationPlacePage,
              DeclarationForSomeoneElsePage
            )

            forAll(mandatoryPages) {
              mandatoryPage =>
                val userAnswers = emptyUserAnswers
                  .unsafeSetVal(DeclarationTypePage)(Option1)
                  .unsafeSetVal(PreLodgeDeclarationPage)(false)
                  .unsafeSetVal(ContainersUsedPage)(false)
                  .unsafeSetVal(DeclarationPlacePage)("declarationPlace")
                  .unsafeSetVal(DeclarationForSomeoneElsePage)(false)
                  .unsafeRemove(mandatoryPage)

                val result = UserAnswersReader[NormalMovementDetails].run(userAnswers).left.value

                result.page mustBe mandatoryPage
            }
          }
        }
      }
    }

    "SimplifiedMovementDetails" - {

      "can be parsed UserAnswers" - {

        "when its a declaration for someone else" - {

          "and all mandatory answers are defined" in {

            val expectedResult = SimplifiedMovementDetails(
              Option1,
              containersUsed = false,
              "declarationPlace",
              DeclarationForSomeoneElse("repName", Direct)
            )

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(DeclarationTypePage)(Option1)
              .unsafeSetVal(ContainersUsedPage)(false)
              .unsafeSetVal(DeclarationPlacePage)("declarationPlace")
              .unsafeSetVal(DeclarationForSomeoneElsePage)(true)
              .unsafeSetVal(RepresentativeNamePage)("repName")
              .unsafeSetVal(RepresentativeCapacityPage)(Direct)

            val result = UserAnswersReader[SimplifiedMovementDetails].run(userAnswers).right.value

            result mustBe expectedResult
          }
        }

        "when its a declaration for self" - {

          "and all mandatory answers are defined" in {

            val expectedResult = SimplifiedMovementDetails(
              Option1,
              containersUsed = false,
              "declarationPlace",
              DeclarationForSelf
            )

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(DeclarationTypePage)(Option1)
              .unsafeSetVal(ContainersUsedPage)(false)
              .unsafeSetVal(DeclarationPlacePage)("declarationPlace")
              .unsafeSetVal(DeclarationForSomeoneElsePage)(false)

            val result = UserAnswersReader[SimplifiedMovementDetails].run(userAnswers).right.value

            result mustBe expectedResult
          }
        }
      }

      "cannot be parse UserAnswers" - {

        "when its a declaration for someone else" - {

          "and a mandatory page is missing" in {

            val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
              DeclarationTypePage,
              ContainersUsedPage,
              DeclarationPlacePage,
              DeclarationForSomeoneElsePage,
              RepresentativeNamePage,
              RepresentativeCapacityPage
            )

            forAll(mandatoryPages) {
              mandatoryPage =>
                val userAnswers = emptyUserAnswers
                  .unsafeSetVal(DeclarationTypePage)(Option1)
                  .unsafeSetVal(ContainersUsedPage)(false)
                  .unsafeSetVal(DeclarationPlacePage)("declarationPlace")
                  .unsafeSetVal(DeclarationForSomeoneElsePage)(true)
                  .unsafeSetVal(RepresentativeNamePage)("repName")
                  .unsafeSetVal(RepresentativeCapacityPage)(Direct)
                  .unsafeRemove(mandatoryPage)

                val result = UserAnswersReader[SimplifiedMovementDetails].run(userAnswers).left.value

                result.page mustBe mandatoryPage
            }
          }
        }

        "when its a declaration for self" - {

          "and a mandatory page is missing" in {

            val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
              DeclarationTypePage,
              ContainersUsedPage,
              DeclarationPlacePage,
              DeclarationForSomeoneElsePage
            )

            forAll(mandatoryPages) {
              mandatoryPage =>
                val userAnswers = emptyUserAnswers
                  .unsafeSetVal(DeclarationTypePage)(Option1)
                  .unsafeSetVal(ContainersUsedPage)(false)
                  .unsafeSetVal(DeclarationPlacePage)("declarationPlace")
                  .unsafeSetVal(DeclarationForSomeoneElsePage)(false)
                  .unsafeRemove(mandatoryPage)

                val result = UserAnswersReader[SimplifiedMovementDetails].run(userAnswers).left.value

                result.page mustBe mandatoryPage
            }
          }
        }
      }
    }
  }
}

object MovementDetailsSpec {

  def setMovementDetails(movementDetails: MovementDetails)(startUserAnswers: UserAnswers): UserAnswers =
    movementDetails match {
      case details: NormalMovementDetails     => setNormalMovement(details)(startUserAnswers)
      case details: SimplifiedMovementDetails => setSimplifiedMovement(details)(startUserAnswers)
    }

  def setNormalMovement(movementDetails: NormalMovementDetails)(startUserAnswers: UserAnswers): UserAnswers = {
    val interstitialUserAnswers =
      startUserAnswers
        .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
        .unsafeSetVal(DeclarationTypePage)(movementDetails.declarationType)
        .unsafeSetVal(PreLodgeDeclarationPage)(movementDetails.prelodge)
        .unsafeSetVal(ContainersUsedPage)(movementDetails.containersUsed)
        .unsafeSetVal(DeclarationPlacePage)(movementDetails.declarationPlacePage)
        .unsafeSetVal(DeclarationForSomeoneElsePage)(movementDetails.declarationForSomeoneElse != DeclarationForSelf)

    val userAnswers = movementDetails.declarationForSomeoneElse match {
      case DeclarationForSelf =>
        interstitialUserAnswers
      case DeclarationForSomeoneElse(companyName, capacity) =>
        interstitialUserAnswers
          .unsafeSetVal(RepresentativeNamePage)(companyName)
          .unsafeSetVal(RepresentativeCapacityPage)(capacity)
    }

    userAnswers
  }

  def setSimplifiedMovement(movementDetails: SimplifiedMovementDetails)(startUserAnswers: UserAnswers): UserAnswers = {
    val interstitialUserAnswers =
      startUserAnswers
        .unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
        .unsafeSetVal(DeclarationTypePage)(movementDetails.declarationType)
        .unsafeSetVal(ContainersUsedPage)(movementDetails.containersUsed)
        .unsafeSetVal(DeclarationPlacePage)(movementDetails.declarationPlacePage)
        .unsafeSetVal(DeclarationForSomeoneElsePage)(movementDetails.declarationForSomeoneElse != DeclarationForSelf)

    val userAnswers = movementDetails.declarationForSomeoneElse match {
      case DeclarationForSelf =>
        interstitialUserAnswers
      case DeclarationForSomeoneElse(companyName, capacity) =>
        interstitialUserAnswers
          .unsafeSetVal(RepresentativeNamePage)(companyName)
          .unsafeSetVal(RepresentativeCapacityPage)(capacity)
    }

    userAnswers
  }

}
