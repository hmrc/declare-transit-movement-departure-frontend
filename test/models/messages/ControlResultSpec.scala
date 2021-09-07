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
import utils.Format
import xml.XMLWrites._

class ControlResultSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  "ControlResultSpec" - {

    "must serialize ControlResult to xml" in {
      forAll(arbitrary[ControlResult]) {
        controlResult =>
          val expectedResult =
            <CONRESERS>
              <ConResCodERS16>A3</ConResCodERS16>
              <DatLimERS69>{Format.dateFormatted(controlResult.datLimERS69)}</DatLimERS69>
            </CONRESERS>

          controlResult.toXml mustEqual expectedResult
      }

    }

    "must deserialize ControlResult from xml" in {
      forAll(arbitrary[ControlResult]) {
        data =>
          val xml    = data.toXml
          val result = XmlReader.of[ControlResult].read(xml).toOption.value
          result mustBe data
      }
    }
  }
}
