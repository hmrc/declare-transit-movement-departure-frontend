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
    formatAnswer = formatAsYesOrNo,
    prefix = "addCarrierEori",
    id = None,
    call = routes.AddCarrierEoriController.onPageLoad(lrn, CheckMode)
  )

  def addCarrier: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddCarrierPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "addCarrier",
    id = None,
    call = routes.AddCarrierController.onPageLoad(lrn, CheckMode)
  )

  def addCircumstanceIndicator: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddCircumstanceIndicatorPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "addCircumstanceIndicator",
    id = None,
    call = routes.AddCircumstanceIndicatorController.onPageLoad(lrn, CheckMode)
  )

  def addCommercialReferenceNumberAllItems: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddCommercialReferenceNumberAllItemsPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "addCommercialReferenceNumberAllItems",
    id = None,
    call = routes.AddCommercialReferenceNumberAllItemsController.onPageLoad(lrn, CheckMode)
  )

  def addCommercialReferenceNumber: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddCommercialReferenceNumberPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "addCommercialReferenceNumber",
    id = None,
    call = routes.AddCommercialReferenceNumberController.onPageLoad(lrn, CheckMode)
  )

  def addConveyanceReferenceNumber: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddConveyanceReferenceNumberPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "addConveyancerReferenceNumber",
    id = None,
    call = routes.AddConveyanceReferenceNumberController.onPageLoad(lrn, CheckMode)
  )

  def addPlaceOfUnloadingCode: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddPlaceOfUnloadingCodePage,
    formatAnswer = formatAsYesOrNo,
    prefix = "addPlaceOfUnloadingCode",
    id = None,
    call = routes.AddPlaceOfUnloadingCodeController.onPageLoad(lrn, CheckMode)
  )

  def addSafetyAndSecurityConsigneeEori: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddSafetyAndSecurityConsigneeEoriPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "addSafetyAndSecurityConsigneeEori",
    id = None,
    call = routes.AddSafetyAndSecurityConsigneeEoriController.onPageLoad(lrn, CheckMode)
  )

  def addSafetyAndSecurityConsignee: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddSafetyAndSecurityConsigneePage,
    formatAnswer = formatAsYesOrNo,
    prefix = "addSafetyAndSecurityConsignee",
    id = None,
    call = routes.AddSafetyAndSecurityConsigneeController.onPageLoad(lrn, CheckMode)
  )

  def addSafetyAndSecurityConsignorEori: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddSafetyAndSecurityConsignorEoriPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "addSafetyAndSecurityConsignorEori",
    id = None,
    call = routes.AddSafetyAndSecurityConsignorEoriController.onPageLoad(lrn, CheckMode)
  )

  def addSafetyAndSecurityConsignor: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddSafetyAndSecurityConsignorPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "addSafetyAndSecurityConsignor",
    id = None,
    call = routes.AddSafetyAndSecurityConsignorController.onPageLoad(lrn, CheckMode)
  )

  def addTransportChargesPaymentMethod: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddTransportChargesPaymentMethodPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "addTransportChargesPaymentMethod",
    id = None,
    call = routes.AddTransportChargesPaymentMethodController.onPageLoad(lrn, CheckMode)
  )

  def carrierAddress: Option[Row] = getAnswerAndBuildRow[CommonAddress](
    page = CarrierAddressPage,
    formatAnswer = formatAsAddress,
    prefix = "carrierAddress",
    id = None,
    call = routes.CarrierAddressController.onPageLoad(lrn, CheckMode)
  )

  def carrierEori: Option[Row] = getAnswerAndBuildRow[String](
    page = CarrierEoriPage,
    formatAnswer = formatAsLiteral,
    prefix = "carrierEori",
    id = None,
    call = routes.CarrierEoriController.onPageLoad(lrn, CheckMode)
  )

  def carrierName: Option[Row] = getAnswerAndBuildRow[String](
    page = CarrierNamePage,
    formatAnswer = formatAsLiteral,
    prefix = "carrierName",
    id = None,
    call = routes.CarrierNameController.onPageLoad(lrn, CheckMode)
  )

  def circumstanceIndicator(circumstanceIndicators: CircumstanceIndicatorList): Option[Row] = getAnswerAndBuildRow[String](
    page = CircumstanceIndicatorPage,
    formatAnswer = code =>
      lit"${circumstanceIndicators
        .getCircumstanceIndicator(code)
        .fold(code)(
          indicator => s"(${indicator.code}) ${indicator.description}"
        )}",
    prefix = "circumstanceIndicator",
    id = None,
    call = routes.CircumstanceIndicatorController.onPageLoad(lrn, CheckMode)
  )

  def commercialReferenceNumberAllItems: Option[Row] = getAnswerAndBuildRow[String](
    page = CommercialReferenceNumberAllItemsPage,
    formatAnswer = formatAsLiteral,
    prefix = "commercialReferenceNumberAllItems",
    id = None,
    call = routes.CommercialReferenceNumberAllItemsController.onPageLoad(lrn, CheckMode)
  )

  def conveyanceReferenceNumber: Option[Row] = getAnswerAndBuildRow[String](
    page = ConveyanceReferenceNumberPage,
    formatAnswer = formatAsLiteral,
    prefix = "conveyanceReferenceNumber",
    id = None,
    call = routes.ConveyanceReferenceNumberController.onPageLoad(lrn, CheckMode)
  )

  def placeOfUnloadingCode: Option[Row] = getAnswerAndBuildRow[String](
    page = PlaceOfUnloadingCodePage,
    formatAnswer = formatAsLiteral,
    prefix = "placeOfUnloadingCode",
    id = None,
    call = routes.PlaceOfUnloadingCodeController.onPageLoad(lrn, CheckMode)
  )

  def safetyAndSecurityConsigneeAddress: Option[Row] = getAnswerAndBuildRow[CommonAddress](
    page = SafetyAndSecurityConsigneeAddressPage,
    formatAnswer = formatAsAddress,
    prefix = "safetyAndSecurityConsigneeAddress",
    id = None,
    call = routes.SafetyAndSecurityConsigneeAddressController.onPageLoad(lrn, CheckMode)
  )

  def safetyAndSecurityConsigneeEori: Option[Row] = getAnswerAndBuildRow[String](
    page = SafetyAndSecurityConsigneeEoriPage,
    formatAnswer = formatAsLiteral,
    prefix = "safetyAndSecurityConsigneeEori",
    id = None,
    call = routes.SafetyAndSecurityConsigneeEoriController.onPageLoad(lrn, CheckMode)
  )

  def safetyAndSecurityConsigneeName: Option[Row] = getAnswerAndBuildRow[String](
    page = SafetyAndSecurityConsigneeNamePage,
    formatAnswer = formatAsLiteral,
    prefix = "safetyAndSecurityConsigneeName",
    id = None,
    call = routes.SafetyAndSecurityConsigneeNameController.onPageLoad(lrn, CheckMode)
  )

  def safetyAndSecurityConsignorAddress: Option[Row] = getAnswerAndBuildRow[CommonAddress](
    page = SafetyAndSecurityConsignorAddressPage,
    formatAnswer = formatAsAddress,
    prefix = "safetyAndSecurityConsignorAddress",
    id = None,
    call = routes.SafetyAndSecurityConsignorAddressController.onPageLoad(lrn, CheckMode)
  )

  def safetyAndSecurityConsignorEori: Option[Row] = getAnswerAndBuildRow[String](
    page = SafetyAndSecurityConsignorEoriPage,
    formatAnswer = formatAsLiteral,
    prefix = "safetyAndSecurityConsignorEori",
    id = None,
    call = routes.SafetyAndSecurityConsignorEoriController.onPageLoad(lrn, CheckMode)
  )

  def safetyAndSecurityConsignorName: Option[Row] = getAnswerAndBuildRow[String](
    page = SafetyAndSecurityConsignorNamePage,
    formatAnswer = formatAsLiteral,
    prefix = "safetyAndSecurityConsignorName",
    id = None,
    call = routes.SafetyAndSecurityConsignorNameController.onPageLoad(lrn, CheckMode)
  )

  def transportChargesPaymentMethod(): Option[Row] = getAnswerAndBuildRow[MethodOfPayment](
    page = TransportChargesPaymentMethodPage,
    formatAnswer = formatAsLiteral,
    prefix = "transportChargesPaymentMethod",
    id = None,
    call = routes.TransportChargesPaymentMethodController.onPageLoad(lrn, CheckMode)
  )

  def countryRow(index: Index, countries: CountryList): Option[Row] = getAnswerAndBuildCountryRow[CountryCode](
    getCountryCode = countryCode => countryCode,
    countryList = countries,
    getAnswerAndBuildRow = formatAnswer =>
      getAnswerAndBuildRemovableRow(
        page = CountryOfRoutingPage(index),
        formatAnswer = formatAnswer,
        id = s"country-${index.display}",
        changeCall = routes.CountryOfRoutingController.onPageLoad(lrn, index, CheckMode),
        removeCall = routes.ConfirmRemoveCountryController.onPageLoad(lrn, index, CheckMode)
      )
  )

  def countryOfRoutingRow(index: Index, countries: CountryList): Option[Row] = getAnswerAndBuildCountryRow[CountryCode](
    getCountryCode = countryCode => countryCode,
    countryList = countries,
    getAnswerAndBuildRow = formatAnswer =>
      getAnswerAndBuildValuelessRow(
        page = CountryOfRoutingPage(index),
        formatAnswer = formatAnswer,
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
