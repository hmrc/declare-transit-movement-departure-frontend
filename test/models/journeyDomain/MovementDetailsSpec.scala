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
import models.DeclarationType.Option1
import models.ProcedureType.{Normal, Simplified}
import models.RepresentativeCapacity.Direct
import models.journeyDomain.MovementDetails.{DeclarationForSelf, DeclarationForSomeoneElse, NormalMovementDetails, SimplifiedMovementDetails}
import org.scalacheck.Gen
import pages._
import pages.movementDetails.PreLodgeDeclarationPage

class MovementDetailsSpec extends SpecBase with GeneratorSpec with UserAnswersSpecHelper {

  "MovementDetails" - {

    "can be parsed from UserAnswers" - {

      "when procedure type is Normal" in {

        val expectedResult = NormalMovementDetails(
          false,
          false,
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
          false,
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
              false,
              false,
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
              false,
              false,
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
              PreLodgeDeclarationPage,
              ContainersUsedPage,
              DeclarationPlacePage,
              DeclarationForSomeoneElsePage
            )

            forAll(mandatoryPages) {
              mandatoryPage =>
                val userAnswers = emptyUserAnswers
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
              false,
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
              false,
              "declarationPlace",
              DeclarationForSelf
            )

            val userAnswers = emptyUserAnswers
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
              ContainersUsedPage,
              DeclarationPlacePage,
              DeclarationForSomeoneElsePage,
              RepresentativeNamePage,
              RepresentativeCapacityPage
            )

            forAll(mandatoryPages) {
              mandatoryPage =>
                val userAnswers = emptyUserAnswers
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
              ContainersUsedPage,
              DeclarationPlacePage,
              DeclarationForSomeoneElsePage
            )

            forAll(mandatoryPages) {
              mandatoryPage =>
                val userAnswers = emptyUserAnswers
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
