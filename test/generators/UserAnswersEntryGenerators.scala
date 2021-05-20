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

package generators

import models._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import pages.addItems.{CommodityCodePage, ConfirmRemoveItemPage, _}
import pages.addItems.traderDetails._
import pages.addItems._
import pages.addItems.specialMentions._
import pages.addItems.containers.{AddAnotherContainerPage, ConfirmRemoveContainerPage, ContainerNumberPage}
import pages.addItems.securityDetails._
import pages.addItems.traderSecurityDetails.{
  AddSecurityConsigneesEoriPage,
  AddSecurityConsignorsEoriPage,
  SecurityConsigneeEoriPage,
  SecurityConsigneeNamePage,
  SecurityConsignorEoriPage,
  SecurityConsignorNamePage
}
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage}
import pages.movementDetails.PreLodgeDeclarationPage
import pages.safetyAndSecurity.{
  AddAnotherCountryOfRoutingPage,
  AddCarrierEoriPage,
  AddCarrierPage,
  AddCircumstanceIndicatorPage,
  AddCommercialReferenceNumberAllItemsPage,
  AddCommercialReferenceNumberPage,
  AddConveyanceReferenceNumberPage,
  AddPlaceOfUnloadingCodePage,
  AddSafetyAndSecurityConsigneeEoriPage,
  AddSafetyAndSecurityConsigneePage,
  AddSafetyAndSecurityConsignorEoriPage,
  AddSafetyAndSecurityConsignorPage,
  AddTransportChargesPaymentMethodPage,
  CarrierEoriPage,
  CarrierNamePage,
  CircumstanceIndicatorPage,
  CommercialReferenceNumberAllItemsPage,
  ConfirmRemoveCountryPage,
  ConveyanceReferenceNumberPage,
  CountryOfRoutingPage,
  PlaceOfUnloadingCodePage,
  SafetyAndSecurityConsigneeEoriPage,
  SafetyAndSecurityConsigneeNamePage,
  SafetyAndSecurityConsignorEoriPage,
  SafetyAndSecurityConsignorNamePage,
  TransportChargesPaymentMethodPage
}
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators {

  self: Generators =>

  implicit lazy val arbitraryOfficeOfTransitCountryUserAnswersEntry: Arbitrary[(OfficeOfTransitCountryPage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (OfficeOfTransitCountryPage(Index(0)), value)
    }

  implicit lazy val arbitraryLoadingPlaceUserAnswersEntry: Arbitrary[(LoadingPlacePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[LoadingPlacePage.type#Data].map(Json.toJson(_))
      } yield (LoadingPlacePage, value)
    }

  implicit lazy val arbitraryMovementDestinationCountryUserAnswersEntry: Arbitrary[(MovementDestinationCountryPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[MovementDestinationCountryPage.type#Data].map(Json.toJson(_))
      } yield (MovementDestinationCountryPage, value)
    }

  implicit lazy val arbitraryConfirmRemoveGuaranteeUserAnswersEntry: Arbitrary[(ConfirmRemoveGuaranteePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[ConfirmRemoveGuaranteePage.type#Data].map(Json.toJson(_))
      } yield (ConfirmRemoveGuaranteePage, value)
    }

  implicit lazy val arbitraryAddAnotherGuaranteeUserAnswersEntry: Arbitrary[(AddAnotherGuaranteePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[AddAnotherGuaranteePage.type#Data].map(Json.toJson(_))
      } yield (AddAnotherGuaranteePage, value)
    }

  implicit lazy val arbitraryConfirmRemoveCountryUserAnswersEntry: Arbitrary[(ConfirmRemoveCountryPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[ConfirmRemoveCountryPage.type#Data].map(Json.toJson(_))
      } yield (ConfirmRemoveCountryPage, value)
    }

  implicit lazy val arbitraryCarrierNameUserAnswersEntry: Arbitrary[(CarrierNamePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[CarrierNamePage.type#Data].map(Json.toJson(_))
      } yield (CarrierNamePage, value)
    }

  implicit lazy val arbitraryCarrierEoriUserAnswersEntry: Arbitrary[(CarrierEoriPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[CarrierEoriPage.type#Data].map(Json.toJson(_))
      } yield (CarrierEoriPage, value)
    }

  implicit lazy val arbitraryCarrierAddressUserAnswersEntry: Arbitrary[(CarrierAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[CarrierAddress].map(Json.toJson(_))
      } yield (CarrierAddressPage, value)
    }

  implicit lazy val arbitraryAddCarrierUserAnswersEntry: Arbitrary[(AddCarrierPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[AddCarrierPage.type#Data].map(Json.toJson(_))
      } yield (AddCarrierPage, value)
    }

  implicit lazy val arbitraryAddCarrierEoriUserAnswersEntry: Arbitrary[(AddCarrierEoriPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[AddCarrierEoriPage.type#Data].map(Json.toJson(_))
      } yield (AddCarrierEoriPage, value)
    }

  implicit lazy val arbitrarySafetyAndSecurityConsigneeNameUserAnswersEntry: Arbitrary[(SafetyAndSecurityConsigneeNamePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[SafetyAndSecurityConsigneeNamePage.type#Data].map(Json.toJson(_))
      } yield (SafetyAndSecurityConsigneeNamePage, value)
    }

  implicit lazy val arbitrarySafetyAndSecurityConsigneeEoriUserAnswersEntry: Arbitrary[(SafetyAndSecurityConsigneeEoriPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[SafetyAndSecurityConsigneeEoriPage.type#Data].map(Json.toJson(_))
      } yield (SafetyAndSecurityConsigneeEoriPage, value)
    }

  implicit lazy val arbitrarySafetyAndSecurityConsigneeAddressUserAnswersEntry: Arbitrary[(SafetyAndSecurityConsigneeAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[SafetyAndSecurityConsigneeAddressPage.type#Data].map(Json.toJson(_))
      } yield (SafetyAndSecurityConsigneeAddressPage, value)
    }

  implicit lazy val arbitraryAddSafetyAndSecurityConsigneeEoriUserAnswersEntry: Arbitrary[(AddSafetyAndSecurityConsigneeEoriPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[AddSafetyAndSecurityConsigneeEoriPage.type#Data].map(Json.toJson(_))
      } yield (AddSafetyAndSecurityConsigneeEoriPage, value)
    }

  implicit lazy val arbitraryAddSafetyAndSecurityConsigneeUserAnswersEntry: Arbitrary[(AddSafetyAndSecurityConsigneePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[AddSafetyAndSecurityConsigneePage.type#Data].map(Json.toJson(_))
      } yield (AddSafetyAndSecurityConsigneePage, value)
    }

  implicit lazy val arbitrarySafetyAndSecurityConsignorNameUserAnswersEntry: Arbitrary[(SafetyAndSecurityConsignorNamePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[SafetyAndSecurityConsignorNamePage.type#Data].map(Json.toJson(_))
      } yield (SafetyAndSecurityConsignorNamePage, value)
    }

  implicit lazy val arbitrarySafetyAndSecurityConsignorEoriUserAnswersEntry: Arbitrary[(SafetyAndSecurityConsignorEoriPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[SafetyAndSecurityConsignorEoriPage.type#Data].map(Json.toJson(_))
      } yield (SafetyAndSecurityConsignorEoriPage, value)
    }

  implicit lazy val arbitrarySafetyAndSecurityConsignorAddressUserAnswersEntry: Arbitrary[(SafetyAndSecurityConsignorAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[SafetyAndSecurityConsignorAddressPage.type#Data].map(Json.toJson(_))
      } yield (SafetyAndSecurityConsignorAddressPage, value)
    }

  implicit lazy val arbitraryAddSafetyAndSecurityConsignorEoriUserAnswersEntry: Arbitrary[(AddSafetyAndSecurityConsignorEoriPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[AddSafetyAndSecurityConsignorEoriPage.type#Data].map(Json.toJson(_))
      } yield (AddSafetyAndSecurityConsignorEoriPage, value)
    }

  implicit lazy val arbitraryAddSafetyAndSecurityConsignorUserAnswersEntry: Arbitrary[(AddSafetyAndSecurityConsignorPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[AddSafetyAndSecurityConsignorPage.type#Data].map(Json.toJson(_))
      } yield (AddSafetyAndSecurityConsignorPage, value)
    }

  implicit lazy val arbitraryAddAnotherCountryOfRoutingUserAnswersEntry: Arbitrary[(AddAnotherCountryOfRoutingPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[AddAnotherCountryOfRoutingPage.type#Data].map(Json.toJson(_))
      } yield (AddAnotherCountryOfRoutingPage, value)
    }

  implicit lazy val arbitraryCountryOfRoutingUserAnswersEntry: Arbitrary[(CountryOfRoutingPage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (CountryOfRoutingPage(Index(0)), value)
    }

  implicit lazy val arbitraryPlaceOfUnloadingCodeUserAnswersEntry: Arbitrary[(PlaceOfUnloadingCodePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[PlaceOfUnloadingCodePage.type#Data].map(Json.toJson(_))
      } yield (PlaceOfUnloadingCodePage, value)
    }

  implicit lazy val arbitraryAddPlaceOfUnloadingCodeUserAnswersEntry: Arbitrary[(AddPlaceOfUnloadingCodePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[AddPlaceOfUnloadingCodePage.type#Data].map(Json.toJson(_))
      } yield (AddPlaceOfUnloadingCodePage, value)
    }

  implicit lazy val arbitraryConveyanceReferenceNumberUserAnswersEntry: Arbitrary[(ConveyanceReferenceNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[ConveyanceReferenceNumberPage.type#Data].map(Json.toJson(_))
      } yield (ConveyanceReferenceNumberPage, value)
    }

  implicit lazy val arbitraryAddConveyancerReferenceNumberUserAnswersEntry: Arbitrary[(AddConveyanceReferenceNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[AddConveyanceReferenceNumberPage.type#Data].map(Json.toJson(_))
      } yield (AddConveyanceReferenceNumberPage, value)
    }

  implicit lazy val arbitraryCommercialReferenceNumberAllItemsUserAnswersEntry: Arbitrary[(CommercialReferenceNumberAllItemsPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[CommercialReferenceNumberAllItemsPage.type#Data].map(Json.toJson(_))
      } yield (CommercialReferenceNumberAllItemsPage, value)
    }

  implicit lazy val arbitraryAddCommercialReferenceNumberAllItemsUserAnswersEntry: Arbitrary[(AddCommercialReferenceNumberAllItemsPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[AddCommercialReferenceNumberAllItemsPage.type#Data].map(Json.toJson(_))
      } yield (AddCommercialReferenceNumberAllItemsPage, value)
    }

  implicit lazy val arbitraryTransportChargesPaymentMethodUserAnswersEntry: Arbitrary[(TransportChargesPaymentMethodPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[TransportChargesPaymentMethodPage.type#Data].map(Json.toJson(_))
      } yield (TransportChargesPaymentMethodPage, value)
    }

  implicit lazy val arbitraryCircumstanceIndicatorUserAnswersEntry: Arbitrary[(CircumstanceIndicatorPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[CircumstanceIndicatorPage.type#Data].map(Json.toJson(_))
      } yield (CircumstanceIndicatorPage, value)
    }

  implicit lazy val arbitraryAddCommercialReferenceNumberUserAnswersEntry: Arbitrary[(AddCommercialReferenceNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[AddCommercialReferenceNumberPage.type#Data].map(Json.toJson(_))
      } yield (AddCommercialReferenceNumberPage, value)
    }

  implicit lazy val arbitraryAddTransportChargesPaymentMethodUserAnswersEntry: Arbitrary[(AddTransportChargesPaymentMethodPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[AddTransportChargesPaymentMethodPage.type#Data].map(Json.toJson(_))
      } yield (AddTransportChargesPaymentMethodPage, value)
    }

  implicit lazy val arbitraryAddCircumstanceIndicatorUserAnswersEntry: Arbitrary[(AddCircumstanceIndicatorPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[AddCircumstanceIndicatorPage.type#Data].map(Json.toJson(_))
      } yield (AddCircumstanceIndicatorPage, value)
    }

  implicit lazy val arbitrarySecurityConsigneeNameUserAnswersEntry: Arbitrary[(SecurityConsigneeNamePage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (SecurityConsigneeNamePage(Index(0)), value)
    }

  implicit lazy val arbitrarySecurityConsignorNameUserAnswersEntry: Arbitrary[(SecurityConsignorNamePage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (SecurityConsignorNamePage(Index(0)), value)
    }

  implicit lazy val arbitrarySecurityConsigneeAddressUserAnswersEntry: Arbitrary[(SecurityConsigneeAddressPage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (SecurityConsigneeAddressPage(Index(0)), value)
    }

  implicit lazy val arbitrarySecurityConsignorAddressUserAnswersEntry: Arbitrary[(SecurityConsignorAddressPage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (SecurityConsignorAddressPage(Index(0)), value)
    }

  implicit lazy val arbitrarySecurityConsigneeEoriUserAnswersEntry: Arbitrary[(SecurityConsigneeEoriPage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (SecurityConsigneeEoriPage(Index(0)), value)
    }

  implicit lazy val arbitrarySecurityConsignorEoriUserAnswersEntry: Arbitrary[(SecurityConsignorEoriPage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (SecurityConsignorEoriPage(Index(0)), value)
    }

  implicit lazy val arbitraryAddSecurityConsigneesEoriUserAnswersEntry: Arbitrary[(AddSecurityConsigneesEoriPage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddSecurityConsigneesEoriPage(Index(0)), value)
    }

  implicit lazy val arbitraryAddSecurityConsignorsEoriUserAnswersEntry: Arbitrary[(AddSecurityConsignorsEoriPage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddSecurityConsignorsEoriPage(Index(0)), value)
    }

  implicit lazy val arbitraryDangerousGoodsCodeUserAnswersEntry: Arbitrary[(DangerousGoodsCodePage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (DangerousGoodsCodePage(Index(0)), value)
    }

  implicit lazy val arbitraryAddDangerousGoodsCodeUserAnswersEntry: Arbitrary[(AddDangerousGoodsCodePage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddDangerousGoodsCodePage(Index(0)), value)
    }

  implicit lazy val arbitraryCommercialReferenceNumberUserAnswersEntry: Arbitrary[(CommercialReferenceNumberPage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (CommercialReferenceNumberPage(Index(0)), value)
    }

  implicit lazy val arbitraryTransportChargesUserAnswersEntry: Arbitrary[(TransportChargesPage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (TransportChargesPage(Index(0)), value)
    }

  implicit lazy val arbitraryConfirmRemoveContainerUserAnswersEntry: Arbitrary[(ConfirmRemoveContainerPage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (ConfirmRemoveContainerPage(Index(0), Index(0)), value)
    }

  implicit lazy val arbitraryAddAnotherDocumentUserAnswersEntry: Arbitrary[(AddAnotherDocumentPage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddAnotherDocumentPage(Index(0)), value)
    }

  implicit lazy val arbitraryConfirmRemoveDocumentUserAnswersEntry: Arbitrary[(ConfirmRemoveDocumentPage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (ConfirmRemoveDocumentPage(Index(0), Index(0)), value)
    }

  implicit lazy val arbitraryDocumentReferenceUserAnswersEntry: Arbitrary[(DocumentReferencePage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (DocumentReferencePage(Index(0), Index(0)), value)
    }

  implicit lazy val arbitraryDocumentExtraInformationUserAnswersEntry: Arbitrary[(DocumentExtraInformationPage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (DocumentExtraInformationPage(Index(0), Index(0)), value)
    }

  implicit lazy val arbitraryAddExtraDocumentInformationUserAnswersEntry: Arbitrary[(AddExtraDocumentInformationPage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddExtraDocumentInformationPage(Index(0), Index(0)), value)
    }

  implicit lazy val arbitraryAddDocumentsUserAnswersEntry: Arbitrary[(AddDocumentsPage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddDocumentsPage(Index(0)), value)
    }

  implicit lazy val arbitraryConfirmRemovePreviousAdministrativeReferenceUserAnswersEntry
    : Arbitrary[(ConfirmRemovePreviousAdministrativeReferencePage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (ConfirmRemovePreviousAdministrativeReferencePage(Index(0), Index(0)), value)
    }

  implicit lazy val arbitraryRemoveSpecialMentionUserAnswersEntry: Arbitrary[(RemoveSpecialMentionPage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (RemoveSpecialMentionPage(Index(0), Index(0)), value)
    }

  implicit lazy val arbitraryAddAnotherSpecialMentionUserAnswersEntry: Arbitrary[(AddAnotherSpecialMentionPage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddAnotherSpecialMentionPage(Index(0)), value)
    }

  implicit lazy val arbitrarySpecialMentionAdditionalInfoUserAnswersEntry: Arbitrary[(SpecialMentionAdditionalInfoPage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (SpecialMentionAdditionalInfoPage(Index(0), Index(0)), value)
    }

  implicit lazy val arbitrarySpecialMentionTypeUserAnswersEntry: Arbitrary[(SpecialMentionTypePage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (SpecialMentionTypePage(Index(0), Index(0)), value)
    }

  implicit lazy val arbitraryAddSpecialMentionUserAnswersEntry: Arbitrary[(AddSpecialMentionPage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddSpecialMentionPage(Index(0)), value)
    }

  implicit lazy val arbitraryAddAnotherContainerUserAnswersEntry: Arbitrary[(AddAnotherContainerPage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddAnotherContainerPage(Index(0)), value)
    }

  implicit lazy val arbitraryContainerNumberUserAnswersEntry: Arbitrary[(ContainerNumberPage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (ContainerNumberPage(Index(0), Index(0)), value)
    }

  implicit lazy val arbitraryAddAnotherPreviousAdministrativeReferenceUserAnswersEntry: Arbitrary[(AddAnotherPreviousAdministrativeReferencePage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddAnotherPreviousAdministrativeReferencePage(Index(0)), value)
    }

  implicit lazy val arbitraryExtraInformationUserAnswersEntry: Arbitrary[(ExtraInformationPage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (ExtraInformationPage(Index(0), Index(0)), value)
    }

  implicit lazy val arbitraryConfirmRemoveItemUserAnswersEntry: Arbitrary[(ConfirmRemoveItemPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (ConfirmRemoveItemPage, value)
    }

  implicit lazy val arbitraryReferenceTypeUserAnswersEntry: Arbitrary[(ReferenceTypePage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[String].map(Json.toJson(_))
      } yield (ReferenceTypePage(Index(0), Index(0)), value)
    }

  implicit lazy val arbitraryDocumentTypeUserAnswersEntry: Arbitrary[(DocumentTypePage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[String].map(Json.toJson(_))
      } yield (DocumentTypePage(Index(0), Index(0)), value)
    }

  implicit lazy val arbitraryAddAdministrativeReferenceUserAnswersEntry: Arbitrary[(AddAdministrativeReferencePage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddAdministrativeReferencePage(Index(0)), value)
    }

  implicit lazy val arbitraryPreviousReferenceUserAnswersEntry: Arbitrary[(PreviousReferencePage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[String].map(Json.toJson(_))
      } yield (PreviousReferencePage(Index(0), Index(0)), value)
    }

  implicit lazy val arbitraryAddExtraInformationUserAnswersEntry: Arbitrary[(AddExtraInformationPage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddExtraInformationPage(Index(0), Index(0)), value)
    }

  implicit lazy val arbitraryHowManyPackagesUserAnswersEntry: Arbitrary[(HowManyPackagesPage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (HowManyPackagesPage(Index(0), Index(0)), value)
    }

  implicit lazy val arbitraryAddAnotherPackageUserAnswersEntry: Arbitrary[(AddAnotherPackagePage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddAnotherPackagePage(Index(0)), value)
    }

  implicit lazy val arbitraryDeclareMarkUserAnswersEntry: Arbitrary[(DeclareMarkPage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (DeclareMarkPage(Index(0), Index(0)), value)
    }

  implicit lazy val arbitraryAddMarkUserAnswersEntry: Arbitrary[(AddMarkPage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddMarkPage(Index(0), Index(0)), value)
    }

  implicit lazy val arbitraryTotalPiecesUserAnswersEntry: Arbitrary[(TotalPiecesPage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (TotalPiecesPage(Index(0), Index(0)), value)
    }

  implicit lazy val arbitraryDeclareNumberOfPackagesUserAnswersEntry: Arbitrary[(DeclareNumberOfPackagesPage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (DeclareNumberOfPackagesPage(Index(0), Index(0)), value)
    }

  implicit lazy val arbitraryCommodityCodeUserAnswersEntry: Arbitrary[(CommodityCodePage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (CommodityCodePage(Index(0)), value)
    }

  implicit lazy val arbitraryAddAnotherItemUserAnswersEntry: Arbitrary[(AddAnotherItemPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddAnotherItemPage, value)
    }

  implicit lazy val arbitraryTraderDetailsConsignorNameUserAnswersEntry: Arbitrary[(TraderDetailsConsignorNamePage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (TraderDetailsConsignorNamePage(Index(0)), value)
    }

  implicit lazy val arbitraryTraderDetailsConsignorEoriNumberUserAnswersEntry: Arbitrary[(TraderDetailsConsignorEoriNumberPage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (TraderDetailsConsignorEoriNumberPage(Index(0)), value)
    }

  implicit lazy val arbitraryTraderDetailsConsignorEoriKnownUserAnswersEntry: Arbitrary[(TraderDetailsConsignorEoriKnownPage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (TraderDetailsConsignorEoriKnownPage(Index(0)), value)
    }

  implicit lazy val arbitraryTraderDetailsConsignorAddressUserAnswersEntry: Arbitrary[(TraderDetailsConsignorAddressPage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (TraderDetailsConsignorAddressPage(Index(0)), value)
    }

  implicit lazy val arbitraryTraderDetailsConsigneeNameUserAnswersEntry: Arbitrary[(TraderDetailsConsigneeNamePage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (TraderDetailsConsigneeNamePage(Index(0)), value)
    }

  implicit lazy val arbitraryTraderDetailsConsigneeEoriNumberUserAnswersEntry: Arbitrary[(TraderDetailsConsigneeEoriNumberPage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (TraderDetailsConsigneeEoriNumberPage(Index(0)), value)
    }

  implicit lazy val arbitraryTraderDetailsConsigneeEoriKnownUserAnswersEntry: Arbitrary[(TraderDetailsConsigneeEoriKnownPage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (TraderDetailsConsigneeEoriKnownPage(Index(0)), value)
    }

  implicit lazy val arbitraryTraderDetailsConsigneeAddressUserAnswersEntry: Arbitrary[(TraderDetailsConsigneeAddressPage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (TraderDetailsConsigneeAddressPage(Index(0)), value)
    }

  implicit lazy val arbitraryPreLodgeDeclarationUserAnswersEntry: Arbitrary[(PreLodgeDeclarationPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (PreLodgeDeclarationPage, value)
    }

  implicit lazy val arbitraryTotalNetMassUserAnswersEntry: Arbitrary[(TotalNetMassPage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (TotalNetMassPage(Index(0)), value)
    }

  implicit lazy val arbitraryIsCommodityCodeKnownUserAnswersEntry: Arbitrary[(IsCommodityCodeKnownPage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (IsCommodityCodeKnownPage(Index(0)), value)
    }

  implicit lazy val arbitraryItemTotalGrossMassUserAnswersEntry: Arbitrary[(ItemTotalGrossMassPage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (ItemTotalGrossMassPage(Index(0)), value)
    }

  implicit lazy val arbitraryAddTotalNetMassUserAnswersEntry: Arbitrary[(AddTotalNetMassPage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddTotalNetMassPage(Index(0)), value)
    }

  implicit lazy val arbitraryItemDescriptionUserAnswersEntry: Arbitrary[(ItemDescriptionPage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (ItemDescriptionPage(Index(0)), value)
    }

  implicit lazy val arbitraryOtherReferenceLiabilityAmountUserAnswersEntry: Arbitrary[(OtherReferenceLiabilityAmountPage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (OtherReferenceLiabilityAmountPage(Index(0)), value)
    }

  implicit lazy val arbitraryAccessCodeUserAnswersEntry: Arbitrary[(AccessCodePage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (AccessCodePage(Index(0)), value)
    }

  implicit lazy val arbitraryGuaranteeTypeUserAnswersEntry: Arbitrary[(GuaranteeTypePage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[GuaranteeType].map(Json.toJson(_))
      } yield (GuaranteeTypePage(Index(0)), value)
    }

  implicit lazy val arbitraryOtherReferenceUserAnswersEntry: Arbitrary[(OtherReferencePage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (OtherReferencePage(Index(0)), value)
    }

  implicit lazy val arbitraryLiabilityAmountUserAnswersEntry: Arbitrary[(LiabilityAmountPage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (LiabilityAmountPage(Index(0)), value)
    }

  implicit lazy val arbitraryGuaranteeReferenceUserAnswersEntry: Arbitrary[(GuaranteeReferencePage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (GuaranteeReferencePage(Index(0)), value)
    }

  implicit lazy val arbitraryConfirmRemoveSealsUserAnswersEntry: Arbitrary[(ConfirmRemoveSealsPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (ConfirmRemoveSealsPage, value)
    }

  implicit lazy val arbitraryConfirmRemoveOfficeOfTransitUserAnswersEntry: Arbitrary[(ConfirmRemoveOfficeOfTransitPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (ConfirmRemoveOfficeOfTransitPage, value)
    }

  implicit lazy val arbitrarySealsInformationUserAnswersEntry: Arbitrary[(SealsInformationPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (SealsInformationPage, value)
    }

  implicit lazy val arbitraryControlResultDateLimitUserAnswersEntry: Arbitrary[(ControlResultDateLimitPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (ControlResultDateLimitPage, value)
    }

  implicit lazy val arbitraryArrivalTimesAtOfficeUserAnswersEntry: Arbitrary[(ArrivalTimesAtOfficePage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (ArrivalTimesAtOfficePage(Index(0)), value)
    }

  implicit lazy val arbitrarySealIdDetailsUserAnswersEntry: Arbitrary[(SealIdDetailsPage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (SealIdDetailsPage(Index(0)), value)
    }

  implicit lazy val arbitraryAddSealsUserAnswersEntry: Arbitrary[(AddSealsPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddSealsPage, value)
    }

  implicit lazy val arbitraryCustomsApprovedLocationUserAnswersEntry: Arbitrary[(CustomsApprovedLocationPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (CustomsApprovedLocationPage, value)
    }

  implicit lazy val arbitraryAddCustomsApprovedLocationUserAnswersEntry: Arbitrary[(AddCustomsApprovedLocationPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddCustomsApprovedLocationPage, value)
    }

  implicit lazy val arbitraryTotalGrossMassUserAnswersEntry: Arbitrary[(TotalGrossMassPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (TotalGrossMassPage, value)
    }

  implicit lazy val arbitraryAuthorisedLocationCodeUserAnswersEntry: Arbitrary[(AuthorisedLocationCodePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (AuthorisedLocationCodePage, value)
    }

  implicit lazy val arbitraryTotalPackagesUserAnswersEntry: Arbitrary[(TotalPackagesPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (TotalPackagesPage, value)
    }

  implicit lazy val arbitraryAddAnotherTransitOfficeUserAnswersEntry: Arbitrary[(AddAnotherTransitOfficePage, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (AddAnotherTransitOfficePage(Index(0)), value)
    }

  implicit lazy val arbitraryDestinationOfficeUserAnswersEntry: Arbitrary[(DestinationOfficePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (DestinationOfficePage, value)
    }

  implicit lazy val arbitraryAddTransitOfficeUserAnswersEntry: Arbitrary[(AddTransitOfficePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddTransitOfficePage, value)
    }

  implicit lazy val arbitraryModeAtBorderUserAnswersEntry: Arbitrary[(ModeAtBorderPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (ModeAtBorderPage, value)
    }

  implicit lazy val arbitraryNationalityCrossingBorderUserAnswersEntry: Arbitrary[(NationalityCrossingBorderPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (NationalityCrossingBorderPage, value)
    }

  implicit lazy val arbitraryModeCrossingBorderUserAnswersEntry: Arbitrary[(ModeCrossingBorderPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (ModeCrossingBorderPage, value)
    }

  implicit lazy val arbitraryInlandModeUserAnswersEntry: Arbitrary[(InlandModePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (InlandModePage, value)
    }

  implicit lazy val arbitraryAddIdAtDepartureUserAnswersEntry: Arbitrary[(AddIdAtDeparturePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddIdAtDeparturePage, value)
    }

  implicit lazy val arbitraryIdCrossingBorderUserAnswersEntry: Arbitrary[(IdCrossingBorderPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (IdCrossingBorderPage, value)
    }

  implicit lazy val arbitraryDestinationCountryUserAnswersEntry: Arbitrary[(DestinationCountryPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- stringsWithMaxLength(2).suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (DestinationCountryPage, value)
    }

  implicit lazy val arbitraryChangeAtBorderUserAnswersEntry: Arbitrary[(ChangeAtBorderPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (ChangeAtBorderPage, value)
    }

  implicit lazy val arbitraryNationalityAtDepartureUserAnswersEntry: Arbitrary[(NationalityAtDeparturePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (NationalityAtDeparturePage, value)
    }

  implicit lazy val arbitraryIdAtDepartureUserAnswersEntry: Arbitrary[(IdAtDeparturePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (IdAtDeparturePage, value)
    }

  implicit lazy val arbitraryPrincipalAddressUserAnswersEntry: Arbitrary[(PrincipalAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[PrincipalAddress].map(Json.toJson(_))
      } yield (PrincipalAddressPage, value)
    }

  implicit lazy val arbitraryConsignorAddressUserAnswersEntry: Arbitrary[(ConsignorAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[ConsignorAddress].map(Json.toJson(_))
      } yield (ConsignorAddressPage, value)
    }

  implicit lazy val arbitraryOfficeOfDepartureUserAnswersEntry: Arbitrary[(OfficeOfDeparturePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (OfficeOfDeparturePage, value)
    }

  implicit lazy val arbitraryConsigneeAddressUserAnswersEntry: Arbitrary[(ConsigneeAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[ConsigneeAddress].map(Json.toJson(_))
      } yield (ConsigneeAddressPage, value)
    }

  implicit lazy val arbitraryCountryOfDispatchUserAnswersEntry: Arbitrary[(CountryOfDispatchPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- stringsWithMaxLength(2).suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (CountryOfDispatchPage, value)
    }

  implicit lazy val arbitraryConsigneeNameUserAnswersEntry: Arbitrary[(ConsigneeNamePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (ConsigneeNamePage, value)
    }

  implicit lazy val arbitraryWhatIsConsigneeEoriUserAnswersEntry: Arbitrary[(WhatIsConsigneeEoriPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (WhatIsConsigneeEoriPage, value)
    }

  implicit lazy val arbitraryConsignorNameUserAnswersEntry: Arbitrary[(ConsignorNamePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (ConsignorNamePage, value)
    }

  implicit lazy val arbitraryAddConsigneeUserAnswersEntry: Arbitrary[(AddConsigneePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddConsigneePage, value)
    }

  implicit lazy val arbitraryIsConsigneeEoriKnownUserAnswersEntry: Arbitrary[(IsConsigneeEoriKnownPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (IsConsigneeEoriKnownPage, value)
    }

  implicit lazy val arbitraryAddConsignorUserAnswersEntry: Arbitrary[(AddConsignorPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddConsignorPage, value)
    }

  implicit lazy val arbitraryIsConsignorEoriKnownUserAnswersEntry: Arbitrary[(IsConsignorEoriKnownPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (IsConsignorEoriKnownPage, value)
    }

  implicit lazy val arbitraryConsignorEoriUserAnswersEntry: Arbitrary[(ConsignorEoriPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (ConsignorEoriPage, value)
    }

  implicit lazy val arbitraryPrincipalNameUserAnswersEntry: Arbitrary[(PrincipalNamePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (PrincipalNamePage, value)
    }

  implicit lazy val arbitraryIsPrincipalEoriKnownUserAnswersEntry: Arbitrary[(IsPrincipalEoriKnownPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (IsPrincipalEoriKnownPage, value)
    }

  implicit lazy val arbitraryWhatIsPrincipalEoriUserAnswersEntry: Arbitrary[(WhatIsPrincipalEoriPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (WhatIsPrincipalEoriPage, value)
    }

  implicit lazy val arbitraryRepresentativeCapacityUserAnswersEntry: Arbitrary[(RepresentativeCapacityPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[RepresentativeCapacity].map(Json.toJson(_))
      } yield (RepresentativeCapacityPage, value)
    }

  implicit lazy val arbitraryRepresentativeNameUserAnswersEntry: Arbitrary[(RepresentativeNamePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (RepresentativeNamePage, value)
    }

  implicit lazy val arbitraryContainersUsedUserAnswersEntry: Arbitrary[(ContainersUsedPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (ContainersUsedPage, value)
    }

  implicit lazy val arbitraryDeclarationForSomeoneElseUserAnswersEntry: Arbitrary[(DeclarationForSomeoneElsePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (DeclarationForSomeoneElsePage, value)
    }

  implicit lazy val arbitraryDeclarationPlaceUserAnswersEntry: Arbitrary[(DeclarationPlacePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (DeclarationPlacePage, value)
    }

  implicit lazy val arbitraryProcedureTypeUserAnswersEntry: Arbitrary[(ProcedureTypePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[ProcedureType].map(Json.toJson(_))
      } yield (ProcedureTypePage, value)
    }

  implicit lazy val arbitraryDeclarationTypeUserAnswersEntry: Arbitrary[(DeclarationTypePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[DeclarationType].map(Json.toJson(_))
      } yield (DeclarationTypePage, value)
    }

  implicit lazy val arbitraryAddSecurityDetailsUserAnswersEntry: Arbitrary[(AddSecurityDetailsPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddSecurityDetailsPage, value)
    }

  implicit lazy val arbitraryLocalReferenceNumberUserAnswersEntry: Arbitrary[(LocalReferenceNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (LocalReferenceNumberPage, value)
    }
}
