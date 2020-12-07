/*
 * Copyright 2020 HM Revenue & Customs
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
import models.Index
import pages.addItems._

final case class ProducedDocument(documentType: String, documentReference: String, extraInformation: Option[String])

object ProducedDocument {

  def producedDocumentReader(index: Index, referenceIndex: Index): UserAnswersReader[ProducedDocument] =
    (
      DocumentTypePage(index, referenceIndex).reader,
      DocumentReferencePage(index, referenceIndex).reader,
      addExtraInformationAnswer(index, referenceIndex)
    ).tupled.map((ProducedDocument.apply _).tupled)

  private def addExtraInformationAnswer(index: Index, referenceIndex: Index): UserAnswersReader[Option[String]] =
    AddExtraInformationPage(index, referenceIndex).reader.flatMap(
      isTrue =>
        if (isTrue) {
          DocumentExtraInformationPage(index, referenceIndex).reader.map(Some(_))
        } else {
          none[String].pure[UserAnswersReader]
      }
    )
}
