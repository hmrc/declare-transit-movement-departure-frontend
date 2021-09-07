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
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import xml.XMLWrites._

import scala.xml.Node

class ItinerarySpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with MessagesModelGenerators with StreamlinedXmlEquality with OptionValues {

  "ItinerarySpec" - {

    "must serialize Itinerary to xml" in {
      forAll(arbitrary[Itinerary]) {
        itinerary =>
          val expectedResult: Node =
            <ITI>
              <CouOfRouCodITI1>{itinerary.countryCode}</CouOfRouCodITI1>
            </ITI>

          itinerary.toXml mustEqual expectedResult
      }

    }

    "must deserialize Itinerary from xml" in {
      forAll(arbitrary[Itinerary]) {
        data =>
          val xml    = data.toXml
          val result = XmlReader.of[Itinerary].read(xml).toOption.value
          result mustBe data
      }
    }
  }

}
