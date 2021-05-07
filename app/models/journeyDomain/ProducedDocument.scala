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

import cats.data.{NonEmptyList, ReaderT}
import cats.implicits._
import derivable.DeriveNumberOfDocuments
import models.reference.CircumstanceIndicator
import models.{Index, UserAnswers}
import pages.AddSecurityDetailsPage
import pages.addItems._
import pages.safetyAndSecurity.{AddCircumstanceIndicatorPage, AddCommercialReferenceNumberPage, CircumstanceIndicatorPage}

final case class ProducedDocument(documentType: String, documentReference: String, extraInformation: Option[String])

object ProducedDocument {

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
                      ProducedDocument.producedDocumentReader(itemIndex, Index(index))
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
                    ProducedDocument.producedDocumentReader(itemIndex, Index(index))
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
              ProducedDocument.producedDocumentReader(itemIndex, Index(index))
          })
      }
    }

  def deriveProducedDocuments(itemIndex: Index): ReaderT[EitherType, UserAnswers, Option[NonEmptyList[ProducedDocument]]] =
    if (itemIndex.position == 0) {
      producedDocumentsWithConditionalIndicator(itemIndex) orElse
        producedDocumentsWithoutConditionalIndicator(itemIndex) orElse
        producedDocumentsOther(itemIndex)
    } else {
      producedDocumentsOther(itemIndex)
    }

  def producedDocumentReader(index: Index, referenceIndex: Index): UserAnswersReader[ProducedDocument] =
    (
      DocumentTypePage(index, referenceIndex).reader,
      DocumentReferencePage(index, referenceIndex).reader,
      addExtraInformationAnswer(index, referenceIndex)
    ).tupled.map((ProducedDocument.apply _).tupled)

  private def addExtraInformationAnswer(index: Index, referenceIndex: Index): UserAnswersReader[Option[String]] =
    AddExtraDocumentInformationPage(index, referenceIndex).filterOptionalDependent(identity) {
      DocumentExtraInformationPage(index, referenceIndex).reader
    }
}
