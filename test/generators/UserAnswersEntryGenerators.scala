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
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage}
import pages.movementDetails.PreLodgeDeclarationPage
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators {

  self: Generators =>

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

  implicit lazy val arbitraryAddItemsSameConsignorForAllItemsUserAnswersEntry: Arbitrary[(AddItemsSameConsignorForAllItemsPage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddItemsSameConsignorForAllItemsPage(Index(0)), value)
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

  implicit lazy val arbitraryAddItemsSameConsigneeForAllItemsUserAnswersEntry: Arbitrary[(AddItemsSameConsigneeForAllItemsPage, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (AddItemsSameConsigneeForAllItemsPage(Index(0)), value)
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

  implicit lazy val arbitraryOtherReferenceLiabilityAmountUserAnswersEntry: Arbitrary[(OtherReferenceLiabilityAmountPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (OtherReferenceLiabilityAmountPage, value)
    }

  implicit lazy val arbitraryAccessCodeUserAnswersEntry: Arbitrary[(AccessCodePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (AccessCodePage, value)
    }

  implicit lazy val arbitraryGuaranteeTypeUserAnswersEntry: Arbitrary[(GuaranteeTypePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[GuaranteeType].map(Json.toJson(_))
      } yield (GuaranteeTypePage, value)
    }

  implicit lazy val arbitraryOtherReferenceUserAnswersEntry: Arbitrary[(OtherReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (OtherReferencePage, value)
    }

  implicit lazy val arbitraryLiabilityAmountUserAnswersEntry: Arbitrary[(LiabilityAmountPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (LiabilityAmountPage, value)
    }

  implicit lazy val arbitraryGuaranteeReferenceUserAnswersEntry: Arbitrary[(GuaranteeReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- nonEmptyString.map(Json.toJson(_))
      } yield (GuaranteeReferencePage, value)
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

  implicit lazy val arbitraryDeclarePackagesUserAnswersEntry: Arbitrary[(DeclarePackagesPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (DeclarePackagesPage, value)
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
