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

package models.messages.goodsitem

import cats.syntax.all._
import com.lucidchart.open.xtract.{__, XmlReader}
import xml.XMLWrites

import scala.xml._

final case class TraderConsigneeGoodsItem(
  name: String,
  streetAndNumber: String,
  postCode: String,
  city: String,
  countryCode: String,
  eori: Option[String]
)

object TraderConsigneeGoodsItem {

  implicit val xmlReader: XmlReader[TraderConsigneeGoodsItem] = (
    (__ \ "NamCE27").read[String],
    (__ \ "StrAndNumCE222").read[String],
    (__ \ "PosCodCE223").read[String],
    (__ \ "CitCE224").read[String],
    (__ \ "CouCE225").read[String],
    (__ \ "TINCE259").read[String].optional
  ).mapN(apply)

  implicit def writes: XMLWrites[TraderConsigneeGoodsItem] = XMLWrites[TraderConsigneeGoodsItem] {
    trader =>
      <TRACONCE2>
        <NamCE27>{trader.name}</NamCE27>
        <StrAndNumCE222>{trader.streetAndNumber}</StrAndNumCE222>
        <PosCodCE223>{trader.postCode}</PosCodCE223>
        <CitCE224>{trader.city}</CitCE224>
        <CouCE225>{trader.countryCode}</CouCE225>
        {
        trader.eori.fold(NodeSeq.Empty) {
          eori =>
            <TINCE259>{eori}</TINCE259>
        }
      }
      </TRACONCE2>
  }
}
