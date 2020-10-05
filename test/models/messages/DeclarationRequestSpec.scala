/*
 * Copyright 2020 HM Revenue & Customs
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

import generators.MessagesModelGenerators
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalacheck.Arbitrary._
import com.lucidchart.open.xtract.XmlReader

import scala.xml.Node
import models.XMLWrites._
import scala.xml.Utility.trim

class DeclarationRequestSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  "DeclarationRequest" - {

    "must serialise DeclarationRequest to xml" in {
      //TODO: This needs more xml nodes adding as models become available
      forAll(arbitrary[DeclarationRequest]) {
        unloadingRemarksRequest =>
          val expectedResult: Node =
            <CC015B>
              {unloadingRemarksRequest.meta.toXml}
            </CC015B>

          unloadingRemarksRequest.toXml.map(trim) mustBe expectedResult.map(trim)
      }

    }

    "must de-serialise xml to DeclarationRequest" in {

      forAll(arbitrary[DeclarationRequest]) {
        unloadingRemarksRequest =>
          val result = XmlReader.of[DeclarationRequest].read(unloadingRemarksRequest.toXml)
          result.toOption.value mustBe unloadingRemarksRequest
      }

    }

  }

}
