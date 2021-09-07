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

import com.lucidchart.open.xtract.{__, XmlReader}
import play.api.libs.json.{Json, Writes}

sealed abstract class InvalidGuaranteeCode(val value: String)

object InvalidGuaranteeCode {

  implicit val xmlReader: XmlReader[InvalidGuaranteeCode] =
    __.read[String].map {
      code =>
        values
          .find(_.value.equalsIgnoreCase(code.trim()))
          .getOrElse(DefaultCode(code.trim))
    }

  implicit val writes: Writes[InvalidGuaranteeCode] = Writes[InvalidGuaranteeCode] {
    invalidCode: InvalidGuaranteeCode =>
      Json.obj("value" -> invalidCode.value)
  }

  val values = Seq(G01, G02, G03, G04, G05, G06, G07, G08, G09, G10, G11, G12, G13)

  case object G01 extends InvalidGuaranteeCode("G01")
  case object G02 extends InvalidGuaranteeCode("G02")
  case object G03 extends InvalidGuaranteeCode("G03")
  case object G04 extends InvalidGuaranteeCode("G04")
  case object G05 extends InvalidGuaranteeCode("G05")
  case object G06 extends InvalidGuaranteeCode("G06")
  case object G07 extends InvalidGuaranteeCode("G07")
  case object G08 extends InvalidGuaranteeCode("G08")
  case object G09 extends InvalidGuaranteeCode("G09")
  case object G10 extends InvalidGuaranteeCode("G10")
  case object G11 extends InvalidGuaranteeCode("G11")
  case object G12 extends InvalidGuaranteeCode("G12")
  case object G13 extends InvalidGuaranteeCode("G13")
  case class DefaultCode(override val value: String) extends InvalidGuaranteeCode(value)

}
