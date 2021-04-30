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

package models.messages.goodsitem

import com.lucidchart.open.xtract.XmlReader
import generators.MessagesModelGenerators
import models.LanguageCodeEnglish
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import xml.XMLWrites._

import scala.xml.NodeSeq

class PreviousAdministrativeReferenceSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  "PreviousAdministrativeReferenceSpec" - {

    "must serialize PreviousAdministrativeReference to xml" in {

      forAll(arbitrary[PreviousAdministrativeReference]) {
        reference =>
          val comOfInfAR29 = reference.comOfInfAR29.fold(NodeSeq.Empty)(
            value => <ComOfInfAR29>{value}</ComOfInfAR29>
              <ComOfInfAR29LNG>EN</ComOfInfAR29LNG>
          )

          val expectedResult =
            <PREADMREFAR2>
              <PreDocTypAR21>{reference.preDocTypAR21}</PreDocTypAR21>
              <PreDocRefAR26>{reference.preDocRefAR26}</PreDocRefAR26>
              <PreDocRefLNG>{LanguageCodeEnglish.code}</PreDocRefLNG>
              {comOfInfAR29}
            </PREADMREFAR2>

          reference.toXml mustEqual expectedResult
      }
    }

    "must deserialize PreviousAdministrativeReference from xml" in {
      forAll(arbitrary[PreviousAdministrativeReference]) {
        data =>
          val xml    = data.toXml
          val result = XmlReader.of[PreviousAdministrativeReference].read(xml).toOption.value
          result mustBe data
      }
    }
  }
}
