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

import cats.syntax.all._
import com.lucidchart.open.xtract.{__, XmlReader}
import models.LanguageCodeEnglish
import xml.XMLWrites

sealed trait SafetyAndSecurityConsignor

object SafetyAndSecurityConsignor {

  implicit val xmlReader: XmlReader[SafetyAndSecurityConsignor] =
    SafetyAndSecurityConsignorWithEori.xmlReader
      .or(SafetyAndSecurityConsignorWithoutEori.xmlReader)
}

final case class SafetyAndSecurityConsignorWithEori(eori: String) extends SafetyAndSecurityConsignor

object SafetyAndSecurityConsignorWithEori {

  implicit val xmlReader: XmlReader[SafetyAndSecurityConsignorWithEori] = ((__ \ "TINTRACORSEC044").read[String]).map(apply)

  implicit def writes: XMLWrites[SafetyAndSecurityConsignorWithEori] = XMLWrites[SafetyAndSecurityConsignorWithEori] {
    consignor =>
      <TRACORSEC037>
        <TINTRACORSEC044>{consignor.eori}</TINTRACORSEC044>
      </TRACORSEC037>
  }

}

final case class SafetyAndSecurityConsignorWithoutEori(
  name: String,
  streetAndNumber: String,
  postCode: String,
  city: String,
  countryCode: String
) extends SafetyAndSecurityConsignor

object SafetyAndSecurityConsignorWithoutEori {

  implicit val xmlReader: XmlReader[SafetyAndSecurityConsignorWithoutEori] = (
    (__ \ "NamTRACORSEC041").read[String],
    (__ \ "StrNumTRACORSEC043").read[String],
    (__ \ "PosCodTRACORSEC042").read[String],
    (__ \ "CitTRACORSEC038").read[String],
    (__ \ "CouCodTRACORSEC039").read[String]
  ).mapN(apply)

  implicit def writes: XMLWrites[SafetyAndSecurityConsignorWithoutEori] = XMLWrites[SafetyAndSecurityConsignorWithoutEori] {
    consignor =>
      <TRACORSEC037>
        <NamTRACORSEC041>{consignor.name}</NamTRACORSEC041>
        <StrNumTRACORSEC043>{consignor.streetAndNumber}</StrNumTRACORSEC043>
        <PosCodTRACORSEC042>{consignor.postCode}</PosCodTRACORSEC042>
        <CitTRACORSEC038>{consignor.city}</CitTRACORSEC038>
        <CouCodTRACORSEC039>{consignor.countryCode}</CouCodTRACORSEC039>
      </TRACORSEC037>
  }
}
