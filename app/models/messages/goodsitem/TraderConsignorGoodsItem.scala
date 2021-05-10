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
import models.messages.escapeXml
import xml.XMLWrites

import scala.xml._

final case class TraderConsignorGoodsItem(
  name: String,
  streetAndNumber: String,
  postCode: String,
  city: String,
  countryCode: String,
  eori: Option[String]
)

object TraderConsignorGoodsItem {

  implicit val xmlReader: XmlReader[TraderConsignorGoodsItem] = (
    (__ \ "NamCO27").read[String],
    (__ \ "StrAndNumCO222").read[String],
    (__ \ "PosCodCO223").read[String],
    (__ \ "CitCO224").read[String],
    (__ \ "CouCO225").read[String],
    (__ \ "TINCO259").read[String].optional
  ).mapN(apply)

  implicit def writes: XMLWrites[TraderConsignorGoodsItem] = XMLWrites[TraderConsignorGoodsItem] {
    trader =>
      <TRACONCO2>
        <NamCO27>{escapeXml(trader.name)}</NamCO27>
        <StrAndNumCO222>{escapeXml(trader.streetAndNumber)}</StrAndNumCO222>
        <PosCodCO223>{trader.postCode}</PosCodCO223>
        <CitCO224>{escapeXml(trader.city)}</CitCO224>
        <CouCO225>{trader.countryCode}</CouCO225>
        {
        trader.eori.fold(NodeSeq.Empty) {
          eori =>
            <TINCO259>{eori}</TINCO259>
        }
      }
      </TRACONCO2>
  }
}
