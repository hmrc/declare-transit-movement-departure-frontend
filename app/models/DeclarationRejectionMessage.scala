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

package models

import java.time.LocalDate
import cats.syntax.all._
import com.lucidchart.open.xtract.{__, XmlReader}
import play.api.libs.json.{Json, OWrites}
import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import models.XMLReads._

case class DeclarationRejectionMessage(reference: String, rejectionDate: LocalDate, reason: Option[String], errors: Seq[RejectionError])

object DeclarationRejectionMessage {

  implicit val writes: OWrites[DeclarationRejectionMessage] = Json.writes[DeclarationRejectionMessage]

  implicit val xmlReader: XmlReader[DeclarationRejectionMessage] = (
    (__ \ "HEAHEA" \ "RefNumHEA4").read[String],
    (__ \ "HEAHEA" \ "DecRejDatHEA159").read[LocalDate],
    (__ \ "HEAHEA" \ "DecRejReaHEA252").read[Option[String]],
    (__ \ "FUNERRER1").read(strictReadSeq[RejectionError])
  ).mapN(apply)
}

case class RejectionError(errorType: String, pointer: String, reason: Option[String])

object RejectionError {

  implicit val writes: OWrites[RejectionError] = Json.writes[RejectionError]

  implicit val xmlReader: XmlReader[RejectionError] = (
    (__ \ "ErrTypER11").read[String],
    (__ \ "ErrPoiER12").read[String],
    (__ \ "ErrReaER13").read[Option[String]]
  ).mapN(apply)
}
