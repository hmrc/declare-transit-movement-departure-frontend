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
import org.scalacheck.Gen
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import xml.XMLWrites._

class CustomsOfficeDepartureSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  "CustomsOfficeDepartureSpec" - {

    "must serialize CustomsOfficeDeparture to xml" in {
      forAll(Gen.pick(CustomsOffice.Constants.length, 'A' to 'Z')) {
        reference =>
          val customsOffice = CustomsOfficeDeparture(reference.mkString)

          val expectedResult =
            <CUSOFFDEPEPT>
              <RefNumEPT1>{customsOffice.referenceNumber}</RefNumEPT1>
            </CUSOFFDEPEPT>

          customsOffice.toXml mustEqual expectedResult
      }

    }

    "must deserialize CustomsOfficeDeparture from xml" in {
      forAll(Gen.pick(CustomsOffice.Constants.length, 'A' to 'Z')) {
        reference =>
          val customsOffice = CustomsOfficeDeparture(reference.mkString)

          val xml    = customsOffice.toXml
          val result = XmlReader.of[CustomsOfficeDeparture].read(xml).toOption.value
          result mustBe customsOffice
      }
    }
  }
}
