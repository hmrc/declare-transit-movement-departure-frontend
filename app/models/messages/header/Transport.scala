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

package models.messages.header

import cats.syntax.all._
import com.lucidchart.open.xtract.{__, XmlReader}
import models.messages.escapeXml
import models.{LanguageCodeEnglish, XMLWrites}

import scala.xml.NodeSeq

case class Transport(
  inlTraModHEA75: Option[Int],
  traModAtBorHEA76: Option[String],
  ideOfMeaOfTraAtDHEA78: Option[String],
  natOfMeaOfTraAtDHEA80: Option[String],
  ideOfMeaOfTraCroHEA85: Option[String],
  natOfMeaOfTraCroHEA87: Option[String],
  typOfMeaOfTraCroHEA88: Option[Int]
)

object Transport {

  object Constants {
    val identityMeansOfTransport = 27
  }

  // scalastyle:off
  implicit def writes: XMLWrites[Transport] = XMLWrites[Transport] {
    transport =>
      {
        transport.inlTraModHEA75.fold(NodeSeq.Empty)(value => <InlTraModHEA75>{value.toString}</InlTraModHEA75>) ++
          transport.traModAtBorHEA76.fold(NodeSeq.Empty)(value => <TraModAtBorHEA76>{value.toString}</TraModAtBorHEA76>) ++
          transport.ideOfMeaOfTraAtDHEA78.fold(NodeSeq.Empty)(value => <IdeOfMeaOfTraAtDHEA78>{escapeXml(value)}</IdeOfMeaOfTraAtDHEA78>
          <IdeOfMeaOfTraAtDHEA78LNG>{LanguageCodeEnglish.code}</IdeOfMeaOfTraAtDHEA78LNG>) ++
          transport.natOfMeaOfTraAtDHEA80.fold(NodeSeq.Empty)(value => <NatOfMeaOfTraAtDHEA80>{escapeXml(value)}</NatOfMeaOfTraAtDHEA80>) ++
          transport.ideOfMeaOfTraCroHEA85.fold(NodeSeq.Empty)(value => <IdeOfMeaOfTraCroHEA85>{escapeXml(value)}</IdeOfMeaOfTraCroHEA85>
          <IdeOfMeaOfTraCroHEA85LNG>{LanguageCodeEnglish.code}</IdeOfMeaOfTraCroHEA85LNG>) ++
          transport.natOfMeaOfTraCroHEA87.fold(NodeSeq.Empty)(value => <NatOfMeaOfTraCroHEA87>{escapeXml(value)}</NatOfMeaOfTraCroHEA87>) ++
          transport.typOfMeaOfTraCroHEA88.fold(NodeSeq.Empty)(value => <TypOfMeaOfTraCroHEA88>{value.toString}</TypOfMeaOfTraCroHEA88>)
      }
  }
  // scalastyle:on

  implicit val reads: XmlReader[Transport] = (
    (__ \\ "InlTraModHEA75").read[Int].optional,
    (__ \\ "TraModAtBorHEA76").read[String].optional,
    (__ \\ "IdeOfMeaOfTraAtDHEA78").read[String].optional,
    (__ \\ "NatOfMeaOfTraAtDHEA80").read[String].optional,
    (__ \\ "IdeOfMeaOfTraCroHEA85").read[String].optional,
    (__ \\ "NatOfMeaOfTraCroHEA87").read[String].optional,
    (__ \\ "TypOfMeaOfTraCroHEA88").read[Int].optional
  ).mapN(apply)
}
