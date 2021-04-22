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

import cats.implicits.catsSyntaxTuple2Semigroupal
import com.lucidchart.open.xtract.{ParseError, ParseFailure, ParseSuccess, XmlReader}
import play.api.libs.json._
import utils.BinaryToBooleanXMLReader._
import xml.XMLWrites

trait SpecialMention {

  def additionalInformationCoded: String
}

object SpecialMention {

  object Constants {
    val specialMentionCount = 99
  }

  val countrySpecificCodes = Seq("DG0", "DG1")

  implicit val xmlReader: XmlReader[SpecialMention] =
    SpecialMentionGuaranteeLiabilityAmount.xmlReader
      .or(SpecialMentionExportFromGB.xmlReader)
      .or(SpecialMentionExportFromNI.xmlReader)
      .or(SpecialMentionNoCountry.xmlReader)
}

final case class SpecialMentionExportFromGB(additionalInformationCoded: String, additionalInformation: String) extends SpecialMention

object SpecialMentionExportFromGB {

  implicit val xmlReader: XmlReader[SpecialMentionExportFromGB] = {

    import com.lucidchart.open.xtract.__

    case class SpecialMentionExportFromGBParseFailure(message: String) extends ParseError

    (__ \ "ExpFroCouMT25")
      .read[String]
      .flatMap {
        case "GB" =>
          XmlReader(
            _ => ParseSuccess(true)
          )
        case _ =>
          XmlReader(
            _ => ParseFailure(SpecialMentionExportFromGBParseFailure("Failed to parse to SpecialMentionExportFromGB: ExpFroCouMT25 was not GB"))
          )
      }
      .flatMap {
        _ =>
          (
            (__ \ "AddInfCodMT23").read[String],
            (__ \ "AddInfMT21").read[String]
          ).tupled.flatMap {
            case (code, addInfo) =>
              if (SpecialMention.countrySpecificCodes.contains(code)) {
                XmlReader(
                  _ => ParseSuccess(SpecialMentionExportFromGB(code, addInfo))
                )
              } else {
                XmlReader(
                  _ => ParseFailure(SpecialMentionExportFromGBParseFailure(s"Failed to parse to SpecialMentionExportFromGB: $code was not DG0 or DG1"))
                )
              }
          }
      }
  }

  implicit def writesXml: XMLWrites[SpecialMentionExportFromGB] = XMLWrites[SpecialMentionExportFromGB] {
    specialMention =>
      <SPEMENMT2>
        <AddInfMT21>{specialMention.additionalInformation}</AddInfMT21>
        <AddInfCodMT23>{specialMention.additionalInformationCoded}</AddInfCodMT23>
        <ExpFroCouMT25>GB</ExpFroCouMT25>
      </SPEMENMT2>
  }
}

final case class SpecialMentionExportFromNI(
  additionalInformationCoded: String,
  additionalInformation: String
) extends SpecialMention

object SpecialMentionExportFromNI {

  implicit val xmlReader: XmlReader[SpecialMentionExportFromNI] = {

    import com.lucidchart.open.xtract.__

    case class SpecialMentionExportFromNIParseFailure(message: String) extends ParseError

    (__ \ "ExpFroECMT24")
      .read[String]
      .flatMap {
        case "1" =>
          XmlReader(
            _ => ParseSuccess(true)
          )
        case _ =>
          XmlReader(
            _ => ParseFailure(SpecialMentionExportFromNIParseFailure(s"Failed to parse to SpecialMentionExportFromNI: ExpFroECMT24 was not 1"))
          )
      }
      .flatMap {
        _ =>
          (
            (__ \ "AddInfCodMT23").read[String],
            (__ \ "AddInfMT21").read[String]
          ).tupled.flatMap {
            case (code, addInfo) =>
              if (SpecialMention.countrySpecificCodes.contains(code)) {
                XmlReader(
                  _ => ParseSuccess(SpecialMentionExportFromNI(code, addInfo))
                )
              } else {
                XmlReader(
                  _ =>
                    ParseFailure(SpecialMentionExportFromNIParseFailure(s"Failed to parse to SpecialMentionExportFromNIParseFailure: $code was not DG0 or DG1"))
                )
              }
          }
      }
  }

  implicit def writesXml: XMLWrites[SpecialMentionExportFromNI] = XMLWrites[SpecialMentionExportFromNI] {
    specialMention =>
      <SPEMENMT2>
        <AddInfMT21>{specialMention.additionalInformation}</AddInfMT21>
        <AddInfCodMT23>{specialMention.additionalInformationCoded}</AddInfCodMT23>
        <ExpFroECMT24>1</ExpFroECMT24>
      </SPEMENMT2>
  }
}

final case class SpecialMentionNoCountry(additionalInformationCoded: String, additionalInformation: String) extends SpecialMention

object SpecialMentionNoCountry {

  implicit val xmlReader: XmlReader[SpecialMentionNoCountry] = {

    import com.lucidchart.open.xtract.__

    case class SpecialMentionNoCountryParseFailure(message: String) extends ParseError

    (
      (__ \ "AddInfCodMT23").read[String],
      (__ \ "AddInfMT21").read[String]
    ).tupled.flatMap {
      case (code, _) if SpecialMention.countrySpecificCodes.contains(code) =>
        XmlReader(
          _ => ParseFailure(SpecialMentionNoCountryParseFailure(s"Failed to parse to SpecialMentionNoCountry cannot be DG0 or DG1"))
        )
      case (code, info) =>
        XmlReader(
          _ => ParseSuccess(SpecialMentionNoCountry(code, info))
        )
    }
  }

  implicit def writesXml: XMLWrites[SpecialMentionNoCountry] = XMLWrites[SpecialMentionNoCountry] {
    specialMention =>
      <SPEMENMT2>
        <AddInfMT21>{specialMention.additionalInformation}</AddInfMT21>
        <AddInfCodMT23>{specialMention.additionalInformationCoded}</AddInfCodMT23>
      </SPEMENMT2>
  }
}

final case class SpecialMentionGuaranteeLiabilityAmount(
  additionalInformationCoded: String,
  additionalInformationOfLiabilityAmount: String
) extends SpecialMention

object SpecialMentionGuaranteeLiabilityAmount {

  implicit val xmlReader: XmlReader[SpecialMentionGuaranteeLiabilityAmount] = {

    import com.lucidchart.open.xtract.__

    case class SpecialMentionGuaranteeLiabilityAmountParseFailure(message: String) extends ParseError

    (__ \ "AddInfCodMT23").read[String].flatMap {
      case "CAL" => {
        (__ \ "AddInfMT21").read[String].flatMap {
          liabilityAmount =>
            XmlReader(
              _ => ParseSuccess(SpecialMentionGuaranteeLiabilityAmount("CAL", liabilityAmount))
            )
        }
      }
      case _ =>
        XmlReader(
          _ => ParseFailure(SpecialMentionGuaranteeLiabilityAmountParseFailure(s"Failed to parse to SpecialMentionGuaranteeLiabilityAmount does not exist"))
        )
    }
  }

  implicit def writesXml: XMLWrites[SpecialMentionGuaranteeLiabilityAmount] = XMLWrites[SpecialMentionGuaranteeLiabilityAmount] {
    specialMention =>
      <SPEMENMT2>
        <AddInfCodMT23>CAL</AddInfCodMT23>
        <AddInfMT21>{specialMention.additionalInformationOfLiabilityAmount}</AddInfMT21>
      </SPEMENMT2>
  }

}
