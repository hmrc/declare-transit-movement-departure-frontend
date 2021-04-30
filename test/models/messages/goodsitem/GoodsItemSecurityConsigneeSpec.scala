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
import generators.MessagesModelGenerators
import models.LanguageCodeEnglish
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import xml.XMLWrites._

import scala.xml.NodeSeq

//format off
class GoodsItemSecurityConsigneeSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  //format off
  "goodsItemSecurityConsignee" - {

    "must deserialize ItemsSecurityConsigneeWithEori" in {

      forAll(arbitrary[ItemsSecurityConsigneeWithEori]) {
        consignee =>
          val consigneeToXml: NodeSeq = consignee.toXml

          val result = XmlReader.of[GoodsItemSecurityConsignee].read(consigneeToXml).toOption.value

          result mustBe consignee
      }
    }

    "must deserialize ItemsSecurityConsigneeWithoutEori" in {

      forAll(arbitrary[ItemsSecurityConsigneeWithoutEori]) {
        consignee =>
          val consigneeToXml: NodeSeq = consignee.toXml

          val result = XmlReader.of[GoodsItemSecurityConsignee].read(consigneeToXml).toOption.value

          result mustBe consignee
      }
    }

    "must serialize ItemSecurityConsigneeWithEori to xml" in {

      forAll(arbitrary[ItemsSecurityConsigneeWithEori]) {
        consignee =>
          val expectedResult =
            <TRACONSECGOO013>
              <TINTRACONSECGOO020>{consignee.eori}</TINTRACONSECGOO020>
            </TRACONSECGOO013>

          consignee.toXml mustEqual expectedResult
      }
    }

    "must serialize ItemSecurityConsigneeWithoutEori to xml" in {

      forAll(arbitrary[ItemsSecurityConsigneeWithoutEori]) {
        consignee =>
          val expectedResult =
            <TRACONSECGOO013>
              <NamTRACONSECGOO017>{consignee.name}</NamTRACONSECGOO017>
              <StrNumTRACONSECGOO019>{consignee.streetAndNumber}</StrNumTRACONSECGOO019>
              <PosCodTRACONSECGOO018>{consignee.postCode}</PosCodTRACONSECGOO018>
              <CityTRACONSECGOO014>{consignee.city}</CityTRACONSECGOO014>
              <CouCodTRACONSECGOO015>{consignee.countryCode}</CouCodTRACONSECGOO015>
              <TRACONSECGOO013LNG>{LanguageCodeEnglish.code}</TRACONSECGOO013LNG>
            </TRACONSECGOO013>

          consignee.toXml mustEqual expectedResult
      }
    }
  }
}
//format on
