/*
 * Copyright 2023 HM Revenue & Customs
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

import cats.data.{NonEmptyList, ReaderT}
import cats.implicits._
import derivable.DeriveNumberOfDocuments
import models.DeclarationType.Option4
import models.reference.CircumstanceIndicator
import models.{Index, UserAnswers}
import pages.addItems._
import pages.safetyAndSecurity.{AddCircumstanceIndicatorPage, AddCommercialReferenceNumberPage, CircumstanceIndicatorPage}
import pages.{AddSecurityDetailsPage, DeclarationTypePage}

sealed trait ProducedDocument

final case class StandardDocument(documentType: String, documentReference: String, extraInformation: Option[String]) extends ProducedDocument

final case class TIRDocument(carnetReference: String, extraInformation: String) extends ProducedDocument

object ProducedDocument {

  private def producedDocumentsWithTIR(itemIndex: Index): ReaderT[EitherType, UserAnswers, Option[NonEmptyList[ProducedDocument]]] =
    DeclarationTypePage
      .filterMandatoryDependent(_ == Option4) {
        DeriveNumberOfDocuments(itemIndex).mandatoryNonEmptyListReader.flatMap {
          _.zipWithIndex
            .traverse[UserAnswersReader, ProducedDocument]({
              case (_, 0)     => ProducedDocument.tirDocumentReader(itemIndex, Index(0)).widen[ProducedDocument]
              case (_, index) => ProducedDocument.standardDocumentReader(itemIndex, Index(index)).widen[ProducedDocument]
            })
        }
      }
      .map(_.some)

  private def producedDocumentsWithConditionalIndicator(itemIndex: Index): ReaderT[EitherType, UserAnswers, Option[NonEmptyList[ProducedDocument]]] =
    AddSecurityDetailsPage
      .filterMandatoryDependent(identity) {
        AddCommercialReferenceNumberPage.filterMandatoryDependent(_ == false) {
          AddCircumstanceIndicatorPage.filterMandatoryDependent(_ == true) {
            CircumstanceIndicatorPage.filterMandatoryDependent(
              x => CircumstanceIndicator.conditionalIndicators.contains(x)
            ) {
              DeriveNumberOfDocuments(itemIndex).mandatoryNonEmptyListReader.flatMap {
                _.zipWithIndex
                  .traverse[UserAnswersReader, ProducedDocument]({
                    case (_, index) =>
                      ProducedDocument.standardDocumentReader(itemIndex, Index(index)).widen[ProducedDocument]
                  })
              }
            }
          }
        }
      }
      .map(_.some)

  private def producedDocumentsWithoutConditionalIndicator(itemIndex: Index): ReaderT[EitherType, UserAnswers, Option[NonEmptyList[ProducedDocument]]] =
    AddSecurityDetailsPage
      .filterMandatoryDependent(identity) {
        AddCommercialReferenceNumberPage.filterMandatoryDependent(_ == false) {
          AddCircumstanceIndicatorPage.filterMandatoryDependent(_ == false) {
            DeriveNumberOfDocuments(itemIndex).mandatoryNonEmptyListReader.flatMap {
              _.zipWithIndex
                .traverse[UserAnswersReader, ProducedDocument]({
                  case (_, index) =>
                    ProducedDocument.standardDocumentReader(itemIndex, Index(index)).widen[ProducedDocument]
                })
            }
          }
        }
      }
      .map(_.some)

  private def producedDocumentsOther(itemIndex: Index): ReaderT[EitherType, UserAnswers, Option[NonEmptyList[ProducedDocument]]] =
    AddDocumentsPage(itemIndex).filterOptionalDependent(identity) {
      DeriveNumberOfDocuments(itemIndex).mandatoryNonEmptyListReader.flatMap {
        _.zipWithIndex
          .traverse[UserAnswersReader, ProducedDocument]({
            case (_, index) =>
              ProducedDocument.standardDocumentReader(itemIndex, Index(index)).widen[ProducedDocument]
          })
      }
    }

  def deriveProducedDocuments(itemIndex: Index): ReaderT[EitherType, UserAnswers, Option[NonEmptyList[ProducedDocument]]] =
    if (itemIndex.position == 0) {
      producedDocumentsWithTIR(itemIndex) orElse
        producedDocumentsWithConditionalIndicator(itemIndex) orElse
        producedDocumentsWithoutConditionalIndicator(itemIndex) orElse
        producedDocumentsOther(itemIndex)
    } else {
      producedDocumentsOther(itemIndex)
    }

  def standardDocumentReader(index: Index, referenceIndex: Index): UserAnswersReader[StandardDocument] =
    (
      DocumentTypePage(index, referenceIndex).reader,
      DocumentReferencePage(index, referenceIndex).reader,
      addExtraInformationAnswer(index, referenceIndex)
    ).tupled.map((StandardDocument.apply _).tupled)

  def tirDocumentReader(index: Index, documentIndex: Index): UserAnswersReader[TIRDocument] =
    (
      TIRCarnetReferencePage(index, documentIndex).reader,
      DocumentExtraInformationPage(index, documentIndex).reader
    ).tupled.map((TIRDocument.apply _).tupled)

  private def addExtraInformationAnswer(index: Index, referenceIndex: Index): UserAnswersReader[Option[String]] =
    AddExtraDocumentInformationPage(index, referenceIndex).filterOptionalDependent(identity) {
      DocumentExtraInformationPage(index, referenceIndex).reader
    }
}
