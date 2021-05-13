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
import models.{CheckMode, CircumstanceIndicatorList, CountryList, Index, LocalReferenceNumber, Mode, UserAnswers}
import pages.safetyAndSecurity._
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._
import viewModels.AddAnotherViewModel

class SafetyAndSecurityCheckYourAnswerHelper(userAnswers: UserAnswers) {

  private def lrn: LocalReferenceNumber = userAnswers.id

  def addCarrierEori: Option[Row] = userAnswers.get(AddCarrierEoriPage) map {
    answer =>
      Row(
        key   = Key(msg"addCarrierEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddCarrierEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addCarrierEori.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def addCarrier: Option[Row] = userAnswers.get(AddCarrierPage) map {
    answer =>
      Row(
        key   = Key(msg"addCarrier.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddCarrierController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addCarrier.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def addCircumstanceIndicator: Option[Row] = userAnswers.get(AddCircumstanceIndicatorPage) map {
    answer =>
      Row(
        key   = Key(msg"addCircumstanceIndicator.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddCircumstanceIndicatorController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addCircumstanceIndicator.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def addCommercialReferenceNumberAllItems: Option[Row] = userAnswers.get(AddCommercialReferenceNumberAllItemsPage) map {
    answer =>
      Row(
        key   = Key(msg"addCommercialReferenceNumberAllItems.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddCommercialReferenceNumberAllItemsController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addCommercialReferenceNumberAllItems.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def addCommercialReferenceNumber: Option[Row] = userAnswers.get(AddCommercialReferenceNumberPage) map {
    answer =>
      Row(
        key   = Key(msg"addCommercialReferenceNumber.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddCommercialReferenceNumberController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addCommercialReferenceNumber.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def addConveyanceReferenceNumber: Option[Row] = userAnswers.get(AddConveyanceReferenceNumberPage) map {
    answer =>
      Row(
        key   = Key(msg"addConveyancerReferenceNumber.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddConveyanceReferenceNumberController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addConveyancerReferenceNumber.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def addPlaceOfUnloadingCode: Option[Row] = userAnswers.get(AddPlaceOfUnloadingCodePage) map {
    answer =>
      Row(
        key   = Key(msg"addPlaceOfUnloadingCode.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddPlaceOfUnloadingCodeController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addPlaceOfUnloadingCode.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def addSafetyAndSecurityConsigneeEori: Option[Row] = userAnswers.get(AddSafetyAndSecurityConsigneeEoriPage) map {
    answer =>
      Row(
        key   = Key(msg"addSafetyAndSecurityConsigneeEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddSafetyAndSecurityConsigneeEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addSafetyAndSecurityConsigneeEori.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def addSafetyAndSecurityConsignee: Option[Row] = userAnswers.get(AddSafetyAndSecurityConsigneePage) map {
    answer =>
      Row(
        key   = Key(msg"addSafetyAndSecurityConsignee.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddSafetyAndSecurityConsigneeController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addSafetyAndSecurityConsignor.checkYourAnswersLabel.visuallyHidden ")
          )
        )
      )
  }

  def addSafetyAndSecurityConsignorEori: Option[Row] = userAnswers.get(AddSafetyAndSecurityConsignorEoriPage) map {
    answer =>
      Row(
        key   = Key(msg"addSafetyAndSecurityConsignorEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddSafetyAndSecurityConsignorEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addSafetyAndSecurityConsignorEori.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def addSafetyAndSecurityConsignor: Option[Row] = userAnswers.get(AddSafetyAndSecurityConsignorPage) map {
    answer =>
      Row(
        key   = Key(msg"addSafetyAndSecurityConsignor.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddSafetyAndSecurityConsignorController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addSafetyAndSecurityConsignorEori.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def addTransportChargesPaymentMethod: Option[Row] = userAnswers.get(AddTransportChargesPaymentMethodPage) map {
    answer =>
      Row(
        key   = Key(msg"addTransportChargesPaymentMethod.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddTransportChargesPaymentMethodController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addTransportChargesPaymentMethod.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def carrierAddress: Option[Row] = userAnswers.get(CarrierAddressPage) map {
    answer =>
      val carrierAddress = Html(
        Seq(answer.AddressLine1, answer.AddressLine2, answer.AddressLine3, answer.country.description)
          .mkString("<br>"))
      Row(
        key   = Key(msg"carrierAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(carrierAddress),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.CarrierAddressController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"carrierAddress.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def carrierEori: Option[Row] = userAnswers.get(CarrierEoriPage) map {
    answer =>
      Row(
        key   = Key(msg"carrierEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.CarrierEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"carrierEori.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def carrierName: Option[Row] = userAnswers.get(CarrierNamePage) map {
    answer =>
      Row(
        key   = Key(msg"carrierName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.CarrierNameController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"carrierName.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def circumstanceIndicator(circumstanceIndicators: CircumstanceIndicatorList): Option[Row] = userAnswers.get(CircumstanceIndicatorPage) map {
    answer =>
      val circumstanceIndicator = circumstanceIndicators
        .getCircumstanceIndicator(answer)
        .map(
          indicator => s"(${indicator.code}) ${indicator.description}"
        )
        .getOrElse(answer)
      Row(
        key   = Key(msg"circumstanceIndicator.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$circumstanceIndicator"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.CircumstanceIndicatorController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"circumstanceIndicator.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def commercialReferenceNumberAllItems: Option[Row] = userAnswers.get(CommercialReferenceNumberAllItemsPage) map {
    answer =>
      Row(
        key   = Key(msg"commercialReferenceNumberAllItems.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.CommercialReferenceNumberAllItemsController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"commercialReferenceNumberAllItems.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def conveyanceReferenceNumber: Option[Row] = userAnswers.get(ConveyanceReferenceNumberPage) map {
    answer =>
      Row(
        key   = Key(msg"conveyanceReferenceNumber.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ConveyanceReferenceNumberController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"conveyanceReferenceNumber.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def placeOfUnloadingCode: Option[Row] = userAnswers.get(PlaceOfUnloadingCodePage) map {
    answer =>
      Row(
        key   = Key(msg"placeOfUnloadingCode.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.PlaceOfUnloadingCodeController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"placeOfUnloadingCode.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def safetyAndSecurityConsigneeAddress: Option[Row] = userAnswers.get(SafetyAndSecurityConsigneeAddressPage) map {
    answer =>
      val consigneeAddress = Html(
        Seq(answer.AddressLine1, answer.AddressLine2, answer.postalCode, answer.country.description)
          .mkString("<br>"))
      Row(
        key   = Key(msg"safetyAndSecurityConsigneeAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(consigneeAddress),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SafetyAndSecurityConsigneeAddressController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"safetyAndSecurityConsigneeAddress.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def safetyAndSecurityConsigneeEori: Option[Row] = userAnswers.get(SafetyAndSecurityConsigneeEoriPage) map {
    answer =>
      Row(
        key   = Key(msg"safetyAndSecurityConsigneeEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SafetyAndSecurityConsigneeEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"safetyAndSecurityConsigneeEori.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def safetyAndSecurityConsigneeName: Option[Row] = userAnswers.get(SafetyAndSecurityConsigneeNamePage) map {
    answer =>
      Row(
        key   = Key(msg"safetyAndSecurityConsigneeName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SafetyAndSecurityConsigneeNameController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"safetyAndSecurityConsigneeName.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def safetyAndSecurityConsignorAddress: Option[Row] = userAnswers.get(SafetyAndSecurityConsignorAddressPage) map {
    answer =>
      val consignorAddress = Html(
        Seq(answer.AddressLine1, answer.AddressLine2, answer.AddressLine3, answer.country.description)
          .mkString("<br>"))
      Row(
        key   = Key(msg"safetyAndSecurityConsignorAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(consignorAddress),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SafetyAndSecurityConsignorAddressController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"safetyAndSecurityConsignorAddress.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def safetyAndSecurityConsignorEori: Option[Row] = userAnswers.get(SafetyAndSecurityConsignorEoriPage) map {
    answer =>
      Row(
        key   = Key(msg"safetyAndSecurityConsignorEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SafetyAndSecurityConsignorEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"safetyAndSecurityConsignorEori.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def safetyAndSecurityConsignorName: Option[Row] = userAnswers.get(SafetyAndSecurityConsignorNamePage) map {
    answer =>
      Row(
        key   = Key(msg"safetyAndSecurityConsignorName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SafetyAndSecurityConsignorNameController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"safetyAndSecurityConsignorName.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def transportChargesPaymentMethod: Option[Row] = userAnswers.get(TransportChargesPaymentMethodPage) map {
    answer =>
      Row(
        key   = Key(msg"transportChargesPaymentMethod.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.TransportChargesPaymentMethodController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"transportChargesPaymentMethod.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def countryRows(index: Index, countries: CountryList, mode: Mode): Option[Row] =
    userAnswers.get(CountryOfRoutingPage(index)).map {
      answer =>
        val countryName = countries.getCountry(answer).map(_.description).getOrElse(answer.code)
        Row(
          key   = Key(lit"$countryName"),
          value = Value(lit""),
          actions = List(
            Action(
              content            = msg"site.change",
              href               = routes.CountryOfRoutingController.onPageLoad(lrn, index, mode).url,
              visuallyHiddenText = Some(msg"addAnotherCountryOfRouting.checkYourAnswersLabel.change.visuallyHidden".withArgs(answer)),
              attributes         = Map("id" -> s"""change-country-${index.display}""")
            ),
            Action(
              content            = msg"site.delete",
              href               = routes.ConfirmRemoveCountryController.onPageLoad(lrn, index, mode).url,
              visuallyHiddenText = Some(msg"addAnotherCountryOfRouting.checkYourAnswersLabel.remove.visuallyHidden".withArgs(answer)),
              attributes         = Map("id" -> s"""remove-country-${index.display}""")
            )
          )
        )
    }

  def countryOfRoutingRows(index: Index, countries: CountryList): Option[Row] =
    userAnswers.get(CountryOfRoutingPage(index)).map {
      answer =>
        val countryName = countries.getCountry(answer).map(_.description).getOrElse(answer.code)
        Row(
          key   = Key(lit"$countryName"),
          value = Value(lit""),
          actions = List(
            Action(
              content            = msg"site.change",
              href               = routes.AddAnotherCountryOfRoutingController.onPageLoad(lrn, CheckMode).url,
              visuallyHiddenText = Some(msg"addAnotherCountryOfRouting.checkYourAnswersLabel.change.visuallyHidden".withArgs(answer)),
              attributes         = Map("id" -> s"""change-country-${index.display}""")
            )
          )
        )
    }

  def addAnotherCountryOfRouting(content: Text): AddAnotherViewModel = {

    val addAnotherCountryOfRoutingHref = routes.AddAnotherCountryOfRoutingController.onPageLoad(lrn, CheckMode).url

    AddAnotherViewModel(addAnotherCountryOfRoutingHref, content)
  }

  def confirmRemoveCountry(index: Index): Option[Row] = userAnswers.get(ConfirmRemoveCountryPage) map {
    answer =>
      Row(
        key   = Key(msg"confirmRemoveCountry.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ConfirmRemoveCountryController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"confirmRemoveCountry.checkYourAnswersLabel"))
          )
        )
      )
  }

}
