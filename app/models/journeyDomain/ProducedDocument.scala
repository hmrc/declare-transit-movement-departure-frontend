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
import models.{Index, UserAnswers}
import models.reference.CircumstanceIndicator
import pages.AddSecurityDetailsPage
import pages.addItems._
import pages.safetyAndSecurity.{AddCircumstanceIndicatorPage, AddCommercialReferenceNumberPage, CircumstanceIndicatorPage}

final case class ProducedDocument(documentType: String, documentReference: String, extraInformation: Option[String])

object ProducedDocument {

  private def readDocumentType(itemIndex: Index): ReaderT[EitherType, UserAnswers, Boolean] =
    (for {
      addSecurity     <- AddSecurityDetailsPage.reader
      addRef          <- AddCommercialReferenceNumberPage.optionalReader
      addCircumstance <- AddCircumstanceIndicatorPage.optionalReader
    } yield {
      (addSecurity, addRef, addCircumstance, itemIndex.position == 0) match {
        case (true, Some(false), Some(false), true) => true.pure[UserAnswersReader]
        case (true, Some(false), Some(true), true)  => CircumstanceIndicatorPage.reader.map(x => CircumstanceIndicator.conditionalIndicators.contains(x))
        case _                                      => AddDocumentsPage(itemIndex).reader
      }
    }).flatMap(x => x)

  def deriveProducedDocuments(itemIndex: Index): UserAnswersReader[Option[NonEmptyList[ProducedDocument]]] =
    readDocumentType(itemIndex)
      .flatMap {
        isTrue =>
          if (isTrue) {
            DeriveNumberOfDocuments(itemIndex).optionalNonEmptyListReader.flatMap {
              _.traverse {
                _.zipWithIndex
                  .traverse[UserAnswersReader, ProducedDocument]({
                    case (_, index) =>
                      ProducedDocument.producedDocumentReader(itemIndex, Index(index))
                  })
              }
            }
          } else {
            none[NonEmptyList[ProducedDocument]].pure[UserAnswersReader]
          }
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
