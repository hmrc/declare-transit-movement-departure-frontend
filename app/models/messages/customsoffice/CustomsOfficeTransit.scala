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

package models.messages.customsoffice

import cats.syntax.all._
import com.lucidchart.open.xtract.{XmlReader, __}
import models.XMLReads._
import utils.Format
import xml.XMLWrites

import java.time.LocalDateTime
import scala.xml.NodeSeq

case class CustomsOfficeTransit(referenceNumber: String, arrivalTime: Option[LocalDateTime])

object CustomsOfficeTransit {

  implicit val xmlReader: XmlReader[CustomsOfficeTransit] = (
    (__ \ "RefNumRNS1").read[String],
    (__ \ "ArrTimTRACUS085").read[LocalDateTime].optional
  ).mapN(apply)

  implicit def writes: XMLWrites[CustomsOfficeTransit] = XMLWrites[CustomsOfficeTransit] {
    customsOffice =>
      val arrivalTime = customsOffice.arrivalTime.map {
        arrivalTime =>
          <ArrTimTRACUS085>{Format.dateTimeFormattedIE015(arrivalTime)}</ArrTimTRACUS085>
      }

      <CUSOFFTRARNS>
        <RefNumRNS1>{customsOffice.referenceNumber}</RefNumRNS1>
        {arrivalTime.getOrElse(NodeSeq.Empty)}
      </CUSOFFTRARNS>
  }
}
