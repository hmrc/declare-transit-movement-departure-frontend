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

import cats.data.NonEmptyList
import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import com.lucidchart.open.xtract.{__, XmlReader}
import models.messages.customsoffice.{CustomsOfficeDeparture, CustomsOfficeDestination, CustomsOfficeTransit}
import models.messages.goodsitem.GoodsItem
import models.messages.guarantee.Guarantee
import models.messages.header.Header
import models.messages.safetyAndSecurity._
import models.messages.trader._
import utils.NonEmptyListXMLReader._
import xml.XMLWrites
import xml.XMLWrites._

import scala.xml.{Elem, Node, NodeSeq}

case class DeclarationRequest(meta: Meta,
                              header: Header,
                              traderPrincipal: TraderPrincipal,
                              traderConsignor: Option[TraderConsignor],
                              traderConsignee: Option[TraderConsignee],
                              traderAuthorisedConsignee: Option[TraderAuthorisedConsignee],
                              customsOfficeDeparture: CustomsOfficeDeparture,
                              customsOfficeTransit: Seq[CustomsOfficeTransit],
                              customsOfficeDestination: CustomsOfficeDestination,
                              controlResult: Option[ControlResult],
                              representative: Option[Representative],
                              seals: Option[Seals],
                              guarantee: NonEmptyList[Guarantee],
                              goodsItems: NonEmptyList[GoodsItem],
                              itinerary: Seq[Itinerary],
                              safetyAndSecurityCarrier: Option[SafetyAndSecurityCarrier],
                              safetyAndSecurityConsignor: Option[SafetyAndSecurityConsignor],
                              safetyAndSecurityConsignee: Option[SafetyAndSecurityConsignee]
)

object DeclarationRequest {

  implicit def writes: XMLWrites[DeclarationRequest] = XMLWrites[DeclarationRequest] {
    declarationRequest =>
      val parentNode: Node = <CC015B></CC015B>

      val childNodes: NodeSeq =
        declarationRequest.meta.toXml ++
          declarationRequest.header.toXml ++
          traderPrinciple(declarationRequest.traderPrincipal) ++
          declarationRequest.traderConsignor.map(_.toXml).getOrElse(NodeSeq.Empty) ++
          declarationRequest.traderConsignee.map(_.toXml).getOrElse(NodeSeq.Empty) ++
          declarationRequest.traderAuthorisedConsignee.map(_.toXml).getOrElse(NodeSeq.Empty) ++
          declarationRequest.customsOfficeDeparture.toXml ++
          declarationRequest.customsOfficeTransit.flatMap(_.toXml) ++
          declarationRequest.customsOfficeDestination.toXml ++
          declarationRequest.controlResult.map(_.toXml).getOrElse(NodeSeq.Empty) ++
          declarationRequest.representative.map(_.toXml).getOrElse(NodeSeq.Empty) ++
          declarationRequest.seals.map(_.toXml).getOrElse(NodeSeq.Empty) ++
          declarationRequest.guarantee.toList.flatMap(_.toXml) ++
          declarationRequest.goodsItems.toList.flatMap(_.toXml) ++
          declarationRequest.itinerary.flatMap(_.toXml) ++
          safetyAndSecurityCarrier(declarationRequest.safetyAndSecurityCarrier) ++
          safetyAndSecurityConsignor(declarationRequest.safetyAndSecurityConsignor) ++
          safetyAndSecurityConsignee(declarationRequest.safetyAndSecurityConsignee)

      Elem(parentNode.prefix, parentNode.label, parentNode.attributes, parentNode.scope, parentNode.child.isEmpty, parentNode.child ++ childNodes: _*)
  }

  private def traderPrinciple(traderPrincipal: TraderPrincipal): NodeSeq = traderPrincipal match {
    case traderPrincipalWithEori: TraderPrincipalWithEori       => traderPrincipalWithEori.toXml
    case traderPrincipalWithoutEori: TraderPrincipalWithoutEori => traderPrincipalWithoutEori.toXml
    case _                                                      => NodeSeq.Empty
  }

  private def safetyAndSecurityCarrier(safetyAndSecurityCarrier: Option[SafetyAndSecurityCarrier]): NodeSeq = safetyAndSecurityCarrier match {
    case Some(safetyAndSecurityCarrierWithEori: SafetyAndSecurityCarrierWithEori)       => safetyAndSecurityCarrierWithEori.toXml
    case Some(safetyAndSecurityCarrierWithoutEori: SafetyAndSecurityCarrierWithoutEori) => safetyAndSecurityCarrierWithoutEori.toXml
    case _                                                                              => NodeSeq.Empty
  }

  private def safetyAndSecurityConsignee(safetyAndSecurityConsignee: Option[SafetyAndSecurityConsignee]): NodeSeq = safetyAndSecurityConsignee match {
    case Some(safetyAndSecurityConsigneeWithEori: SafetyAndSecurityConsigneeWithEori)       => safetyAndSecurityConsigneeWithEori.toXml
    case Some(safetyAndSecurityConsigneeWithoutEori: SafetyAndSecurityConsigneeWithoutEori) => safetyAndSecurityConsigneeWithoutEori.toXml
    case _                                                                                  => NodeSeq.Empty
  }

  private def safetyAndSecurityConsignor(safetyAndSecurityConsignor: Option[SafetyAndSecurityConsignor]): NodeSeq = safetyAndSecurityConsignor match {
    case Some(safetyAndSecurityConsigorWithEori: SafetyAndSecurityConsignorWithEori)       => safetyAndSecurityConsigorWithEori.toXml
    case Some(safetyAndSecurityConsigorWithoutEori: SafetyAndSecurityConsignorWithoutEori) => safetyAndSecurityConsigorWithoutEori.toXml
    case _                                                                                 => NodeSeq.Empty
  }

  implicit val reads: XmlReader[DeclarationRequest] =
    (__.read[Meta],
     (__ \ "HEAHEA").read[Header],
     (__ \ "TRAPRIPC1").read[TraderPrincipal],
     (__ \ "TRACONCO1").read[TraderConsignor].optional,
     (__ \ "TRACONCE1").read[TraderConsignee].optional,
     (__ \ "TRAAUTCONTRA").read[TraderAuthorisedConsignee].optional,
     (__ \ "CUSOFFDEPEPT").read[CustomsOfficeDeparture],
     (__ \ "CUSOFFTRARNS").read(strictReadSeq[CustomsOfficeTransit]),
     (__ \ "CUSOFFDESEST").read[CustomsOfficeDestination],
     (__ \ "CONRESERS").read[ControlResult].optional,
     (__ \ "REPREP").read[Representative].optional,
     (__ \ "SEAINFSLI").read[Seals].optional,
     (__ \ "GUAGUA").read(xmlNonEmptyListReads[Guarantee]),
     (__ \ "GOOITEGDS").read(xmlNonEmptyListReads[GoodsItem]),
     (__ \ "ITI").read(strictReadSeq[Itinerary]),
     (__ \ "CARTRA100").read[SafetyAndSecurityCarrier].optional,
     (__ \ "TRACORSEC037").read[SafetyAndSecurityConsignor].optional,
     (__ \ "TRACONSEC029").read[SafetyAndSecurityConsignee].optional
    ) mapN apply
}
