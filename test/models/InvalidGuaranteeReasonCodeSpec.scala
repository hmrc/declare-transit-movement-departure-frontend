/*
 * Copyright 2022 HM Revenue & Customs
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

package models

import base.SpecBase
import com.lucidchart.open.xtract.{ParseFailure, XmlReader}
import generators.MessagesModelGenerators
import models.InvalidGuaranteeCode.G01
import org.scalacheck.Arbitrary._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.xml.NodeSeq

class InvalidGuaranteeReasonCodeSpec extends SpecBase with MessagesModelGenerators with ScalaCheckPropertyChecks {

  "InvalidGuaranteeReasonCode" - {
    "must read xml" in {
      forAll(arbitrary[InvalidGuaranteeReasonCode]) {
        reasonCode =>
          val reasonOptXml = reasonCode.reason.fold[NodeSeq](NodeSeq.Empty)(
            x => <InvGuaReaRNS12>{x}</InvGuaReaRNS12>
          )
          val xml: NodeSeq =
            <GUAREF2>
              <GuaRefNumGRNREF21>{reasonCode.guaranteeRefNumber}</GuaRefNumGRNREF21>
              <INVGUARNS>
                <InvGuaReaCodRNS11>{reasonCode.code.value}</InvGuaReaCodRNS11>
                ++ {reasonOptXml}
              </INVGUARNS>
            </GUAREF2>

          XmlReader.of[InvalidGuaranteeReasonCode].read(xml).toOption.value mustBe reasonCode
      }
    }

    "must fail to read xml for invalid input " in {

      val xml: NodeSeq =
        <GUAREF2>
              <INVGUARNS>
                <InvGuaReaCodRNS11>{G01.value}</InvGuaReaCodRNS11>
              </INVGUARNS>
            </GUAREF2>

      XmlReader.of[InvalidGuaranteeReasonCode].read(xml) mustBe a[ParseFailure]
    }

  }
}
