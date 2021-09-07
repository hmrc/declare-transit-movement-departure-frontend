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

package models.messages.safetyAndSecurity

import com.lucidchart.open.xtract.XmlReader
import generators.MessagesModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import xml.XMLWrites.XMLWritesOps

class SafetyAndSecurityConsignorSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  "SafetyAndSecurityConsignor" - {

    "must serialise and deserialise SafetyAndSecurityConsignorWithEori" in {

      forAll(arbitrary[SafetyAndSecurityCarrierWithEori]) {
        safetyAndSecurityCarrierWithEori =>
          val result = XmlReader.of[SafetyAndSecurityCarrier].read(safetyAndSecurityCarrierWithEori.toXml).toOption.value

          result mustBe safetyAndSecurityCarrierWithEori
      }
    }

    "must serialise and deserialise SafetyAndSecurityConsignorWithoutEori" in {

      forAll(arbitrary[SafetyAndSecurityCarrierWithoutEori]) {
        safetyAndSecurityCarrierWithoutEori =>
          val result = XmlReader.of[SafetyAndSecurityCarrier].read(safetyAndSecurityCarrierWithoutEori.toXml).toOption.value

          result mustBe safetyAndSecurityCarrierWithoutEori
      }
    }
  }

}
