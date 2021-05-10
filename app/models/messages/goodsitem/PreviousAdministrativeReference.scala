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

import cats.syntax.all._
import com.lucidchart.open.xtract.{__, XmlReader}
import models.LanguageCodeEnglish
import xml.XMLWrites

import scala.xml.NodeSeq

final case class PreviousAdministrativeReference(
  preDocTypAR21: String, //an6 CL014 ref data: Previous document type (Common)
  preDocRefAR26: String,
  comOfInfAR29: Option[String]
)

object PreviousAdministrativeReference {

  object Constants {
    val previousDocumentTypeLength           = 6
    val previousDocumentReferenceLength      = 35
    val complementOfInformationLength        = 26
    val previousAdministrativeReferenceCount = 9
  }

  implicit val xmlReader: XmlReader[PreviousAdministrativeReference] =
    ((__ \ "PreDocTypAR21").read[String], (__ \ "PreDocRefAR26").read[String], (__ \ "ComOfInfAR29").read[String].optional).mapN(apply)

  implicit def writes: XMLWrites[PreviousAdministrativeReference] = XMLWrites[PreviousAdministrativeReference] {
    references =>
      val comOfInfAR29 = references.comOfInfAR29.fold(NodeSeq.Empty)(
        value => <ComOfInfAR29>{value}</ComOfInfAR29>
      )

      <PREADMREFAR2>
        <PreDocTypAR21>{references.preDocTypAR21}</PreDocTypAR21>
        <PreDocRefAR26>{references.preDocRefAR26}</PreDocRefAR26>
        {comOfInfAR29}
      </PREADMREFAR2>
  }
}
