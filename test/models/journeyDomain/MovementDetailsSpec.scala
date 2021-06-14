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
import models.journeyDomain.MovementDetails.{DeclarationForSelf, DeclarationForSomeoneElse, NormalMovementDetails, SimplifiedMovementDetails}
import models.journeyDomain.PackagesSpec.UserAnswersSpecHelperOps
import models.{ProcedureType, UserAnswers}
import org.scalacheck.{Arbitrary, Gen}
import pages._
import pages.movementDetails.PreLodgeDeclarationPage

class MovementDetailsSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {

  private val normalMandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
    ProcedureTypePage,
    DeclarationTypePage,
    PreLodgeDeclarationPage,
    ContainersUsedPage,
    DeclarationPlacePage,
    DeclarationForSomeoneElsePage
  )

  private val simplifiedMandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
    ProcedureTypePage,
    DeclarationTypePage,
    ContainersUsedPage,
    DeclarationPlacePage,
    DeclarationForSomeoneElsePage
  )

  "MovementDetails" - {

    "can be parsed UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(movementUserAnswers) {
          case (movementDetails, userAnswers) =>
            val result = UserAnswersReader[MovementDetails].run(userAnswers).right.value

            result mustBe movementDetails
        }
      }
    }

    "cannot parse UserAnswers" - {
      "when a mandatory page for NormalMovementDetails" in {
        forAll(normalMovementUserAnswers, normalMandatoryPages) {
          case ((_, ua), mandatoryPage) =>
            val userAnswers = ua.remove(mandatoryPage).success.value

            val result = UserAnswersReader[MovementDetails].run(userAnswers).left.value

            result mustBe ReaderError(mandatoryPage)
        }
      }

      "when a mandatory page for SimplifiedMovementDetails" in {
        forAll(simpleMovementUserAnswers, simplifiedMandatoryPages) {
          case ((_, ua), mandatoryPage) =>
            val userAnswers = ua.remove(mandatoryPage).success.value

            val result = UserAnswersReader[MovementDetails].run(userAnswers).left.value

            result mustBe ReaderError(mandatoryPage)
        }
      }
    }
  }

  "NormalMovementDetails" - {

    "can be parsed" - {
      "when all details for section have been answered" in {
        forAll(normalMovementUserAnswers) {
          case (expected, userAnswers) =>
            val result = UserAnswersReader[NormalMovementDetails].run(userAnswers).right.value

            result mustEqual expected
        }
      }

    }

    "cannot be parsed from UserAnswers" - {
      "when an answer is missing" in {
        forAll(normalMovementUserAnswers, normalMandatoryPages) {
          case ((_, ua), mandatoryPage) =>
            val userAnswers = ua.remove(mandatoryPage).success.value

            val result = UserAnswersReader[NormalMovementDetails].run(userAnswers).isLeft

            result mustEqual true
        }
      }

      "when the movement is a simplified" in {
        forAll(simpleMovementUserAnswers) {
          case (_, userAnswers) =>
            val result = UserAnswersReader[NormalMovementDetails].run(userAnswers).isLeft

            result mustEqual true
        }
      }
    }
  }

  "SimplifiedMovementDetails" - {

    "can be parsed from UserAnswers" - {
      "when all the answers have been answered" in {
        forAll(simpleMovementUserAnswers) {
          case (expected, userAnswers) =>
            val result = UserAnswersReader[SimplifiedMovementDetails].run(userAnswers).right.value

            result mustEqual expected
        }
      }
    }

    "cannot be parsed from UserAnswers" - {
      "when a mandatory answer is missing" in {
        forAll(simpleMovementUserAnswers, simplifiedMandatoryPages) {
          case ((_, ua), mandatoryPage) =>
            val userAnswers = ua.remove(mandatoryPage).success.value

            val result = UserAnswersReader[SimplifiedMovementDetails].run(userAnswers).isLeft

            result mustEqual true
        }
      }

      "when the movement is a simplified movement" in {
        forAll(normalMovementUserAnswers) {
          case (_, userAnswers) =>
            val result = UserAnswersReader[SimplifiedMovementDetails].run(userAnswers).isLeft

            result mustEqual true
        }
      }
    }
  }

  val simpleMovementUserAnswers: Gen[(SimplifiedMovementDetails, UserAnswers)] =
    for {
      movementDetails <- Arbitrary.arbitrary[SimplifiedMovementDetails]
      baseUserAnswers <- Arbitrary.arbitrary[UserAnswers]
    } yield {
      val userAnswers = MovementDetailsSpec.setSimplifiedMovement(movementDetails)(baseUserAnswers)

      (movementDetails, userAnswers)
    }

  private val normalMovementUserAnswers: Gen[(NormalMovementDetails, UserAnswers)] =
    for {
      movementDetails <- Arbitrary.arbitrary[NormalMovementDetails]
      baseUserAnswers <- Arbitrary.arbitrary[UserAnswers]
    } yield {
      val userAnswers = MovementDetailsSpec.setNormalMovement(movementDetails)(baseUserAnswers)

      (movementDetails, userAnswers)
    }

  private val movementUserAnswers: Gen[(MovementDetails, UserAnswers)] =
    Gen.oneOf(simpleMovementUserAnswers, normalMovementUserAnswers)

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
