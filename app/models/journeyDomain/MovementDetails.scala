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

import cats.data.ReaderT
import cats.implicits._
import models.ProcedureType.{Normal, Simplified}
import models.journeyDomain.MovementDetails.DeclarationForSomeoneElseAnswer
import models.{DeclarationType, RepresentativeCapacity, UserAnswers}
import pages._
import pages.movementDetails.PreLodgeDeclarationPage

sealed trait MovementDetails {
  val declarationType: DeclarationType
  val containersUsed: Boolean
  val declarationPlacePage: String
  val declarationForSomeoneElse: DeclarationForSomeoneElseAnswer
}

object MovementDetails {

  implicit val parserMovementDetails: UserAnswersReader[MovementDetails] =
    UserAnswersReader[NormalMovementDetails].widen[MovementDetails] orElse
      UserAnswersReader[SimplifiedMovementDetails].widen[MovementDetails]

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
    declarationType: DeclarationType,
    prelodge: Boolean,
    containersUsed: Boolean,
    declarationPlacePage: String,
    declarationForSomeoneElse: DeclarationForSomeoneElseAnswer
  ) extends MovementDetails

  object NormalMovementDetails {

    implicit val parseSimplifiedMovementDetails: UserAnswersReader[NormalMovementDetails] =
      ProcedureTypePage.reader.flatMap {
        case procedureType if procedureType == Normal =>
          (
            DeclarationTypePage.reader,
            PreLodgeDeclarationPage.reader,
            ContainersUsedPage.reader,
            DeclarationPlacePage.reader,
            declarationForSomeoneElseAnswer
          ).tupled.map((NormalMovementDetails.apply _).tupled)
        case _ =>
          ReaderT[EitherType, UserAnswers, NormalMovementDetails](
            _ => Left(ReaderError(ProcedureTypePage)) //TODO add message
          )
      }
  }

  final case class SimplifiedMovementDetails(
    declarationType: DeclarationType,
    containersUsed: Boolean,
    declarationPlacePage: String,
    declarationForSomeoneElse: DeclarationForSomeoneElseAnswer
  ) extends MovementDetails

  object SimplifiedMovementDetails {

    implicit val makeSimplifiedMovementDetails: UserAnswersReader[SimplifiedMovementDetails] =
      ProcedureTypePage.reader.flatMap {
        case procedureType if procedureType == Simplified =>
          (
            DeclarationTypePage.reader,
            ContainersUsedPage.reader,
            DeclarationPlacePage.reader,
            declarationForSomeoneElseAnswer
          ).tupled.map((SimplifiedMovementDetails.apply _).tupled)
        case _ =>
          ReaderT[EitherType, UserAnswers, SimplifiedMovementDetails](
            _ => Left(ReaderError(ProcedureTypePage)) //TODO add message
          )
      }
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
