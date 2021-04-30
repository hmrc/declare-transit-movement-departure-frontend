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

package models.messages

import com.lucidchart.open.xtract.XmlReader
import generators.MessagesModelGenerators
import models.messages.trader.TraderConsignee
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import xml.XMLWrites._

import scala.xml.NodeSeq

class TraderConsigneeSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  "TraderConsigneeSpec" - {

    "must serialize TraderConsignee to xml" in {
      forAll(arbitrary[TraderConsignee]) {
        trader =>
          val eori = trader.eori.map(
            value => <TINCE159>{value}</TINCE159>
          )

          val expectedResult =
            <TRACONCE1>
              <NamCE17>{escapeXml(trader.name)}</NamCE17>
              <StrAndNumCE122>{escapeXml(trader.streetAndNumber)}</StrAndNumCE122>
              <PosCodCE123>{trader.postCode}</PosCodCE123>
              <CitCE124>{escapeXml(trader.city)}</CitCE124>
              <CouCE125>{trader.countryCode}</CouCE125>
              <NADLNGCE>EN</NADLNGCE>
              {eori.getOrElse(NodeSeq.Empty)}
            </TRACONCE1>

          trader.toXml mustEqual expectedResult
      }

    }

    "must deserialize TraderConsignor from xml" in {
      forAll(arbitrary[TraderConsignee]) {
        data =>
          val xml    = data.toXml
          val result = XmlReader.of[TraderConsignee].read(xml).toOption.value
          result mustBe data
      }
    }
  }
}
