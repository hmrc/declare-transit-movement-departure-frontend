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

import cats.syntax.all._
import com.lucidchart.open.xtract.{__, XmlReader}
import play.api.libs.json.{Json, OWrites}

case class InvalidGuaranteeReasonCode(guaranteeRefNumber: String, code: InvalidGuaranteeCode, reason: Option[String])

object InvalidGuaranteeReasonCode {

  implicit val writes: OWrites[InvalidGuaranteeReasonCode] = Json.writes[InvalidGuaranteeReasonCode]

  implicit val xmlReader: XmlReader[InvalidGuaranteeReasonCode] = (
    (__ \ "GuaRefNumGRNREF21").read[String],
    (__ \ "INVGUARNS" \ "InvGuaReaCodRNS11").read[InvalidGuaranteeCode],
    (__ \ "INVGUARNS" \ "InvGuaReaRNS12").read[String].optional
  ).mapN(apply)
}
