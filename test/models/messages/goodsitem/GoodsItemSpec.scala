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

import com.lucidchart.open.xtract.XmlReader
import generators.MessagesModelGenerators
import xml.XMLWrites._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.xml.NodeSeq

class GoodsItemSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with MessagesModelGenerators with StreamlinedXmlEquality with OptionValues {

  "GoodsItemSpec" - {
    //format off
    "must serialize GoodsItem to xml" in {

      forAll(arbitrary[GoodsItem]) {
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

          val traderConsignorGoodsItem = goodsItem.traderConsignorGoodsItem.fold(NodeSeq.Empty)(
            value => value.toXml
          )
          val traderConsigneeGoodsItem = goodsItem.traderConsigneeGoodsItem.fold(NodeSeq.Empty)(
            value => value.toXml
          )
          val containers = goodsItem.containers.toList.map {
            x =>
              <CONNR2><ConNumNR21>{x}</ConNumNR21></CONNR2>
          }
          val packages = goodsItem.packages.flatMap(
            value => packageNode(value)
          )

          val sensitiveGoodsInformation = goodsItem.sensitiveGoodsInformation.flatMap(_.toXml)

          val expectedResult =
            <GOOITEGDS>
              <IteNumGDS7>{goodsItem.itemNumber}</IteNumGDS7>
              {commodityCode}
              {declarationType}
              <GooDesGDS23>{goodsItem.description}</GooDesGDS23>
              <GooDesGDS23LNG>EN</GooDesGDS23LNG>
              {grossMass}
              {netMass}
              {countryOfDispatch}
              {countryOfDestination}
              {metOfPayGDI12}
              {comRefNumGIM1}
              {UNDanGooCodGDI1}
              {
              goodsItem.previousAdministrativeReferences.flatMap(
                value => value.toXml
              )
            }
              {
              goodsItem.producedDocuments.flatMap(
                value => value.toXml
              )
            }
              {
              goodsItem.specialMention.flatMap(
                value => specialMention(value)
              )
            }
              {traderConsignorGoodsItem}
              {traderConsigneeGoodsItem}
              {containers}
              {packages}
              {sensitiveGoodsInformation}
            </GOOITEGDS>

          goodsItem.toXml mustEqual expectedResult
      }
    }

    "must deserialize GoodsItem from xml" in {
      forAll(arbitrary[GoodsItem]) {
        data =>
          val xml    = data.toXml
          val result = XmlReader.of[GoodsItem].read(xml).toOption.value
          result mustBe data
      }
    }
    //format on
  }

  def specialMention(specialMention: SpecialMention): NodeSeq = specialMention match {
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
