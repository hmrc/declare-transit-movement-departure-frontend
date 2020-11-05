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

package models.messages.goodsitem

import com.lucidchart.open.xtract.{ParseError, ParseFailure, ParseSuccess, XmlReader}
import models.XMLWrites
import play.api.libs.json._
import utils.BinaryToBooleanXMLReader._

trait SpecialMention {

  def additionalInformationCoded: String
}

object SpecialMention {

  object Constants {
    val specialMentionCount = 99
  }

  val countrySpecificCodes = Seq("DG0", "DG1")

  implicit val xmlReader: XmlReader[SpecialMention] = {

    SpecialMentionEc.xmlReader
      .or(SpecialMentionNonEc.xmlReader)
      .or(SpecialMentionNoCountry.xmlReader)

  }

  implicit lazy val reads: Reads[SpecialMention] = {

    implicit class ReadsWithContravariantOr[A](a: Reads[A]) {

      def or[B >: A](b: Reads[B]): Reads[B] =
        a.map[B](identity).orElse(b)
    }

    implicit def convertToSupertype[A, B >: A](a: Reads[A]): Reads[B] =
      a.map(identity)

    SpecialMentionEc.reads or
      SpecialMentionNonEc.reads or
      SpecialMentionNoCountry.reads
  }

  implicit lazy val writes: OWrites[SpecialMention] = OWrites {
    case sm: SpecialMentionEc        => Json.toJsObject(sm)(SpecialMentionEc.writes)
    case sm: SpecialMentionNonEc     => Json.toJsObject(sm)(SpecialMentionNonEc.writes)
    case sm: SpecialMentionNoCountry => Json.toJsObject(sm)(SpecialMentionNoCountry.writes)
  }

}

final case class SpecialMentionEc(additionalInformationCoded: String) extends SpecialMention

object SpecialMentionEc {

  implicit val xmlReader: XmlReader[SpecialMentionEc] = {

    import com.lucidchart.open.xtract.__

    case class SpecialMentionEcParseFailure(message: String) extends ParseError

    (__ \ "ExpFroECMT24")
      .read[Boolean]
      .flatMap {
        case true  => XmlReader(_ => ParseSuccess(true))
        case false => XmlReader(_ => ParseFailure(SpecialMentionEcParseFailure("Failed to parse to SpecialMentionEc: ExpFroECMT24 was false")))
      }
      .flatMap {
        _ =>
          (__ \ "AddInfCodMT23").read[String].flatMap {
            code =>
              if (SpecialMention.countrySpecificCodes.contains(code)) {
                XmlReader(_ => ParseSuccess(SpecialMentionEc(code)))
              } else {
                XmlReader(_ => ParseFailure(SpecialMentionEcParseFailure(s"Failed to parse to SpecialMentionEc: $code was not country specific")))
              }
          }
      }
  }

  implicit lazy val reads: Reads[SpecialMentionEc] = {

    import play.api.libs.functional.syntax._

    (__ \ "exportFromEc")
      .read[Boolean]
      .flatMap[Boolean] {
        fromEc =>
          if (fromEc) {
            Reads(_ => JsSuccess(fromEc))
          } else {
            Reads(_ => JsError("exportFromEc must be true"))
          }
      }
      .andKeep(
        (__ \ "additionalInformationCoded")
          .read[String]
          .flatMap[String] {
            code =>
              if (SpecialMention.countrySpecificCodes.contains(code)) {
                Reads(_ => JsSuccess(code))
              } else {
                Reads(_ => JsError(s"additionalInformationCoded must be in ${SpecialMention.countrySpecificCodes}"))
              }
          }
      )
      .andKeep((__ \ "additionalInformationCoded")
        .read[String]
        .map(SpecialMentionEc(_)))
  }

  implicit lazy val writes: OWrites[SpecialMentionEc] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "exportFromEc").write[Boolean] and
        (__ \ "additionalInformationCoded").write[String]
    )(s => (true, s.additionalInformationCoded))
  }

  implicit def writesXml: XMLWrites[SpecialMentionEc] = XMLWrites[SpecialMentionEc] {
    specialMention =>
      <SPEMENMT2>
        <AddInfCodMT23>{specialMention.additionalInformationCoded}</AddInfCodMT23>
        <ExpFroECMT24>1</ExpFroECMT24>
      </SPEMENMT2>
  }
}

final case class SpecialMentionNonEc(
  additionalInformationCoded: String,
  exportFromCountry: String
) extends SpecialMention

object SpecialMentionNonEc {

  implicit val xmlReader: XmlReader[SpecialMentionNonEc] = {

    import com.lucidchart.open.xtract.__

    case class SpecialMentionNonEcParseFailure(message: String) extends ParseError

    (__ \ "ExpFroECMT24")
      .read[Boolean]
      .flatMap {
        case true  => XmlReader(_ => ParseFailure(SpecialMentionNonEcParseFailure("Failed to parse to SpecialMentionNonEc: ExpFroECMT24 was true")))
        case false => XmlReader(_ => ParseSuccess(false))
      }
      .flatMap {
        _ =>
          (__ \ "AddInfCodMT23").read[String].flatMap {
            code =>
              if (SpecialMention.countrySpecificCodes.contains(code)) {
                XmlReader(_ => ParseSuccess(code))
              } else {
                XmlReader(_ => ParseFailure(SpecialMentionNonEcParseFailure(s"Failed to parse to SpecialMentionNonEc: $code was not country specific")))
              }
          }
      }
      .flatMap {
        code =>
          (__ \ "ExpFroCouMT25").read[String].flatMap {
            exportFromCountry =>
              XmlReader(_ => ParseSuccess(SpecialMentionNonEc(code, exportFromCountry)))
          }
      }
  }

  implicit lazy val reads: Reads[SpecialMentionNonEc] = {

    import play.api.libs.functional.syntax._

    (__ \ "exportFromEc")
      .read[Boolean]
      .flatMap[Boolean] {
        fromEc =>
          if (fromEc) {
            Reads(_ => JsError("exportFromEc must be false"))
          } else {
            Reads(_ => JsSuccess(fromEc))
          }
      }
      .andKeep(
        (__ \ "additionalInformationCoded")
          .read[String]
          .flatMap[String] {
            code =>
              if (SpecialMention.countrySpecificCodes.contains(code)) {
                Reads(_ => JsSuccess(code))
              } else {
                Reads(_ => JsError(s"additionalInformationCoded must be in ${SpecialMention.countrySpecificCodes}"))
              }
          }
      )
      .andKeep(
        (
          (__ \ "additionalInformationCoded").read[String] and
            (__ \ "exportFromCountry").read[String]
        )(SpecialMentionNonEc(_, _))
      )
  }

  implicit lazy val writes: OWrites[SpecialMentionNonEc] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "exportFromEc").write[Boolean] and
        (__ \ "additionalInformationCoded").write[String] and
        (__ \ "exportFromCountry").write[String]
    )(s => (false, s.additionalInformationCoded, s.exportFromCountry))
  }

  implicit def writesXml: XMLWrites[SpecialMentionNonEc] = XMLWrites[SpecialMentionNonEc] {
    specialMention =>
      <SPEMENMT2>
        <AddInfCodMT23>{specialMention.additionalInformationCoded}</AddInfCodMT23>
        <ExpFroECMT24>0</ExpFroECMT24>
        <ExpFroCouMT25>{specialMention.exportFromCountry}</ExpFroCouMT25>
      </SPEMENMT2>
  }
}

final case class SpecialMentionNoCountry(additionalInformationCoded: String) extends SpecialMention

object SpecialMentionNoCountry {

  implicit val xmlReader: XmlReader[SpecialMentionNoCountry] = {

    import com.lucidchart.open.xtract.__

    case class SpecialMentionNoCountryParseFailure(message: String) extends ParseError

    (__ \ "AddInfCodMT23").read[String].flatMap {
      code =>
        if (SpecialMention.countrySpecificCodes.contains(code)) {
          XmlReader(_ => ParseFailure(SpecialMentionNoCountryParseFailure(s"Failed to parse to SpecialMentionNoCountry: $code was country specific")))
        } else {
          XmlReader(_ => ParseSuccess(SpecialMentionNoCountry(code)))
        }
    }
  }

  implicit lazy val reads: Reads[SpecialMentionNoCountry] = {

    import play.api.libs.functional.syntax._

    (__ \ "additionalInformationCoded")
      .read[String]
      .flatMap[String] {
        code =>
          if (SpecialMention.countrySpecificCodes.contains(code)) {
            Reads(_ => JsError(s"additionalInformationCoded must not be in ${SpecialMention.countrySpecificCodes}"))
          } else {
            Reads(_ => JsSuccess(code))
          }
      }
      .andKeep((__ \ "additionalInformationCoded")
        .read[String]
        .map(SpecialMentionNoCountry(_)))
  }

  implicit lazy val writes: OWrites[SpecialMentionNoCountry] = Json.writes[SpecialMentionNoCountry]

  implicit def writesXml: XMLWrites[SpecialMentionNoCountry] = XMLWrites[SpecialMentionNoCountry] {
    specialMention =>
      <SPEMENMT2>
        <AddInfCodMT23>{specialMention.additionalInformationCoded}</AddInfCodMT23>
      </SPEMENMT2>
  }
}
