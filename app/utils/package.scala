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

import models.reference._
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.viewmodels.{Content, MessageInterpolators}

package object utils {
  val defaultOption: JsObject = Json.obj("value" -> "", "text" -> "")

  def packageTypeList(value: Option[PackageType], packageTypes: Seq[PackageType]): Seq[JsObject] = {
    val packageTypeJson = packageTypes.map {
      packageType =>
        Json.obj("text" -> s"${packageType.description} (${packageType.code})", "value" -> packageType.code, "selected" -> value.contains(packageType))
    }

    defaultOption +: packageTypeJson
  }

  def countryJsonList(value: Option[Country], countries: Seq[Country]): Seq[JsObject] = {
    val countryJsonList = countries.map {
      country =>
        Json.obj("text" -> country.description, "value" -> country.code, "selected" -> value.contains(country))
    }

    defaultOption +: countryJsonList
  }

  def getCustomsOfficesAsJson(value: Option[CustomsOffice], customsOffices: Seq[CustomsOffice]): Seq[JsObject] = {
    val customsOfficeObjects = customsOffices.map {
      office =>
        Json.obj(
          "value"    -> office.id,
          "text"     -> s"${office.name} (${office.id})",
          "selected" -> value.contains(office)
        )
    }
    defaultOption +: customsOfficeObjects
  }

  def transportModesAsJson(value: Option[TransportMode], transportModes: Seq[TransportMode]): Seq[JsObject] = {
    val transportModeObjects = transportModes.map {
      mode =>
        Json.obj(
          "value"    -> mode.code,
          "text"     -> s"(${mode.code}) ${mode.description}",
          "selected" -> value.contains(mode)
        )
    }
    defaultOption +: transportModeObjects
  }

  def amPmAsJson(value: Option[String]): Seq[JsObject] = {
    val amPms = Seq("am", "pm")
    val jsObjects: Seq[JsObject] = amPms map (
      amOrPm =>
        Json.obj(
          "value"    -> s"$amOrPm",
          "text"     -> s"$amOrPm",
          "selected" -> value.contains(amOrPm)
        )
    )

    defaultOption +: jsObjects
  }

  def getOfficeOfTransitAsJson(value: Option[OfficeOfTransit], officeOfTransitList: Seq[OfficeOfTransit]): Seq[JsObject] = {
    val officeOfTransitObjects = officeOfTransitList.map {
      office =>
        Json.obj(
          "value"    -> office.id,
          "text"     -> s"${office.name} (${office.id})",
          "selected" -> value.contains(office)
        )
    }
    defaultOption +: officeOfTransitObjects
  }

  def getPreviousDocumentsAsJson(value: Option[PreviousReferencesDocumentType], documentList: Seq[PreviousReferencesDocumentType]): Seq[JsObject] = {
    val documentObjects = documentList.map {
      documentType =>
        Json.obj(
          "value"    -> documentType.code,
          "text"     -> s"(${documentType.code}) ${documentType.description}",
          "selected" -> value.contains(documentType)
        )
    }
    defaultOption +: documentObjects
  }

  def getSpecialMentionAsJson(value: Option[SpecialMention], documentList: Seq[SpecialMention]): Seq[JsObject] = {
    val list = documentList.map {
      specialMention =>
        Json.obj(
          "value"    -> specialMention.code,
          "text"     -> s"(${specialMention.code}) ${specialMention.description}",
          "selected" -> value.contains(specialMention)
        )
    }
    defaultOption +: list
  }

  def getDocumentsAsJson(value: Option[DocumentType], documentList: Seq[DocumentType]): Seq[JsObject] = {
    val documentObjects = documentList.map {
      documentType =>
        Json.obj(
          "value"    -> documentType.code,
          "text"     -> s"(${documentType.code}) ${documentType.description}",
          "selected" -> value.contains(documentType)
        )
    }
    defaultOption +: documentObjects
  }

  def getDangerousGoodsCodeAsJson(value: Option[DangerousGoodsCode], dangerousGoodsCodeList: Seq[DangerousGoodsCode]): Seq[JsObject] = {
    val dangerousGoodsCodeObjects = dangerousGoodsCodeList.map {
      dangerousGoodsCode =>
        Json.obj(
          "value"    -> dangerousGoodsCode.code,
          "text"     -> s"(${dangerousGoodsCode.code}) ${dangerousGoodsCode.description}",
          "selected" -> value.contains(dangerousGoodsCode)
        )
    }
    defaultOption +: dangerousGoodsCodeObjects
  }

  def getPaymentsAsJson(value: Option[MethodOfPayment], methodOfPaymentList: Seq[MethodOfPayment]): Seq[JsObject] = {
    val paymentObjects = methodOfPaymentList.map {
      methodOfPayment =>
        Json.obj(
          "value"    -> methodOfPayment.code,
          "text"     -> s"(${methodOfPayment.code}) ${methodOfPayment.description}",
          "selected" -> value.contains(methodOfPayment)
        )
    }
    defaultOption +: paymentObjects
  }

  def getCircumstanceIndicatorsAsJson(value: Option[CircumstanceIndicator], circumstanceIndicators: Seq[CircumstanceIndicator]): Seq[JsObject] = {
    val paymentObjects = circumstanceIndicators.map {
      circumstanceIndicator =>
        Json.obj(
          "value"    -> circumstanceIndicator.code,
          "text"     -> s"(${circumstanceIndicator.code}) ${circumstanceIndicator.description}",
          "selected" -> value.contains(circumstanceIndicator)
        )
    }
    defaultOption +: paymentObjects
  }

  def yesOrNo(answer: Boolean): Content =
    if (answer) {
      msg"site.yes"
    } else {
      msg"site.no"
    }

  def yesOrNo(answer: Int): Content =
    if (answer == 1) {
      msg"site.yes"
    } else {
      msg"site.no"
    }

  def acceptedOrRejected(answer: Int): Content =
    if (answer == 1) {
      msg"site.accepted"
    } else {
      msg"site.rejected"
    }

}
