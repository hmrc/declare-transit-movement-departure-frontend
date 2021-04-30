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

package models.messages.guarantee

import com.lucidchart.open.xtract.XmlReader
import generators.MessagesModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import xml.XMLWrites._

import scala.xml.NodeSeq

class GuaranteeReferenceSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  "must serialize" - {

    "GuaranteeReferenceWithGrn to xml" in {
      forAll(arbitrary[GuaranteeReferenceWithGrn]) {
        reference =>
          val expectedResult =
            <GUAREFREF>
              <GuaRefNumGRNREF1>{reference.guaranteeReferenceNumber}</GuaRefNumGRNREF1>
              <AccCodREF6>{reference.accessCode}</AccCodREF6>
            </GUAREFREF>

          reference.toXml mustEqual expectedResult
      }
    }

    "GuaranteeReferenceWithOther to xml" in {
      forAll(arbitrary[GuaranteeReferenceWithOther]) {
        reference =>
          val accessCode = reference.accessCode.map(
            value => <AccCodREF6>{value}</AccCodREF6>
          )

          val expectedResult =
            <GUAREFREF>
              <OthGuaRefREF4>{reference.otherReferenceNumber}</OthGuaRefREF4>
              {accessCode.getOrElse(NodeSeq.Empty)}
            </GUAREFREF>

          reference.toXml mustEqual expectedResult
      }
    }

  }

  "must deserialize" - {

    "GuaranteeReferenceWithGrn from xml" in {
      forAll(arbitrary[GuaranteeReferenceWithGrn]) {
        data =>
          val xml    = data.toXml
          val result = XmlReader.of[GuaranteeReferenceWithGrn].read(xml).toOption.value
          result mustBe data
      }
    }

    "GuaranteeReferenceWithOther from xml" in {
      forAll(arbitrary[GuaranteeReferenceWithOther]) {
        data =>
          val xml    = data.toXml
          val result = XmlReader.of[GuaranteeReferenceWithOther].read(xml).toOption.value
          result mustBe data
      }
    }

  }

}
