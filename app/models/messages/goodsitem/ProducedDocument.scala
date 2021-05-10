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

package models.messages.goodsitem

import cats.syntax.all._
import com.lucidchart.open.xtract.{__, XmlReader}
import models.LanguageCodeEnglish
import xml.XMLWrites

import scala.xml.NodeSeq

final case class ProducedDocument(
  documentType: String, //CL13 ref: Document Type (Common)
  reference: Option[String],
  complementOfInformation: Option[String]
)

object ProducedDocument {

  object Constants {
    val documentTypeLength      = 4
    val reference               = 35
    val complementOfInformation = 26
    val producedDocumentCount   = 99
  }

  implicit val xmlReader: XmlReader[ProducedDocument] = (
    (__ \ "DocTypDC21").read[String],
    (__ \ "DocRefDC23").read[String].optional,
    (__ \ "ComOfInfDC25").read[String].optional
  ).mapN(apply)

  implicit def writes: XMLWrites[ProducedDocument] = XMLWrites[ProducedDocument] {
    references =>
      val reference = references.reference.fold(NodeSeq.Empty)(
        value => <DocRefDC23>{value}</DocRefDC23>
      )

      val complementOfInformation = references.complementOfInformation.fold(NodeSeq.Empty)(
        value => <ComOfInfDC25>{value}</ComOfInfDC25>
      )

      <PRODOCDC2>
        <DocTypDC21>{references.documentType}</DocTypDC21>
        {reference}
        {complementOfInformation}
      </PRODOCDC2>
  }
}
