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

package models.messages.trader

import cats.syntax.all._
import com.lucidchart.open.xtract.{__, XmlReader}
import models.messages.escapeXml
import xml.XMLWrites

import scala.xml._

final case class TraderConsignor(
  name: String,
  streetAndNumber: String,
  postCode: String,
  city: String,
  countryCode: String,
  eori: Option[String]
)

object TraderConsignor {

  implicit val xmlReader: XmlReader[TraderConsignor] = (
    (__ \ "NamCO17").read[String],
    (__ \ "StrAndNumCO122").read[String],
    (__ \ "PosCodCO123").read[String],
    (__ \ "CitCO124").read[String],
    (__ \ "CouCO125").read[String],
    (__ \ "TINCO159").read[String].optional
  ).mapN(apply)

  implicit def writes: XMLWrites[TraderConsignor] = XMLWrites[TraderConsignor] {
    trader =>
      <TRACONCO1>
        <NamCO17>{escapeXml(trader.name)}</NamCO17>
        <StrAndNumCO122>{escapeXml(trader.streetAndNumber)}</StrAndNumCO122>
        <PosCodCO123>{trader.postCode}</PosCodCO123>
        <CitCO124>{escapeXml(trader.city)}</CitCO124>
        <CouCO125>{trader.countryCode}</CouCO125>
        {
        trader.eori.fold(NodeSeq.Empty) {
          eori =>
            <TINCO159>{eori}</TINCO159>
        }
      }
      </TRACONCO1>
  }
}
