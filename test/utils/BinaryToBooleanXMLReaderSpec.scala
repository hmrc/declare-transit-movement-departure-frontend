/*
 * Copyright 2023 HM Revenue & Customs
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

package utils

import com.lucidchart.open.xtract.XmlReader
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import utils.BinaryToBooleanXMLReader._

class BinaryToBooleanXMLReaderSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "BinaryToBooleanXMLReader" - {

    "must convert 0 to false" in {

      val xml = <testXml>0</testXml>

      val result = XmlReader.of[Boolean].read(xml).toOption.value

      result mustBe false
    }
  }

  "must convert 1 to true" in {

    val xml = <testXml>1</testXml>

    val result = XmlReader.of[Boolean].read(xml).toOption.value

    result mustBe true
  }

  "must fail to deserialise if given invalid value" in {

    val xml = <testXml>Invalid value</testXml>

    val result = XmlReader.of[Boolean].read(xml).toOption

    result mustBe None
  }
}
