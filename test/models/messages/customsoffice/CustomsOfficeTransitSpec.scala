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

package models.messages.customsoffice

import com.lucidchart.open.xtract.XmlReader
import generators.MessagesModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import utils.Format
import xml.XMLWrites._

import scala.xml.NodeSeq

class CustomsOfficeTransitSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  "CustomsOfficeTransitSpec" - {

    "must serialize" - {

      "CustomsOfficeTransit to xml" in {
        forAll(arbitrary[CustomsOfficeTransit]) {
          customsOffice =>
            val arrivalTime = customsOffice.arrivalTime.map {
              arrivalTime =>
                <ArrTimTRACUS085>{Format.dateTimeFormattedIE015(arrivalTime)}</ArrTimTRACUS085>
            }

            val expectedResult =
              <CUSOFFTRARNS>
                <RefNumRNS1>{customsOffice.referenceNumber}</RefNumRNS1>
                {arrivalTime.getOrElse(NodeSeq.Empty)}
              </CUSOFFTRARNS>

            customsOffice.toXml mustEqual expectedResult
        }
      }

      "CustomsOfficeTransit to xml when arrival date is None" in {
        forAll(Gen.pick(CustomsOffice.Constants.length, 'A' to 'Z')) {
          reference =>
            val customsOffice = CustomsOfficeTransit(reference.mkString, None)

            val expectedResult =
              <CUSOFFTRARNS>
                <RefNumRNS1>{customsOffice.referenceNumber}</RefNumRNS1>
              </CUSOFFTRARNS>

            customsOffice.toXml mustEqual expectedResult
        }
      }
    }

    "must deserialize CustomsOfficeTransit from xml" in {
      forAll(arbitrary[CustomsOfficeTransit]) {
        customsOffice =>
          val xml    = customsOffice.toXml
          val result = XmlReader.of[CustomsOfficeTransit].read(xml).toOption.value
          result mustBe customsOffice
      }
    }
  }
}
