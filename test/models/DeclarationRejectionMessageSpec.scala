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

package models

import com.lucidchart.open.xtract.XmlReader
import generators.Generators
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import java.time.LocalDate
import scala.xml.Elem

class DeclarationRejectionMessageSpec extends AnyFreeSpec with Generators with ScalaCheckPropertyChecks with Matchers with OptionValues {

  private val validXml: Elem = <CC016A>
    <SynIdeMES1>UNOC</SynIdeMES1>
    <SynVerNumMES2>3</SynVerNumMES2>
    <MesSenMES3>NTA.GB</MesSenMES3>
    <MesRecMES6>SYST17B-NCTS_EU_EXIT</MesRecMES6>
    <DatOfPreMES9>20190913</DatOfPreMES9>
    <TimOfPreMES10>1156</TimOfPreMES10>
    <IntConRefMES11>47390913115628</IntConRefMES11>
    <AppRefMES14>NCTS</AppRefMES14>
    <TesIndMES18>0</TesIndMES18>
    <MesIdeMES19>47390913115628</MesIdeMES19>
    <MesTypMES20>GB016A</MesTypMES20>
    <HEAHEA>
      <RefNumHEA4>05CTC20190913113500</RefNumHEA4>
      <TypOfDecHEA24>T1</TypOfDecHEA24>
      <DecRejDatHEA159>20190913</DecRejDatHEA159>
      <DecRejReaHEA252>The IE015 message received was invalid</DecRejReaHEA252>
    </HEAHEA>
    <FUNERRER1>
      <ErrTypER11>15</ErrTypER11>
      <ErrPoiER12>GUA(1).REF(1).Other guarantee reference</ErrPoiER12>
      <OriAttValER14>EU_EXIT</OriAttValER14>
    </FUNERRER1>
    <FUNERRER1>
      <ErrTypER11>12</ErrTypER11>
      <ErrPoiER12>GUA(1).REF(1).Guarantee reference number (GRN)</ErrPoiER12>
      <OriAttValER14>07IT00000100000Z1</OriAttValER14>
    </FUNERRER1>
  </CC016A>

  "DeclarationRejectionMessage" - {
    "must create valid object when passed valid xml" in {
      val errors = Seq(
        RejectionError("15", "GUA(1).REF(1).Other guarantee reference"),
        RejectionError("12", "GUA(1).REF(1).Guarantee reference number (GRN)")
      )
      val expected = DeclarationRejectionMessage("05CTC20190913113500", LocalDate.parse("2019-09-13"), Some("The IE015 message received was invalid"), errors)
      XmlReader.of[DeclarationRejectionMessage].read(validXml).toOption.value mustBe expected
    }
  }
}
