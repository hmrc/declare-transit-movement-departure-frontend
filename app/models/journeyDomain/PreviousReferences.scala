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

import cats.data.NonEmptyList
import cats.implicits._
import derivable.DeriveNumberOfPreviousAdministrativeReferences
import models.DeclarationType.{Option2, Option3}
import models.Index
import models.reference.{CountryCode, CountryOfDispatch}
import pages.addItems._
import pages.{CountryOfDispatchPage, DeclarationTypePage}

final case class PreviousReferences(
  referenceType: String,
  previousReference: String,
  extraInformation: Option[String]
)

object PreviousReferences {

  def previousReferenceReader(itemIndex: Index, referenceIndex: Index): UserAnswersReader[PreviousReferences] = {

    val extraInformation: UserAnswersReader[Option[String]] =
      AddExtraInformationPage(itemIndex, referenceIndex).filterOptionalDependent(identity) {
        ExtraInformationPage(itemIndex, referenceIndex).reader
      }

    (
      ReferenceTypePage(itemIndex, referenceIndex).reader,
      PreviousReferencePage(itemIndex, referenceIndex).reader,
      extraInformation
    ).tupled.map((PreviousReferences.apply _).tupled)
  }

  def derivePreviousReferences(itemIndex: Index): UserAnswersReader[Option[NonEmptyList[PreviousReferences]]] =
    (
      DeclarationTypePage.reader,
      IsNonEuOfficePage.reader
    ).tupled.flatMap {
      case (Option2, true) | (Option3, true) =>
        allPreviousReferencesReader(itemIndex)
      case _ =>
        AddAdministrativeReferencePage(itemIndex).reader.flatMap { // Optional reader if any other condition
          case true  => allPreviousReferencesReader(itemIndex)
          case false => none[NonEmptyList[PreviousReferences]].pure[UserAnswersReader]
        }
    }

  private def allPreviousReferencesReader(itemIndex: Index): UserAnswersReader[Option[NonEmptyList[PreviousReferences]]] =
    DeriveNumberOfPreviousAdministrativeReferences(itemIndex).optionalNonEmptyListReader.flatMap {
      _.traverse(
        _.zipWithIndex
          .traverse[UserAnswersReader, PreviousReferences]({
            case (_, index) =>
              PreviousReferences.previousReferenceReader(itemIndex, Index(index))
          })
      )
    }

}
