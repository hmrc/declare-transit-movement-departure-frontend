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

package models.messages.header

import cats.syntax.all._
import com.lucidchart.open.xtract.{__, XmlReader}
import models.LanguageCodeEnglish
import models.XMLReads._
import utils.Format
import xml.XMLWrites
import xml.XMLWrites._

import java.time.LocalDate
import scala.xml.NodeSeq

case class Header(
  refNumHEA4: String,
  typOfDecHEA24: String,
  couOfDesCodHEA30: Option[String],
  agrLocOfGooCodHEA38: Option[String],
  agrLocOfGooHEA39: Option[String],
  autLocOfGooCodHEA41: Option[String],
  plaOfLoaCodHEA46: Option[String],
  couOfDisCodHEA55: Option[String],
  cusSubPlaHEA66: Option[String],
  transportDetails: Transport,
  conIndHEA96: Int, //TODO: If user specifies they're using a container, this is set to 1 (Containerised indicator)
  totNumOfIteHEA305: Int,
  totNumOfPacHEA306: Int,
  totGroMasHEA307: String,
  decDatHEA383: LocalDate,
  decPlaHEA394: String,
  speCirIndHEA1: Option[String],
  traChaMetOfPayHEA1: Option[String],
  comRefNumHEA: Option[String],
  secHEA358: Option[Int],
  conRefNumHEA: Option[String],
  codPlUnHEA357: Option[String]
)

object Header {

  object Constants {
    val typeOfDeclarationLength             = 9
    val countryLength                       = 2
    val agreedLocationOfGoodsCodeLength     = 17
    val authorisedLocationOfGoodsCodeLength = 17
    val agreedLocationOfGoodsLength         = 35
    val declarationPlace                    = 35
    val identityMeansOfTransport            = 27
    val placeOfLoadingGoodsCodeLength       = 17
    val customsSubPlaceLength               = 17

    val specificCircumstanceIndicatorLength = 1
    val methodOfPaymentLength               = 1
    val commercialReferenceNumberLength     = 70
    val conveyanceReferenceNumberLength     = 35
    val placeOfUnloadingCodeLength          = 35
  }

  // scalastyle:off
  implicit def writes: XMLWrites[Header] = XMLWrites[Header] {
    header =>
      <HEAHEA>
        <RefNumHEA4>{header.refNumHEA4}</RefNumHEA4>
        <TypOfDecHEA24>{header.typOfDecHEA24}</TypOfDecHEA24>
        {
        header.couOfDesCodHEA30.fold(NodeSeq.Empty)(
          value => <CouOfDesCodHEA30>{value}</CouOfDesCodHEA30>
        ) ++
          header.agrLocOfGooCodHEA38.fold(NodeSeq.Empty)(
            value => <AgrLocOfGooCodHEA38>{value}</AgrLocOfGooCodHEA38>
          ) ++
          header.agrLocOfGooHEA39.fold(NodeSeq.Empty)(
            value => <AgrLocOfGooHEA39>{value}</AgrLocOfGooHEA39>
          ) ++
          header.autLocOfGooCodHEA41.fold(NodeSeq.Empty)(
            value => <AutLocOfGooCodHEA41>{value}</AutLocOfGooCodHEA41>
          ) ++
          header.plaOfLoaCodHEA46.fold(NodeSeq.Empty)(
            value => <PlaOfLoaCodHEA46>{value}</PlaOfLoaCodHEA46>
          ) ++
          header.couOfDisCodHEA55.fold(NodeSeq.Empty)(
            value => <CouOfDisCodHEA55>{value}</CouOfDisCodHEA55>
          ) ++
          header.cusSubPlaHEA66.fold(NodeSeq.Empty)(
            value => <CusSubPlaHEA66>{value}</CusSubPlaHEA66>
          ) ++ header.transportDetails.toXml
      }
        <ConIndHEA96>{header.conIndHEA96.toString}</ConIndHEA96>
        <DiaLanIndAtDepHEA254>{LanguageCodeEnglish.code}</DiaLanIndAtDepHEA254>
        <NCTSAccDocHEA601LNG>{LanguageCodeEnglish.code}</NCTSAccDocHEA601LNG>
        <TotNumOfIteHEA305>{header.totNumOfIteHEA305.toString}</TotNumOfIteHEA305>
        <TotNumOfPacHEA306>{header.totNumOfPacHEA306.toString}</TotNumOfPacHEA306>
        <TotGroMasHEA307>{header.totGroMasHEA307.toString}</TotGroMasHEA307>
        <DecDatHEA383>{Format.dateFormatted(header.decDatHEA383)}</DecDatHEA383>
        <DecPlaHEA394>{header.decPlaHEA394}</DecPlaHEA394>
        {
        header.speCirIndHEA1.fold(NodeSeq.Empty)(
          value => <SpeCirIndHEA1>{value}</SpeCirIndHEA1>
        ) ++
          header.traChaMetOfPayHEA1.fold(NodeSeq.Empty)(
            value => <TraChaMetOfPayHEA1>{value}</TraChaMetOfPayHEA1>
          ) ++
          header.comRefNumHEA.fold(NodeSeq.Empty)(
            value => <ComRefNumHEA>{value}</ComRefNumHEA>
          ) ++
          header.secHEA358.fold(NodeSeq.Empty)(
            value => <SecHEA358>{value.toString}</SecHEA358>
          ) ++
          header.conRefNumHEA.fold(NodeSeq.Empty)(
            value => <ConRefNumHEA>{value}</ConRefNumHEA>
          ) ++
          header.codPlUnHEA357.fold(NodeSeq.Empty)(
            value => <CodPlUnHEA357>{value}</CodPlUnHEA357>
          )
      }
      </HEAHEA>
  }
  // scalastyle:on

  implicit val reads: XmlReader[Header] = (
    (__ \ "RefNumHEA4").read[String],
    (__ \ "TypOfDecHEA24").read[String],
    (__ \ "CouOfDesCodHEA30").read[String].optional,
    (__ \ "AgrLocOfGooCodHEA38").read[String].optional,
    (__ \ "AgrLocOfGooHEA39").read[String].optional,
    (__ \ "AutLocOfGooCodHEA41").read[String].optional,
    (__ \ "PlaOfLoaCodHEA46").read[String].optional,
    (__ \ "CouOfDisCodHEA55").read[String].optional,
    (__ \ "CusSubPlaHEA66").read[String].optional,
    __.read[Transport],
    (__ \ "ConIndHEA96").read[Int],
    (__ \ "TotNumOfIteHEA305").read[Int],
    (__ \ "TotNumOfPacHEA306").read[Int],
    (__ \ "TotGroMasHEA307").read[String],
    (__ \ "DecDatHEA383").read[LocalDate],
    (__ \ "DecPlaHEA394").read[String],
    (__ \ "SpeCirIndHEA1").read[String].optional,
    (__ \ "TraChaMetOfPayHEA1").read[String].optional,
    (__ \ "ComRefNumHEA").read[String].optional,
    (__ \ "SecHEA358").read[Int].optional,
    (__ \ "ConRefNumHEA").read[String].optional,
    (__ \ "CodPlUnHEA357").read[String].optional
  ).mapN(apply)
}
