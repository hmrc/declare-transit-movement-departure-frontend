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

class SafetyAndSecurityConsigneeSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  "SafetyAndSecurityConsignee" - {

    "must serialise and deserialise SafetyAndSecurityConsigneeWithEori" in {

      forAll(arbitrary[SafetyAndSecurityConsigneeWithEori]) {
        safetyAndSecurityConsigneeWithEori =>
          val result = XmlReader.of[SafetyAndSecurityConsignee].read(safetyAndSecurityConsigneeWithEori.toXml).toOption.value

          result mustBe safetyAndSecurityConsigneeWithEori
      }
    }

    "must serialise and deserialise SafetyAndSecurityConsigneeWithoutEori" in {

      forAll(arbitrary[SafetyAndSecurityConsigneeWithoutEori]) {
        safetyAndSecurityConsigneeWithoutEori =>
          val result = XmlReader.of[SafetyAndSecurityConsigneeWithoutEori].read(safetyAndSecurityConsigneeWithoutEori.toXml).toOption.value

          result mustBe safetyAndSecurityConsigneeWithoutEori
      }
    }
  }

}
