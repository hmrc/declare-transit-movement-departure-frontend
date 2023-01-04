/*
 * Copyright 2023 HM Revenue & Customs
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

package models.messages

import cats.syntax.all._
import com.lucidchart.open.xtract.{__, XmlReader}
import xml.XMLWrites

import scala.xml.NodeSeq

case class Representative(namREP5: String, repCapREP18: Option[String])

object Representative {

  object Constants {
    val nameLength     = 35
    val capacityLength = 35
  }

  implicit val xmlReader: XmlReader[Representative] =
    ((__ \ "NamREP5").read[String], (__ \ "RepCapREP18").read[String].optional).mapN(apply)

  implicit def writes: XMLWrites[Representative] = XMLWrites[Representative] {
    representative =>
      <REPREP>
        <NamREP5>{representative.namREP5}</NamREP5>
        {
        representative.repCapREP18.fold(NodeSeq.Empty) {
          value => <RepCapREP18>{value}</RepCapREP18>
        }
      }
      </REPREP>
  }
}
