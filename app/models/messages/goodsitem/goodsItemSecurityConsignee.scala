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

import com.lucidchart.open.xtract.XmlReader
import models.LanguageCodeEnglish
import xml.XMLWrites

trait GoodsItemSecurityConsignee

object GoodsItemSecurityConsignee

final case class ItemsSecurityConsigneeWithEori(eori: String) extends GoodsItemSecurityConsignee
//format off

object ItemsSecurityConsigneeWithEori {

  implicit def writes: XMLWrites[ItemsSecurityConsigneeWithEori] = XMLWrites[ItemsSecurityConsigneeWithEori] {
    consignee =>
      <TRACONSECGOO013>
        <TINTRACONSECGOO020>{consignee.eori}</TINTRACONSECGOO020>
      </TRACONSECGOO013>
  }
}
final case class ItemsSecurityConsigneeWithoutEori(
  name: String,
  streetAndNumber: String,
  postCode: String,
  city: String,
  countryCode: String
) extends GoodsItemSecurityConsignee

object ItemsSecurityConsigneeWithoutEori {

  implicit def writes: XMLWrites[ItemsSecurityConsigneeWithoutEori] = XMLWrites[ItemsSecurityConsigneeWithoutEori] {
    consignee =>
      <TRACONSECGOO013>
        <NamTRACONSECGOO017>{consignee.name}</NamTRACONSECGOO017>
        <StrNumTRACONSECGOO019>{consignee.streetAndNumber}</StrNumTRACONSECGOO019>
        <PosCodTRACONSECGOO018>{consignee.postCode}</PosCodTRACONSECGOO018>
        <CityTRACONSECGOO014>{consignee.city}</CityTRACONSECGOO014>
        <CouCodTRACONSECGOO015>{consignee.countryCode}</CouCodTRACONSECGOO015>
        <TRACONSECGOO013LNG>{LanguageCodeEnglish.code}</TRACONSECGOO013LNG>
      </TRACONSECGOO013>
  }

}
//format on
