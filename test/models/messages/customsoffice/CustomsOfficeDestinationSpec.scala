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

class CustomsOfficeDestinationSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  "CustomsOfficeDestinationSpec" - {

    "must serialize CustomsOfficeDestination to xml" in {
      forAll(Gen.pick(CustomsOffice.Constants.length, 'A' to 'Z')) {
        reference =>
          val customsOffice = CustomsOfficeDestination(reference.mkString)

          val expectedResult =
            <CUSOFFDESEST>
              <RefNumEST1>{customsOffice.referenceNumber}</RefNumEST1>
            </CUSOFFDESEST>

          customsOffice.toXml mustEqual expectedResult
      }

    }

    "must deserialize CustomsOfficeDestination from xml" in {
      forAll(Gen.pick(CustomsOffice.Constants.length, 'A' to 'Z')) {
        reference =>
          val customsOffice = CustomsOfficeDestination(reference.mkString)

          val xml    = customsOffice.toXml
          val result = XmlReader.of[CustomsOfficeDestination].read(xml).toOption.value
          result mustBe customsOffice
      }
    }
  }
}
