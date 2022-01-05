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

package models.messages.guarantee

import cats.syntax.all._
import com.lucidchart.open.xtract.{__, XmlReader}
import xml.XMLWrites

import scala.xml.NodeSeq

sealed trait GuaranteeReference

object GuaranteeReference {

  object Constants {
    val accessCodeLength = 4
  }

  implicit lazy val xmlReader: XmlReader[GuaranteeReference] =
    GuaranteeReferenceWithGrn.xmlReader.or(GuaranteeReferenceWithOther.xmlReader)
}
case class GuaranteeReferenceWithGrn(guaranteeReferenceNumber: String, accessCode: String) extends GuaranteeReference

object GuaranteeReferenceWithGrn {

  object Constants {
    val guaranteeReferenceNumberLength = 24
    val grnOtherTypeLength             = 17
  }

  implicit val xmlReader: XmlReader[GuaranteeReferenceWithGrn] = ((__ \ "GuaRefNumGRNREF1").read[String], (__ \ "AccCodREF6").read[String]).mapN(apply)

  implicit def writes: XMLWrites[GuaranteeReferenceWithGrn] = XMLWrites[GuaranteeReferenceWithGrn] {
    guarantee =>
      <GUAREFREF>
        <GuaRefNumGRNREF1>{guarantee.guaranteeReferenceNumber}</GuaRefNumGRNREF1>
        <AccCodREF6>{guarantee.accessCode}</AccCodREF6>
      </GUAREFREF>
  }
}

case class GuaranteeReferenceWithOther(otherReferenceNumber: String, accessCode: Option[String]) extends GuaranteeReference

object GuaranteeReferenceWithOther {

  object Constants {
    val otherReferenceNumberLength = 35
  }

  implicit val xmlReader: XmlReader[GuaranteeReferenceWithOther] = ((__ \ "OthGuaRefREF4").read[String], (__ \ "AccCodREF6").read[String].optional).mapN(apply)

  implicit def writes: XMLWrites[GuaranteeReferenceWithOther] = XMLWrites[GuaranteeReferenceWithOther] {
    guarantee =>
      <GUAREFREF>
        <OthGuaRefREF4>{guarantee.otherReferenceNumber}</OthGuaRefREF4>
        {
        guarantee.accessCode.fold(NodeSeq.Empty) {
          accessCode =>
            <AccCodREF6>{accessCode}</AccCodREF6>
        }
      }
      </GUAREFREF>
  }
}
