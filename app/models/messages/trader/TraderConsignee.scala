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

package models.messages.trader

import cats.syntax.all._
import com.lucidchart.open.xtract.{__, XmlReader}
import xml.XMLWrites

import scala.xml._

final case class TraderConsignee(
  name: String,
  streetAndNumber: String,
  postCode: String,
  city: String,
  countryCode: String,
  eori: Option[String]
)

object TraderConsignee {

  implicit val xmlReader: XmlReader[TraderConsignee] = (
    (__ \ "NamCE17").read[String],
    (__ \ "StrAndNumCE122").read[String],
    (__ \ "PosCodCE123").read[String],
    (__ \ "CitCE124").read[String],
    (__ \ "CouCE125").read[String],
    (__ \ "TINCE159").read[String].optional
  ).mapN(apply)

  implicit def writes: XMLWrites[TraderConsignee] = XMLWrites[TraderConsignee] {
    trader =>
      <TRACONCE1>
        <NamCE17>{trader.name}</NamCE17>
        <StrAndNumCE122>{trader.streetAndNumber}</StrAndNumCE122>
        <PosCodCE123>{trader.postCode}</PosCodCE123>
        <CitCE124>{trader.city}</CitCE124>
        <CouCE125>{trader.countryCode}</CouCE125>
        {
        trader.eori.fold(NodeSeq.Empty) {
          eori =>
            <TINCE159>{eori}</TINCE159>
        }
      }
      </TRACONCE1>
  }
}
