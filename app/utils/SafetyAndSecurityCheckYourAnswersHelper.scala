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

package utils

import controllers.safetyAndSecurity.routes
import models.reference.{CountryCode, MethodOfPayment}
import models.{CheckMode, CircumstanceIndicatorList, CommonAddress, CountryList, Index, UserAnswers}
import pages.safetyAndSecurity._
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._
import viewModels.AddAnotherViewModel

// scalastyle:off number.of.methods
class SafetyAndSecurityCheckYourAnswersHelper(userAnswers: UserAnswers) extends CheckYourAnswersHelper(userAnswers) {

  def addCarrierEori: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddCarrierEoriPage,
    formatAnswer = yesOrNo,
    prefix = "addCarrierEori",
    id = None,
    call = routes.AddCarrierEoriController.onPageLoad(lrn, CheckMode)
  )

  def addCarrier: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddCarrierPage,
    formatAnswer = yesOrNo,
    prefix = "addCarrier",
    id = None,
    call = routes.AddCarrierController.onPageLoad(lrn, CheckMode)
  )

  def addCircumstanceIndicator: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddCircumstanceIndicatorPage,
    formatAnswer = yesOrNo,
    prefix = "addCircumstanceIndicator",
    id = None,
    call = routes.AddCircumstanceIndicatorController.onPageLoad(lrn, CheckMode)
  )

  def addCommercialReferenceNumberAllItems: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddCommercialReferenceNumberAllItemsPage,
    formatAnswer = yesOrNo,
    prefix = "addCommercialReferenceNumberAllItems",
    id = None,
    call = routes.AddCommercialReferenceNumberAllItemsController.onPageLoad(lrn, CheckMode)
  )

  def addCommercialReferenceNumber: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddCommercialReferenceNumberPage,
    formatAnswer = yesOrNo,
    prefix = "addCommercialReferenceNumber",
    id = None,
    call = routes.AddCommercialReferenceNumberController.onPageLoad(lrn, CheckMode)
  )

  def addConveyanceReferenceNumber: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddConveyanceReferenceNumberPage,
    formatAnswer = yesOrNo,
    prefix = "addConveyancerReferenceNumber",
    id = None,
    call = routes.AddConveyanceReferenceNumberController.onPageLoad(lrn, CheckMode)
  )

  def addPlaceOfUnloadingCode: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddPlaceOfUnloadingCodePage,
    formatAnswer = yesOrNo,
    prefix = "addPlaceOfUnloadingCode",
    id = None,
    call = routes.AddPlaceOfUnloadingCodeController.onPageLoad(lrn, CheckMode)
  )

  def addSafetyAndSecurityConsigneeEori: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddSafetyAndSecurityConsigneeEoriPage,
    formatAnswer = yesOrNo,
    prefix = "addSafetyAndSecurityConsigneeEori",
    id = None,
    call = routes.AddSafetyAndSecurityConsigneeEoriController.onPageLoad(lrn, CheckMode)
  )

  def addSafetyAndSecurityConsignee: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddSafetyAndSecurityConsigneePage,
    formatAnswer = yesOrNo,
    prefix = "addSafetyAndSecurityConsignee",
    id = None,
    call = routes.AddSafetyAndSecurityConsigneeController.onPageLoad(lrn, CheckMode)
  )

  def addSafetyAndSecurityConsignorEori: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddSafetyAndSecurityConsignorEoriPage,
    formatAnswer = yesOrNo,
    prefix = "addSafetyAndSecurityConsignorEori",
    id = None,
    call = routes.AddSafetyAndSecurityConsignorEoriController.onPageLoad(lrn, CheckMode)
  )

  def addSafetyAndSecurityConsignor: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddSafetyAndSecurityConsignorPage,
    formatAnswer = yesOrNo,
    prefix = "addSafetyAndSecurityConsignor",
    id = None,
    call = routes.AddSafetyAndSecurityConsignorController.onPageLoad(lrn, CheckMode)
  )

  def addTransportChargesPaymentMethod: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddTransportChargesPaymentMethodPage,
    formatAnswer = yesOrNo,
    prefix = "addTransportChargesPaymentMethod",
    id = None,
    call = routes.AddTransportChargesPaymentMethodController.onPageLoad(lrn, CheckMode)
  )

  def carrierAddress: Option[Row] = getAnswerAndBuildRow[CommonAddress](
    page = CarrierAddressPage,
    formatAnswer = address,
    prefix = "carrierAddress",
    id = None,
    call = routes.CarrierAddressController.onPageLoad(lrn, CheckMode)
  )

  def carrierEori: Option[Row] = getAnswerAndBuildRow[String](
    page = CarrierEoriPage,
    formatAnswer = x => lit"$x",
    prefix = "carrierEori",
    id = None,
    call = routes.CarrierEoriController.onPageLoad(lrn, CheckMode)
  )

  def carrierName: Option[Row] = getAnswerAndBuildRow[String](
    page = CarrierNamePage,
    formatAnswer = x => lit"$x",
    prefix = "carrierName",
    id = None,
    call = routes.CarrierNameController.onPageLoad(lrn, CheckMode)
  )

  def circumstanceIndicator(circumstanceIndicators: CircumstanceIndicatorList): Option[Row] = getAnswerAndBuildRow[String](
    page = CircumstanceIndicatorPage,
    formatAnswer = code =>
      lit"${circumstanceIndicators
        .getCircumstanceIndicator(code)
        .map(
          x => s"(${x.code}) ${x.description}"
        )
        .getOrElse(code)}",
    prefix = "circumstanceIndicator",
    id = None,
    call = routes.CircumstanceIndicatorController.onPageLoad(lrn, CheckMode)
  )

  def commercialReferenceNumberAllItems: Option[Row] = getAnswerAndBuildRow[String](
    page = CommercialReferenceNumberAllItemsPage,
    formatAnswer = x => lit"$x",
    prefix = "commercialReferenceNumberAllItems",
    id = None,
    call = routes.CommercialReferenceNumberAllItemsController.onPageLoad(lrn, CheckMode)
  )

  def conveyanceReferenceNumber: Option[Row] = getAnswerAndBuildRow[String](
    page = ConveyanceReferenceNumberPage,
    formatAnswer = x => lit"$x",
    prefix = "conveyanceReferenceNumber",
    id = None,
    call = routes.ConveyanceReferenceNumberController.onPageLoad(lrn, CheckMode)
  )

  def placeOfUnloadingCode: Option[Row] = getAnswerAndBuildRow[String](
    page = PlaceOfUnloadingCodePage,
    formatAnswer = x => lit"$x",
    prefix = "placeOfUnloadingCode",
    id = None,
    call = routes.PlaceOfUnloadingCodeController.onPageLoad(lrn, CheckMode)
  )

  def safetyAndSecurityConsigneeAddress: Option[Row] = getAnswerAndBuildRow[CommonAddress](
    page = SafetyAndSecurityConsigneeAddressPage,
    formatAnswer = address,
    prefix = "safetyAndSecurityConsigneeAddress",
    id = None,
    call = routes.SafetyAndSecurityConsigneeAddressController.onPageLoad(lrn, CheckMode)
  )

  def safetyAndSecurityConsigneeEori: Option[Row] = getAnswerAndBuildRow[String](
    page = SafetyAndSecurityConsigneeEoriPage,
    formatAnswer = x => lit"$x",
    prefix = "safetyAndSecurityConsigneeEori",
    id = None,
    call = routes.SafetyAndSecurityConsigneeEoriController.onPageLoad(lrn, CheckMode)
  )

  def safetyAndSecurityConsigneeName: Option[Row] = getAnswerAndBuildRow[String](
    page = SafetyAndSecurityConsigneeNamePage,
    formatAnswer = x => lit"$x",
    prefix = "safetyAndSecurityConsigneeName",
    id = None,
    call = routes.SafetyAndSecurityConsigneeNameController.onPageLoad(lrn, CheckMode)
  )

  def safetyAndSecurityConsignorAddress: Option[Row] = getAnswerAndBuildRow[CommonAddress](
    page = SafetyAndSecurityConsignorAddressPage,
    formatAnswer = address,
    prefix = "safetyAndSecurityConsignorAddress",
    id = None,
    call = routes.SafetyAndSecurityConsignorAddressController.onPageLoad(lrn, CheckMode)
  )

  def safetyAndSecurityConsignorEori: Option[Row] = getAnswerAndBuildRow[String](
    page = SafetyAndSecurityConsignorEoriPage,
    formatAnswer = x => lit"$x",
    prefix = "safetyAndSecurityConsignorEori",
    id = None,
    call = routes.SafetyAndSecurityConsignorEoriController.onPageLoad(lrn, CheckMode)
  )

  def safetyAndSecurityConsignorName: Option[Row] = getAnswerAndBuildRow[String](
    page = SafetyAndSecurityConsignorNamePage,
    formatAnswer = x => lit"$x",
    prefix = "safetyAndSecurityConsignorName",
    id = None,
    call = routes.SafetyAndSecurityConsignorNameController.onPageLoad(lrn, CheckMode)
  )

  def transportChargesPaymentMethod(): Option[Row] = getAnswerAndBuildRow[MethodOfPayment](
    page = TransportChargesPaymentMethodPage,
    formatAnswer = x => lit"$x",
    prefix = "transportChargesPaymentMethod",
    id = None,
    call = routes.TransportChargesPaymentMethodController.onPageLoad(lrn, CheckMode)
  )

  def countryRow(index: Index, countries: CountryList): Option[Row] = getAnswerAndBuildCountryRow[CountryCode](
    getCountryCode = x => x,
    countryList = countries,
    getAnswerAndBuildRow = f =>
      getAnswerAndBuildRemovableRow(
        page = CountryOfRoutingPage(index),
        formatAnswer = f,
        id = s"country-${index.display}",
        changeCall = routes.CountryOfRoutingController.onPageLoad(lrn, index, CheckMode),
        removeCall = routes.ConfirmRemoveCountryController.onPageLoad(lrn, index, CheckMode)
      )
  )

  def countryOfRoutingRow(index: Index, countries: CountryList): Option[Row] = getAnswerAndBuildCountryRow[CountryCode](
    getCountryCode = x => x,
    countryList = countries,
    getAnswerAndBuildRow = f =>
      getAnswerAndBuildValuelessRow(
        page = CountryOfRoutingPage(index),
        formatAnswer = f,
        id = Some(s"change-country-${index.display}"),
        call = routes.AddAnotherCountryOfRoutingController.onPageLoad(lrn, CheckMode)
      )
  )

  def addAnotherCountryOfRouting(content: Text): AddAnotherViewModel = {

    val addAnotherCountryOfRoutingHref = routes.AddAnotherCountryOfRoutingController.onPageLoad(lrn, CheckMode).url

    AddAnotherViewModel(addAnotherCountryOfRoutingHref, content)
  }

}
// scalastyle:on number.of.methods
