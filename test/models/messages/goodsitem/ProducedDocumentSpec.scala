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
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import xml.XMLWrites._

import scala.xml.NodeSeq

class ProducedDocumentSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  "ProducedDocumentSpec" - {

    "must serialize ProducedDocument to xml" in {

      forAll(arbitrary[ProducedDocument]) {
        producedDocument =>
          val reference = producedDocument.reference.fold(NodeSeq.Empty)(
            value => <DocRefDC23>{value}</DocRefDC23>
          )

          val complementOfInformation = producedDocument.complementOfInformation.fold(NodeSeq.Empty)(
            value => <ComOfInfDC25>{value}</ComOfInfDC25>
            <ComOfInfDC25LNG>EN</ComOfInfDC25LNG>
          )

          val expectedResult = <PRODOCDC2>
            <DocTypDC21>{producedDocument.documentType}</DocTypDC21>
            {reference}
            <DocRefDCLNG>EN</DocRefDCLNG>
            {complementOfInformation}
          </PRODOCDC2>

          producedDocument.toXml mustEqual expectedResult
      }
    }

    "must deserialize ProducedDocument from xml" in {
      forAll(arbitrary[ProducedDocument]) {
        data =>
          val xml    = data.toXml
          val result = XmlReader.of[ProducedDocument].read(xml).toOption.value
          result mustBe data
      }
    }
  }
}
