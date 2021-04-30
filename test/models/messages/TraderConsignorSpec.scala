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
import models.messages.trader._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import xml.XMLWrites._

import scala.xml.NodeSeq

class TraderConsignorSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  "TraderConsignorSpec" - {

    "must serialize TraderConsignor to xml" in {
      forAll(arbitrary[TraderConsignor]) {
        trader =>
          val eori = trader.eori.map(
            value => <TINCO159>
            {value}
          </TINCO159>
          )

          val expectedResult =
            <TRACONCO1>
              <NamCO17>
                {escapeXml(trader.name)}
              </NamCO17>
              <StrAndNumCO122>
                {escapeXml(trader.streetAndNumber)}
              </StrAndNumCO122>
              <PosCodCO123>
                {trader.postCode}
              </PosCodCO123>
              <CitCO124>
                {escapeXml(trader.city)}
              </CitCO124>
              <CouCO125>
                {trader.countryCode}
              </CouCO125>
              <TRACONCO1LNG>EN</TRACONCO1LNG>
              {eori.getOrElse(NodeSeq.Empty)}
            </TRACONCO1>

          trader.toXml mustEqual expectedResult
      }

    }

    "must deserialize TraderConsignor from xml" in {
      forAll(arbitrary[TraderConsignor]) {
        data =>
          val xml    = data.toXml
          val result = XmlReader.of[TraderConsignor].read(xml).toOption.value
          result mustBe data
      }
    }

  }

}
