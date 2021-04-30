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
import models.messages.escapeXml
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import xml.XMLWrites._

import scala.xml.NodeSeq

class TraderConsigneeGoodsItemSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  "TraderConsigneeGoodsItemSpec" - {

    "must serialize TraderConsigneeGoodsItem to xml" in {
      forAll(arbitrary[TraderConsigneeGoodsItem]) {
        trader =>
          val eori = trader.eori.map(
            value => <TINCE259>{value}</TINCE259>
          )

          val expectedResult =
            <TRACONCE2>
              <NamCE27>{escapeXml(trader.name)}</NamCE27>
              <StrAndNumCE222>{escapeXml(trader.streetAndNumber)}</StrAndNumCE222>
              <PosCodCE223>{trader.postCode}</PosCodCE223>
              <CitCE224>{escapeXml(trader.city)}</CitCE224>
              <CouCE225>{trader.countryCode}</CouCE225>
              <NADLNGGICE>EN</NADLNGGICE>
              {eori.getOrElse(NodeSeq.Empty)}
            </TRACONCE2>

          trader.toXml mustEqual expectedResult
      }

    }

    "must deserialize TraderConsigneeGoodsItem from xml" in {
      forAll(arbitrary[TraderConsigneeGoodsItem]) {
        data =>
          val xml    = data.toXml
          val result = XmlReader.of[TraderConsigneeGoodsItem].read(xml).toOption.value
          result mustBe data
      }
    }
  }
}
