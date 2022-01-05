/*
 * Copyright 2022 HM Revenue & Customs
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

package models.messages

import com.lucidchart.open.xtract.{__, XmlReader}
import models.XMLReads._
import utils.Format
import xml.XMLWrites

import java.time.LocalDate

case class ControlResult(datLimERS69: LocalDate)

object ControlResult {

  object Constants {
    val dateLimitLength = 8
  }

  implicit val xmlReader: XmlReader[ControlResult] =
    (__ \ "DatLimERS69").read[LocalDate].map(apply)

  implicit def writes: XMLWrites[ControlResult] = XMLWrites[ControlResult] {
    controlResult =>
      <CONRESERS>
        <ConResCodERS16>A3</ConResCodERS16>
        <DatLimERS69>{Format.dateFormatted(controlResult.datLimERS69)}</DatLimERS69>
      </CONRESERS>
  }
}
