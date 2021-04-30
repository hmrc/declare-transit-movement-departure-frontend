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
import models.LanguageCodeEnglish
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import xml.XMLWrites._

import scala.xml.Node

class SealsSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with MessagesModelGenerators with StreamlinedXmlEquality with OptionValues {

  "SealsSpec" - {

    "must serialize Seals to xml" in {
      forAll(arbitrary[Seals]) {
        seals =>
          val expectedResult: Node =
            <SEAINFSLI>
              <SeaNumSLI2>{seals.numberOfSeals}</SeaNumSLI2>
              {
              seals.SealId.map {
                id =>
                  <SEAIDSID>
                      <SeaIdeSID1>{id}</SeaIdeSID1>
                      <SeaIdeSID1LNG>{LanguageCodeEnglish.code}</SeaIdeSID1LNG>
                    </SEAIDSID>
              }
            }
            </SEAINFSLI>

          seals.toXml mustEqual expectedResult
      }

    }

    "must deserialize Seals from xml" in {
      forAll(arbitrary[Seals]) {
        data =>
          val xml    = data.toXml
          val result = XmlReader.of[Seals].read(xml).toOption.value
          result mustBe data
      }
    }
  }

}
