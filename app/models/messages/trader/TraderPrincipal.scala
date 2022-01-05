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

package models.messages.trader

import cats.syntax.all._
import com.lucidchart.open.xtract.{__, XmlReader}
import models.messages.escapeXml
import xml.XMLWrites

import scala.xml._

sealed trait TraderPrincipal

object TraderPrincipal {

  implicit lazy val xmlReader: XmlReader[TraderPrincipal] =
    TraderPrincipalWithEori.xmlReader.or(TraderPrincipalWithoutEori.xmlReader)
}

final case class TraderPrincipalWithEori(
  eori: String,
  name: Option[String],
  streetAndNumber: Option[String],
  postCode: Option[String],
  city: Option[String],
  countryCode: Option[String],
  principalTirHolderId: Option[String]
) extends TraderPrincipal

object TraderPrincipalWithEori {

  implicit val xmlReader: XmlReader[TraderPrincipalWithEori] = (
    (__ \ "TINPC159").read[String],
    (__ \ "NamPC17").read[String].optional,
    (__ \ "StrAndNumPC122").read[String].optional,
    (__ \ "PosCodPC123").read[String].optional,
    (__ \ "CitPC124").read[String].optional,
    (__ \ "CouPC125").read[String].optional,
    (__ \ "HITPC126").read[String].optional
  ).mapN(apply)

  implicit def writes: XMLWrites[TraderPrincipalWithEori] = XMLWrites[TraderPrincipalWithEori] {
    trader =>
      <TRAPRIPC1>
        {
        trader.name.fold(NodeSeq.Empty) {
          name =>
            <NamPC17>{escapeXml(name)}</NamPC17>
        } ++
          trader.streetAndNumber.fold(NodeSeq.Empty) {
            streetAndNumber =>
              <StrAndNumPC122>{escapeXml(streetAndNumber)}</StrAndNumPC122>
          } ++
          trader.postCode.fold(NodeSeq.Empty) {
            postCode =>
              <PosCodPC123>{postCode}</PosCodPC123>
          } ++
          trader.city.fold(NodeSeq.Empty) {
            city =>
              <CitPC124>{escapeXml(city)}</CitPC124>
          } ++
          trader.countryCode.fold(NodeSeq.Empty) {
            countryCode =>
              <CouPC125>{countryCode}</CouPC125>
          }
      }<TINPC159>{trader.eori}</TINPC159>
        {
        trader.principalTirHolderId.fold(NodeSeq.Empty) {
          value =>
            <HITPC126>{value}</HITPC126>
        }
      }
      </TRAPRIPC1>
  }
}

final case class TraderPrincipalWithoutEori(
  name: String,
  streetAndNumber: String,
  postCode: String,
  city: String,
  countryCode: String,
  principalTirHolderId: Option[String]
) extends TraderPrincipal

object TraderPrincipalWithoutEori {

  implicit val xmlReader: XmlReader[TraderPrincipalWithoutEori] = (
    (__ \ "NamPC17").read[String],
    (__ \ "StrAndNumPC122").read[String],
    (__ \ "PosCodPC123").read[String],
    (__ \ "CitPC124").read[String],
    (__ \ "CouPC125").read[String],
    (__ \ "HITPC126").read[String].optional
  ).mapN(apply)

  implicit def writes: XMLWrites[TraderPrincipalWithoutEori] = XMLWrites[TraderPrincipalWithoutEori] {
    trader =>
      <TRAPRIPC1>
            <NamPC17>{escapeXml(trader.name)}</NamPC17>
            <StrAndNumPC122>{escapeXml(trader.streetAndNumber)}</StrAndNumPC122>
            <PosCodPC123>{trader.postCode}</PosCodPC123>
            <CitPC124>{escapeXml(trader.city)}</CitPC124>
            <CouPC125>{trader.countryCode}</CouPC125>
            {
        trader.principalTirHolderId.fold(NodeSeq.Empty) {
          value => <HITPC126>{value}</HITPC126>
        }
      }
          </TRAPRIPC1>
  }

}
