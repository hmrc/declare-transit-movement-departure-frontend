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

import com.lucidchart.open.xtract.XmlReader
import generators.MessagesModelGenerators
import models.messages.safetyAndSecurity._
import models.messages.trader._
import org.scalacheck.Arbitrary._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import xml.XMLWrites._

import scala.xml.Utility.trim
import scala.xml.{Node, NodeSeq}

class DeclarationRequestSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  import DeclarationRequestSpec._

  "DeclarationRequest" - {

    "must serialise DeclarationRequest to xml" in {
      forAll(arbitrary[DeclarationRequest]) {
        declarationRequest =>
          val expectedResult: Node =
            <CC015B>
              {declarationRequest.meta.toXml}
              {declarationRequest.header.toXml}
              {traderPrinciple(declarationRequest.traderPrincipal)}
              {declarationRequest.traderConsignor.map(_.toXml).getOrElse(NodeSeq.Empty)}
              {declarationRequest.traderConsignee.map(_.toXml).getOrElse(NodeSeq.Empty)}
              {declarationRequest.traderAuthorisedConsignee.map(_.toXml).getOrElse(NodeSeq.Empty)}
              {declarationRequest.customsOfficeDeparture.toXml}
              {declarationRequest.customsOfficeTransit.flatMap(_.toXml)}
              {declarationRequest.customsOfficeDestination.toXml}
              {declarationRequest.controlResult.map(_.toXml).getOrElse(NodeSeq.Empty)}
              {declarationRequest.representative.map(_.toXml).getOrElse(NodeSeq.Empty)}
              {declarationRequest.seals.map(_.toXml).getOrElse(NodeSeq.Empty)}
              {declarationRequest.guarantee.toList.flatMap(_.toXml)}
              {declarationRequest.goodsItems.toList.flatMap(_.toXml)}
              {declarationRequest.itinerary.map(_.toXml)}
              {safetyAndSecurityCarrier(declarationRequest.safetyAndSecurityCarrier)}
              {safetyAndSecurityConsignor(declarationRequest.safetyAndSecurityConsignor)}
              {safetyAndSecurityConsignee(declarationRequest.safetyAndSecurityConsignee)}
            </CC015B>

          declarationRequest.toXml.map(trim) mustBe expectedResult.map(trim)
      }
    }

    "must serialise DeclarationRequest to xml and escape special characters" in {
      forAll(arbitrary[DeclarationRequest]) {
        declarationRequest =>
          val declarationRequestWithSpecial = declarationRequest.copy(
            traderConsignee = Some(
              TraderConsignee("Consignee & Name", "Consignee \" Street", "postal > code", "city <", "GB", None)
            ),
            traderConsignor = Some(
              TraderConsignor("Consignor & Name", "Consignor \" Street", "postal > code", "city <", "GB", None)
            )
          )

          val expectedConsignor =
            "<TRACONCO1>" +
              "<NamCO17>Consignor &amp; Name</NamCO17>" +
              "<StrAndNumCO122>Consignor &quot; Street</StrAndNumCO122>" +
              "<PosCodCO123>postal &gt; code</PosCodCO123>" +
              "<CitCO124>city &lt;</CitCO124>" +
              "<CouCO125>GB</CouCO125>" +
              "</TRACONCO1>"

          val expectedConsignee =
            "<TRACONCE1>" +
              "<NamCE17>Consignee &amp; Name</NamCE17>" +
              "<StrAndNumCE122>Consignee &quot; Street</StrAndNumCE122>" +
              "<PosCodCE123>postal &gt; code</PosCodCE123>" +
              "<CitCE124>city &lt;</CitCE124>" +
              "<CouCE125>GB</CouCE125>" +
              "</TRACONCE1>"

          (declarationRequestWithSpecial.toXml \ "TRACONCO1").map(trim).toString mustBe expectedConsignor
          (declarationRequestWithSpecial.toXml \ "TRACONCE1").map(trim).toString mustBe expectedConsignee
      }
    }

    "must de-serialise xml to DeclarationRequest" in {

      forAll(arbitrary[DeclarationRequest]) {
        declarationRequest =>
          val result = XmlReader.of[DeclarationRequest].read(declarationRequest.toXml)
          result.toOption.value mustBe declarationRequest
      }
    }
  }
}

object DeclarationRequestSpec {

  def traderPrinciple(traderPrincipal: TraderPrincipal): NodeSeq = traderPrincipal match {
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
    case Some(safetyAndSecurityConsignorWithEori: SafetyAndSecurityConsignorWithEori)       => safetyAndSecurityConsignorWithEori.toXml
    case Some(safetyAndSecurityConsignorWithoutEori: SafetyAndSecurityConsignorWithoutEori) => safetyAndSecurityConsignorWithoutEori.toXml
    case _                                                                                  => NodeSeq.Empty
  }
}
