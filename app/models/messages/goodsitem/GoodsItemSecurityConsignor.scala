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

package models.messages.goodsitem

import cats.implicits.catsSyntaxTuple5Semigroupal
import com.lucidchart.open.xtract.{__, XmlReader}
import xml.XMLWrites

trait GoodsItemSecurityConsignor

object GoodsItemSecurityConsignor {

  implicit val xmlReader: XmlReader[GoodsItemSecurityConsignor] =
    ItemsSecurityConsignorWithEori.xmlReader
      .or(ItemsSecurityConsignorWithoutEori.xmlReader)
}

final case class ItemsSecurityConsignorWithEori(eori: String) extends GoodsItemSecurityConsignor

object ItemsSecurityConsignorWithEori {

  implicit def writes: XMLWrites[ItemsSecurityConsignorWithEori] = XMLWrites[ItemsSecurityConsignorWithEori] {
    consignor =>
      <TRACORSECGOO021>
         <TINTRACORSECGOO028>{consignor.eori}</TINTRACORSECGOO028>
      </TRACORSECGOO021>
  }

  implicit val xmlReader: XmlReader[ItemsSecurityConsignorWithEori] = (__ \ "TINTRACORSECGOO028").read[String].map(apply)

}

final case class ItemsSecurityConsignorWithoutEori(
  name: String,
  streetAndNumber: String,
  postCode: String,
  city: String,
  countryCode: String
) extends GoodsItemSecurityConsignor

object ItemsSecurityConsignorWithoutEori {

  implicit def writes: XMLWrites[ItemsSecurityConsignorWithoutEori] = XMLWrites[ItemsSecurityConsignorWithoutEori] {
    consignor =>
      <TRACORSECGOO021>
        <NamTRACORSECGOO025>{consignor.name}</NamTRACORSECGOO025>
        <StrNumTRACORSECGOO027>{consignor.streetAndNumber}</StrNumTRACORSECGOO027>
        <PosCodTRACORSECGOO026>{consignor.postCode}</PosCodTRACORSECGOO026>
        <CitTRACORSECGOO022>{consignor.city}</CitTRACORSECGOO022>
        <CouCodTRACORSECGOO023>{consignor.countryCode}</CouCodTRACORSECGOO023>
      </TRACORSECGOO021>
  }

  implicit val xmlReader: XmlReader[ItemsSecurityConsignorWithoutEori] = (
    (__ \ "NamTRACORSECGOO025").read[String],
    (__ \ "StrNumTRACORSECGOO027").read[String],
    (__ \ "PosCodTRACORSECGOO026").read[String],
    (__ \ "CitTRACORSECGOO022").read[String],
    (__ \ "CouCodTRACORSECGOO023").read[String]
  ).mapN(apply)

}
