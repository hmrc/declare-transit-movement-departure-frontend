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

package models.messages

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader.seq
import com.lucidchart.open.xtract.{__, XmlReader}
import models.LanguageCodeEnglish
import xml.XMLWrites

case class Seals(numberOfSeals: Int, SealId: Seq[String])

object Seals {

  object Constants {
    val maxSeals     = 9999
    val sealIdLength = 20
  }

  implicit val xmlReader: XmlReader[Seals] = (
    (__ \ "SeaNumSLI2").read[Int],
    (__ \ "SEAIDSID" \ "SeaIdeSID1").read(seq[String])
  ).mapN(apply)

  implicit def writes: XMLWrites[Seals] = XMLWrites[Seals] {
    seals =>
      <SEAINFSLI>
        <SeaNumSLI2>{seals.numberOfSeals}</SeaNumSLI2>
        {
        seals.SealId.map {
          id =>
            <SEAIDSID>
                <SeaIdeSID1>{id}</SeaIdeSID1>
                <SeaIdeSID1LNG>{LanguageCodeEnglish.code}</SeaIdeSID1LNG>
              </SEAIDSID>
        }
      }
      </SEAINFSLI>
  }

}
