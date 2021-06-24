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

import cats.implicits._
import models.ProcedureType.{Normal, Simplified}
import models.journeyDomain.MovementDetails.DeclarationForSomeoneElseAnswer
import models.{DeclarationType, RepresentativeCapacity}
import pages._
import pages.movementDetails.PreLodgeDeclarationPage

sealed trait MovementDetails {
  val containersUsed: Boolean
  val declarationPlacePage: String
  val declarationForSomeoneElse: DeclarationForSomeoneElseAnswer
}

object MovementDetails {

  implicit val parserMovementDetails: UserAnswersReader[MovementDetails] =
    ProcedureTypePage.reader.flatMap {
      case Normal     => UserAnswersReader[NormalMovementDetails].widen[MovementDetails]
      case Simplified => UserAnswersReader[SimplifiedMovementDetails].widen[MovementDetails]
    }

  private val declarationForSomeoneElseAnswer: UserAnswersReader[DeclarationForSomeoneElseAnswer] =
    DeclarationForSomeoneElsePage.reader.flatMap(
      bool =>
        if (bool) {
          UserAnswersReader[DeclarationForSomeoneElse].widen[DeclarationForSomeoneElseAnswer]
        } else {
          UserAnswersReader[DeclarationForSelf.type].widen[DeclarationForSomeoneElseAnswer]
        }
    )

  final case class NormalMovementDetails(
    prelodge: Boolean,
    containersUsed: Boolean,
    declarationPlacePage: String,
    declarationForSomeoneElse: DeclarationForSomeoneElseAnswer
  ) extends MovementDetails

  object NormalMovementDetails {

    implicit val parseNormalMovementDetails: UserAnswersReader[NormalMovementDetails] =
      (
        PreLodgeDeclarationPage.reader,
        ContainersUsedPage.reader,
        DeclarationPlacePage.reader,
        declarationForSomeoneElseAnswer
      ).tupled.map((NormalMovementDetails.apply _).tupled)
  }

  final case class SimplifiedMovementDetails(
    containersUsed: Boolean,
    declarationPlacePage: String,
    declarationForSomeoneElse: DeclarationForSomeoneElseAnswer
  ) extends MovementDetails

  object SimplifiedMovementDetails {

    implicit val parseSimplifiedMovementDetails: UserAnswersReader[SimplifiedMovementDetails] =
      (
        ContainersUsedPage.reader,
        DeclarationPlacePage.reader,
        declarationForSomeoneElseAnswer
      ).tupled.map((SimplifiedMovementDetails.apply _).tupled)
  }

  sealed trait DeclarationForSomeoneElseAnswer

  object DeclarationForSelf extends DeclarationForSomeoneElseAnswer {

    implicit val readDeclarationForSelf: UserAnswersReader[DeclarationForSelf.type] =
      DeclarationForSelf.pure[UserAnswersReader]

  }

  final case class DeclarationForSomeoneElse(
    companyName: String,
    capacity: RepresentativeCapacity
  ) extends DeclarationForSomeoneElseAnswer

  object DeclarationForSomeoneElse {

    implicit val readDeclarationForSomeoneElse: UserAnswersReader[DeclarationForSomeoneElse] =
      (
        RepresentativeNamePage.reader,
        RepresentativeCapacityPage.reader
      ).tupled.map((DeclarationForSomeoneElse.apply _).tupled)
  }

}
