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

trait SafetyAndSecurityConsignee

object SafetyAndSecurityConsignee {

  implicit val xmlReader: XmlReader[SafetyAndSecurityConsignee] =
    SafetyAndSecurityConsigneeWithEori.xmlReader
      .or(SafetyAndSecurityConsigneeWithoutEori.xmlReader)
}

final case class SafetyAndSecurityConsigneeWithEori(eori: String) extends SafetyAndSecurityConsignee

object SafetyAndSecurityConsigneeWithEori {

  implicit val xmlReader: XmlReader[SafetyAndSecurityConsigneeWithEori] = (
    (__ \ "TINTRACONSEC036").read[String]
  ).map(apply)

  implicit def writes: XMLWrites[SafetyAndSecurityConsigneeWithEori] = XMLWrites[SafetyAndSecurityConsigneeWithEori] {
    consignee =>
      <TRACONSEC029>
        <TINTRACONSEC036>{consignee.eori}</TINTRACONSEC036>
      </TRACONSEC029>
  }
}

final case class SafetyAndSecurityConsigneeWithoutEori(
  name: String,
  streetAndNumber: String,
  postCode: String,
  city: String,
  countryCode: String
) extends SafetyAndSecurityConsignee

object SafetyAndSecurityConsigneeWithoutEori {

  implicit val xmlReader: XmlReader[SafetyAndSecurityConsigneeWithoutEori] = (
    (__ \ "NameTRACONSEC033").read[String],
    (__ \ "StrNumTRACONSEC035").read[String],
    (__ \ "PosCodTRACONSEC034").read[String],
    (__ \ "CitTRACONSEC030").read[String],
    (__ \ "CouCodTRACONSEC031").read[String],
  ).mapN(apply)

  implicit def writes: XMLWrites[SafetyAndSecurityConsigneeWithoutEori] = XMLWrites[SafetyAndSecurityConsigneeWithoutEori] {
    consignee =>
      <TRACONSEC029>
        <NameTRACONSEC033>{consignee.name}</NameTRACONSEC033>
        <StrNumTRACONSEC035>{consignee.streetAndNumber}</StrNumTRACONSEC035>
        <PosCodTRACONSEC034>{consignee.postCode}</PosCodTRACONSEC034>
        <CitTRACONSEC030>{consignee.city}</CitTRACONSEC030>
        <CouCodTRACONSEC031>{consignee.countryCode}</CouCodTRACONSEC031>
      </TRACONSEC029>
  }
}
