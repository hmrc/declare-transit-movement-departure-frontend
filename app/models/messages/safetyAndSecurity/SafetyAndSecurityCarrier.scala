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

trait SafetyAndSecurityCarrier

object SafetyAndSecurityCarrier {

  implicit val xmlReader: XmlReader[SafetyAndSecurityCarrier] =
    SafetyAndSecurityCarrierWithEori.xmlReader
      .or(SafetyAndSecurityCarrierWithoutEori.xmlReader)
}

final case class SafetyAndSecurityCarrierWithEori(eori: String) extends SafetyAndSecurityCarrier

object SafetyAndSecurityCarrierWithEori {

  implicit val xmlReader: XmlReader[SafetyAndSecurityCarrierWithEori] = (
    (__ \ "TINCARTRA254").read[String]
  ).map(apply)

  implicit def writes: XMLWrites[SafetyAndSecurityCarrierWithEori] = XMLWrites[SafetyAndSecurityCarrierWithEori] {
    carrier =>
      <CARTRA100>
        <TINCARTRA254>{carrier.eori}</TINCARTRA254>
      </CARTRA100>
  }
}

final case class SafetyAndSecurityCarrierWithoutEori(
  name: String,
  streetAndNumber: String,
  postCode: String,
  city: String,
  countryCode: String
) extends SafetyAndSecurityCarrier

object SafetyAndSecurityCarrierWithoutEori {

  implicit val xmlReader: XmlReader[SafetyAndSecurityCarrierWithoutEori] = (
    (__ \ "NamCARTRA121").read[String],
    (__ \ "StrAndNumCARTRA254").read[String],
    (__ \ "PosCodCARTRA121").read[String],
    (__ \ "CitCARTRA789").read[String],
    (__ \ "CouCodCARTRA587").read[String],
  ).mapN(apply)

  implicit def writes: XMLWrites[SafetyAndSecurityCarrierWithoutEori] = XMLWrites[SafetyAndSecurityCarrierWithoutEori] {
    carrier =>
      <CARTRA100>
        <NamCARTRA121>{carrier.name}</NamCARTRA121>
        <StrAndNumCARTRA254>{carrier.streetAndNumber}</StrAndNumCARTRA254>
        <PosCodCARTRA121>{carrier.postCode}</PosCodCARTRA121>
        <CitCARTRA789>{carrier.city}</CitCARTRA789>
        <CouCodCARTRA587>{carrier.countryCode}</CouCodCARTRA587>
      </CARTRA100>
  }
}
