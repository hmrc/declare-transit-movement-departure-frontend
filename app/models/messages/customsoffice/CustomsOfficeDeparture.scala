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

import com.lucidchart.open.xtract.{__, XmlReader}
import xml.XMLWrites

case class CustomsOfficeDeparture(referenceNumber: String)

object CustomsOfficeDeparture {

  implicit val xmlReader: XmlReader[CustomsOfficeDeparture] = (
    (__ \ "RefNumEPT1").read[String]
  ).map(apply)

  implicit def writes: XMLWrites[CustomsOfficeDeparture] = XMLWrites[CustomsOfficeDeparture] {
    customsOffice =>
      <CUSOFFDEPEPT>
        <RefNumEPT1>{customsOffice.referenceNumber}</RefNumEPT1>
      </CUSOFFDEPEPT>
  }
}
