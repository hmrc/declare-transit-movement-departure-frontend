/*
 * Copyright 2020 HM Revenue & Customs
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

import generators.MessagesModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import models.XMLWrites._

import scala.xml.NodeSeq

class TraderPrincipalSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  "TraderPrincipalSpec" - {

    "must serialize TraderPrincipalWithEori to xml" in {

      forAll(arbitrary[TraderPrincipalWithEori]) {
        trader =>
          val nameNode        = trader.name.map(value => <NamPC17>{escapeXml(value)}</NamPC17>)
          val streetNameNode  = trader.streetAndNumber.map(value => <StrAndNumPC122>{value}</StrAndNumPC122>)
          val postCodeNode    = trader.postCode.map(value => <PosCodPC123>{value}</PosCodPC123>)
          val cityNode        = trader.city.map(value => <CitPC124>{value}</CitPC124>)
          val countryCodeNode = trader.countryCode.map(value => <CouPC125>{value}</CouPC125>)

          val expectedResult =
            <TRAPRIPC1>
              {nameNode.getOrElse(NodeSeq.Empty) ++
              streetNameNode.getOrElse(NodeSeq.Empty) ++
              postCodeNode.getOrElse(NodeSeq.Empty) ++
              cityNode.getOrElse(NodeSeq.Empty) ++
              countryCodeNode.getOrElse(NodeSeq.Empty)}
              <NADLNGPC>EN</NADLNGPC>
              <TINPC159>{trader.eori}</TINPC159>
            </TRAPRIPC1>

          trader.toXml mustEqual expectedResult
      }

    }

    "must serialize TraderPrincipalWithoutEori to xml" in {
      forAll(arbitrary[TraderPrincipalWithoutEori]) {
        trader =>
          val expectedResult =
            <TRAPRIPC1>
              <NamPC17>{escapeXml(trader.name)}</NamPC17>
              <StrAndNumPC122>{trader.streetAndNumber}</StrAndNumPC122>
              <PosCodPC123>{trader.postCode}</PosCodPC123>
              <CitPC124>{trader.city}</CitPC124>
              <CouPC125>{trader.countryCode}</CouPC125>
              <NADLNGPC>EN</NADLNGPC>
            </TRAPRIPC1>

          trader.toXml mustEqual expectedResult
      }

    }

  }

}
