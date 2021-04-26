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
import com.lucidchart.open.xtract.XmlReader.{seq, strictReadSeq}
import com.lucidchart.open.xtract.{__, XmlReader}
import models.LanguageCodeEnglish
import utils.BigDecimalXMLReader._
import xml.XMLWrites
import xml.XMLWrites._

import scala.xml.NodeSeq

final case class GoodsItem(
  itemNumber: Int,
  commodityCode: Option[String],
  declarationType: Option[String],
  description: String,
  grossMass: Option[BigDecimal],
  netMass: Option[BigDecimal],
  countryOfDispatch: Option[String],
  countryOfDestination: Option[String],
  methodOfPayment: Option[String],
  commercialReferenceNumber: Option[String],
  dangerousGoodsCode: Option[String],
  previousAdministrativeReferences: Seq[PreviousAdministrativeReference],
  producedDocuments: Seq[ProducedDocument],
  specialMention: Seq[SpecialMention],
  traderConsignorGoodsItem: Option[TraderConsignorGoodsItem],
  traderConsigneeGoodsItem: Option[TraderConsigneeGoodsItem],
  containers: Seq[String],
  packages: Seq[Package],
  sensitiveGoodsInformation: Seq[SensitiveGoodsInformation],
  goodsItemSecurityConsignor: Option[GoodsItemSecurityConsignor],
  goodsItemSecurityConsignee: Option[GoodsItemSecurityConsignee]
)

object GoodsItem {

  object Constants {
    val commodityCodeLength      = 22
    val typeOfDeclarationLength  = 9
    val descriptionLength        = 280
    val countryLength            = 2
    val itemCount                = 999
    val dangerousGoodsCodeLength = 4

  }

  implicit val xmlReader: XmlReader[GoodsItem] = ((__ \ "IteNumGDS7").read[Int],
                                                  (__ \ "ComCodTarCodGDS10").read[String].optional,
                                                  (__ \ "DecTypGDS15").read[String].optional,
                                                  (__ \ "GooDesGDS23").read[String],
                                                  (__ \ "GroMasGDS46").read[BigDecimal].optional,
                                                  (__ \ "NetMasGDS48").read[BigDecimal].optional,
                                                  (__ \ "CouOfDisGDS58").read[String].optional,
                                                  (__ \ "CouOfDesGDS59").read[String].optional,
                                                  (__ \ "MetOfPayGDI12").read[String].optional,
                                                  (__ \ "ComRefNumGIM1").read[String].optional,
                                                  (__ \ "UNDanGooCodGDI1").read[String].optional,
                                                  (__ \ "PREADMREFAR2").read(strictReadSeq[PreviousAdministrativeReference]),
                                                  (__ \ "PRODOCDC2").read(strictReadSeq[ProducedDocument]),
                                                  (__ \ "SPEMENMT2").read(strictReadSeq[SpecialMention]),
                                                  (__ \ "TRACONCO2").read[TraderConsignorGoodsItem].optional,
                                                  (__ \ "TRACONCE2").read[TraderConsigneeGoodsItem].optional,
                                                  (__ \ "CONNR2" \ "ConNumNR21").read(seq[String]),
                                                  (__ \ "PACGS2").read(strictReadSeq[Package]),
                                                  (__ \ "SGICODSD2").read(strictReadSeq[SensitiveGoodsInformation]),
                                                  (__ \ "TRACORSECGOO021").read[GoodsItemSecurityConsignor].optional,
                                                  (__ \ "TRACONSECGOO013").read[GoodsItemSecurityConsignee].optional).mapN(apply)

  implicit def writes: XMLWrites[GoodsItem] = XMLWrites[GoodsItem] {
    goodsItem =>
      val commodityCode = goodsItem.commodityCode.fold(NodeSeq.Empty)(
        value => <ComCodTarCodGDS10>{value}</ComCodTarCodGDS10>
      )
      val declarationType = goodsItem.declarationType.fold(NodeSeq.Empty)(
        value => <DecTypGDS15>{value}</DecTypGDS15>
      )
      val grossMass = goodsItem.grossMass.fold(NodeSeq.Empty)(
        value => <GroMasGDS46>{value}</GroMasGDS46>
      )
      val netMass = goodsItem.netMass.fold(NodeSeq.Empty)(
        value => <NetMasGDS48>{value}</NetMasGDS48>
      )
      val countryOfDispatch = goodsItem.countryOfDispatch.fold(NodeSeq.Empty)(
        value => <CouOfDisGDS58>{value}</CouOfDisGDS58>
      )
      val countryOfDestination = goodsItem.countryOfDestination.fold(NodeSeq.Empty)(
        value => <CouOfDesGDS59>{value}</CouOfDesGDS59>
      )
      val metOfPayGDI12 = goodsItem.methodOfPayment.fold(NodeSeq.Empty)(
        value => <MetOfPayGDI12>{value}</MetOfPayGDI12>
      )
      val comRefNumGIM1 = goodsItem.commercialReferenceNumber.fold(NodeSeq.Empty)(
        value => <ComRefNumGIM1>{value}</ComRefNumGIM1>
      )
      val UNDanGooCodGDI1 = goodsItem.dangerousGoodsCode.fold(NodeSeq.Empty)(
        value => <UNDanGooCodGDI1>{value}</UNDanGooCodGDI1>
      )
      val previousAdministrativeReference = goodsItem.previousAdministrativeReferences.flatMap(_.toXml)
      val producedDocuments               = goodsItem.producedDocuments.flatMap(_.toXml)
      val specialMentions                 = goodsItem.specialMention.flatMap(specialMentionNode)
      val traderConsignorGoodsItem        = goodsItem.traderConsignorGoodsItem.fold(NodeSeq.Empty)(_.toXml)
      val traderConsigneeGoodsItem        = goodsItem.traderConsigneeGoodsItem.fold(NodeSeq.Empty)(_.toXml)

      val containers = goodsItem.containers.toList.map(
        x => <CONNR2><ConNumNR21>{x}</ConNumNR21></CONNR2>
      )

      val packages = goodsItem.packages.flatMap(packageNode)

      val sensitiveGoodsInformation = goodsItem.sensitiveGoodsInformation.flatMap(_.toXml)

      <GOOITEGDS>
        <IteNumGDS7>{goodsItem.itemNumber}</IteNumGDS7>
        {commodityCode}
        {declarationType}
        <GooDesGDS23>{goodsItem.description}</GooDesGDS23>
        <GooDesGDS23LNG>{LanguageCodeEnglish.code}</GooDesGDS23LNG>
        {grossMass}
        {netMass}
        {countryOfDispatch}
        {countryOfDestination}
        {metOfPayGDI12}
        {comRefNumGIM1}
        {UNDanGooCodGDI1}
        {previousAdministrativeReference}
        {producedDocuments}
        {specialMentions}
        {traderConsignorGoodsItem}
        {traderConsigneeGoodsItem}
        {containers}
        {packages}
        {sensitiveGoodsInformation}
      </GOOITEGDS>
  }

  def specialMentionNode(specialMention: SpecialMention): NodeSeq = specialMention match {
    case specialMention: SpecialMentionGuaranteeLiabilityAmount => specialMention.toXml
    case specialMention: SpecialMentionExportFromGB             => specialMention.toXml
    case specialMention: SpecialMentionExportFromNI             => specialMention.toXml
    case specialMention: SpecialMentionNoCountry                => specialMention.toXml
    case _                                                      => NodeSeq.Empty
  }

  def packageNode(packageType: Package): NodeSeq = packageType match {
    case packageItem: UnpackedPackage => packageItem.toXml
    case packageItem: RegularPackage  => packageItem.toXml
    case packageItem: BulkPackage     => packageItem.toXml
    case _                            => NodeSeq.Empty
  }

}
