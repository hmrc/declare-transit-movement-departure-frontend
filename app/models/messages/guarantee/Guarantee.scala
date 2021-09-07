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

package models.messages.guarantee

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import com.lucidchart.open.xtract.{__, XmlReader}
import xml.XMLWrites
import xml.XMLWrites._

import scala.xml.NodeSeq

case class Guarantee(guaranteeType: String, guaranteeReference: Seq[GuaranteeReference])

object Guarantee {

  object Constants {
    val guaranteeTypeLength     = 1
    val guaranteeReferenceCount = 99
    val requiredKey             = "liabilityAmount.error.required"
    val lengthKey               = "liabilityAmount.error.length"
    val invalidCharactersKey    = "liabilityAmount.error.characters"
    val invalidFormatKey        = "liabilityAmount.error.invalidFormat"
    val greaterThanZeroErrorKey = "liabilityAmount.error.greaterThanZero"
    val maxLength               = 100
  }

  implicit val xmlReader: XmlReader[Guarantee] = (
    (__ \ "GuaTypGUA1").read[String],
    (__ \ "GUAREFREF").read(strictReadSeq[GuaranteeReference])
  ).mapN(apply)

  implicit def writes: XMLWrites[Guarantee] = XMLWrites[Guarantee] {
    guarantee =>
      <GUAGUA>
        <GuaTypGUA1>{guarantee.guaranteeType}</GuaTypGUA1>
        {
        guarantee.guaranteeReference.flatMap(
          x => guaranteeReference(x)
        )
      }
      </GUAGUA>
  }

  private def guaranteeReference(guarantee: GuaranteeReference): NodeSeq = guarantee match {
    case guaranteeReferenceWithGrn: GuaranteeReferenceWithGrn     => guaranteeReferenceWithGrn.toXml
    case guaranteeReferenceWithOther: GuaranteeReferenceWithOther => guaranteeReferenceWithOther.toXml
    case _                                                        => NodeSeq.Empty
  }
}
