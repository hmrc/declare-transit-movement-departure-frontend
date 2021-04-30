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

package models.messages.header

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
import scala.xml.Utility.trim

class TransportSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with MessagesModelGenerators with StreamlinedXmlEquality with OptionValues {

  "TransportSpec" - {

    "must serialize Transport to xml" in {
      forAll(arbitrary[Transport]) {
        transport =>
          val inlTraModHEA75 = transport.inlTraModHEA75.map(
            value => <InlTraModHEA75>
              {value.toString}
            </InlTraModHEA75>
          )

          val traModAtBorHEA76 = transport.traModAtBorHEA76.map(
            value => <TraModAtBorHEA76>
              {value.toString}
            </TraModAtBorHEA76>
          )

          val ideOfMeaOfTraAtDHEA78 = transport.ideOfMeaOfTraAtDHEA78.map(
            value => <IdeOfMeaOfTraAtDHEA78>
              {escapeXml(value)}
            </IdeOfMeaOfTraAtDHEA78>
              <IdeOfMeaOfTraAtDHEA78LNG>EN</IdeOfMeaOfTraAtDHEA78LNG>
          )

          val natOfMeaOfTraAtDHEA80 = transport.natOfMeaOfTraAtDHEA80.map(
            value => <NatOfMeaOfTraAtDHEA80>
              {escapeXml(value)}
            </NatOfMeaOfTraAtDHEA80>
          )

          val ideOfMeaOfTraCroHEA85 = transport.ideOfMeaOfTraCroHEA85.map(
            value => <IdeOfMeaOfTraCroHEA85>
              {escapeXml(value)}
            </IdeOfMeaOfTraCroHEA85>
              <IdeOfMeaOfTraCroHEA85LNG>EN</IdeOfMeaOfTraCroHEA85LNG>
          )

          val natOfMeaOfTraCroHEA87 = transport.natOfMeaOfTraCroHEA87.map(
            value => <NatOfMeaOfTraCroHEA87>
              {escapeXml(value)}
            </NatOfMeaOfTraCroHEA87>
          )

          val typOfMeaOfTraCroHEA88 = transport.typOfMeaOfTraCroHEA88.map(
            value => <TypOfMeaOfTraCroHEA88>
              {value.toString}
            </TypOfMeaOfTraCroHEA88>
          )

          val expectedResult: NodeSeq = {
            inlTraModHEA75.getOrElse(NodeSeq.Empty) ++
              traModAtBorHEA76.getOrElse(NodeSeq.Empty) ++
              ideOfMeaOfTraAtDHEA78.getOrElse(NodeSeq.Empty) ++
              natOfMeaOfTraAtDHEA80.getOrElse(NodeSeq.Empty) ++
              ideOfMeaOfTraCroHEA85.getOrElse(NodeSeq.Empty) ++
              natOfMeaOfTraCroHEA87.getOrElse(NodeSeq.Empty) ++
              typOfMeaOfTraCroHEA88.getOrElse(NodeSeq.Empty)
          }

          transport.toXml.map(trim) mustEqual expectedResult.map(trim)
      }
    }

    "must deserialize Xml to Transport" in {
      forAll(arbitrary[Transport]) {
        transport =>
          val result = XmlReader.of[Transport].read(transport.toXml)

          result.toOption.value mustBe transport
      }

    }

  }

}
