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
import pages.addItems.containers.{AddAnotherContainerPage, ContainerNumberPage}
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage}
import pages.movementDetails.PreLodgeDeclarationPage
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators {

  self: Generators =>

  implicit lazy val arbitraryConfirmRemovePreviousAdministrativeReferenceUserAnswersEntry
    : Arbitrary[(ConfirmRemovePreviousAdministrativeReferencePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConfirmRemovePreviousAdministrativeReferencePage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryRemoveSpecialMentionUserAnswersEntry: Arbitrary[(RemoveSpecialMentionPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[RemoveSpecialMentionPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddAnotherSpecialMentionUserAnswersEntry: Arbitrary[(AddAnotherSpecialMentionPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddAnotherSpecialMentionPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySpecialMentionAdditionalInfoUserAnswersEntry: Arbitrary[(SpecialMentionAdditionalInfoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SpecialMentionAdditionalInfoPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySpecialMentionTypeUserAnswersEntry: Arbitrary[(SpecialMentionTypePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SpecialMentionTypePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddSpecialMentionUserAnswersEntry: Arbitrary[(AddSpecialMentionPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddSpecialMentionPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddAnotherContainerUserAnswersEntry: Arbitrary[(AddAnotherContainerPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddAnotherContainerPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryContainerNumberUserAnswersEntry: Arbitrary[(ContainerNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ContainerNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddAnotherPreviousAdministrativeReferenceUserAnswersEntry: Arbitrary[(AddAnotherPreviousAdministrativeReferencePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddAnotherPreviousAdministrativeReferencePage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryExtraInformationUserAnswersEntry: Arbitrary[(ExtraInformationPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ExtraInformationPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddItemsSameConsignorForAllItemsUserAnswersEntry: Arbitrary[(AddItemsSameConsignorForAllItemsPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddItemsSameConsignorForAllItemsPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield page -> value
    }

  implicit lazy val arbConfirmRemoveItemPage: Arbitrary[(ConfirmRemoveItemPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConfirmRemoveItemPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield page -> value
    }

  implicit lazy val arbReferenceTypePage: Arbitrary[(ReferenceTypePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ReferenceTypePage]
        value <- arbitrary[String].map(Json.toJson(_))
      } yield page -> value
    }

  implicit lazy val arbAddAdministrativeReferencePage: Arbitrary[(AddAdministrativeReferencePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddAdministrativeReferencePage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield page -> value
    }

  implicit lazy val arbitraryPreviousReferencePageUserAnswersEntry: Arbitrary[(PreviousReferencePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PreviousReferencePage]
        value <- arbitrary[String].map(Json.toJson(_))
      } yield page -> value
    }

  implicit lazy val arbitraryAddExtraInformationPageUserAnswersEntry: Arbitrary[(AddExtraInformationPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddExtraInformationPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield page -> value
    }

  implicit lazy val arbitraryAddItemsSameConsigneeForAllItemsUserAnswersEntry: Arbitrary[(AddItemsSameConsigneeForAllItemsPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddItemsSameConsigneeForAllItemsPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHowManyPackagesUserAnswersEntry: Arbitrary[(HowManyPackagesPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HowManyPackagesPage]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddAnotherPackageUserAnswersEntry: Arbitrary[(AddAnotherPackagePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddAnotherPackagePage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDeclareMarkUserAnswersEntry: Arbitrary[(DeclareMarkPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DeclareMarkPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddMarkUserAnswersEntry: Arbitrary[(AddMarkPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddMarkPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTotalPiecesUserAnswersEntry: Arbitrary[(TotalPiecesPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TotalPiecesPage]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDeclareNumberOfPackagesUserAnswersEntry: Arbitrary[(DeclareNumberOfPackagesPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DeclareNumberOfPackagesPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryCommodityCodeUserAnswersEntry: Arbitrary[(CommodityCodePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CommodityCodePage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddAnotherItemUserAnswersEntry: Arbitrary[(AddAnotherItemPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddAnotherItemPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTraderDetailsConsignorNameUserAnswersEntry: Arbitrary[(TraderDetailsConsignorNamePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TraderDetailsConsignorNamePage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTraderDetailsConsignorEoriNumberUserAnswersEntry: Arbitrary[(TraderDetailsConsignorEoriNumberPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TraderDetailsConsignorEoriNumberPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTraderDetailsConsignorEoriKnownUserAnswersEntry: Arbitrary[(TraderDetailsConsignorEoriKnownPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TraderDetailsConsignorEoriKnownPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTraderDetailsConsignorAddressUserAnswersEntry: Arbitrary[(TraderDetailsConsignorAddressPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TraderDetailsConsignorAddressPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTraderDetailsConsigneeNameUserAnswersEntry: Arbitrary[(TraderDetailsConsigneeNamePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TraderDetailsConsigneeNamePage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTraderDetailsConsigneeEoriNumberUserAnswersEntry: Arbitrary[(TraderDetailsConsigneeEoriNumberPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TraderDetailsConsigneeEoriNumberPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTraderDetailsConsigneeEoriKnownUserAnswersEntry: Arbitrary[(TraderDetailsConsigneeEoriKnownPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TraderDetailsConsigneeEoriKnownPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTraderDetailsConsigneeAddressUserAnswersEntry: Arbitrary[(TraderDetailsConsigneeAddressPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TraderDetailsConsigneeAddressPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPreLodgeDeclarationUserAnswersEntry: Arbitrary[(PreLodgeDeclarationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PreLodgeDeclarationPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTotalNetMassUserAnswersEntry: Arbitrary[(TotalNetMassPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TotalNetMassPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsCommodityCodeKnownUserAnswersEntry: Arbitrary[(IsCommodityCodeKnownPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsCommodityCodeKnownPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryItemTotalGrossMassUserAnswersEntry: Arbitrary[(ItemTotalGrossMassPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ItemTotalGrossMassPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddTotalNetMassUserAnswersEntry: Arbitrary[(AddTotalNetMassPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddTotalNetMassPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryItemDescriptionUserAnswersEntry: Arbitrary[(ItemDescriptionPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ItemDescriptionPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryOtherReferenceLiabiityAmountUserAnswersEntry: Arbitrary[(OtherReferenceLiabilityAmountPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[OtherReferenceLiabilityAmountPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAccessCodeUserAnswersEntry: Arbitrary[(AccessCodePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AccessCodePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryGuaranteeTypeUserAnswersEntry: Arbitrary[(GuaranteeTypePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[GuaranteeTypePage.type]
        value <- arbitrary[GuaranteeType].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryOtherReferenceUserAnswersEntry: Arbitrary[(OtherReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[OtherReferencePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryLiabilityAmountUserAnswersEntry: Arbitrary[(LiabilityAmountPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[LiabilityAmountPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryGuaranteeReferenceUserAnswersEntry: Arbitrary[(GuaranteeReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[GuaranteeReferencePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryConfirmRemoveSealsUserAnswersEntry: Arbitrary[(ConfirmRemoveSealsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConfirmRemoveSealsPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryConfirmRemoveOfficeOfTransitUserAnswersEntry: Arbitrary[(ConfirmRemoveOfficeOfTransitPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConfirmRemoveOfficeOfTransitPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySealsInformationUserAnswersEntry: Arbitrary[(SealsInformationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SealsInformationPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryControlResultDateLimitUserAnswersEntry: Arbitrary[(ControlResultDateLimitPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ControlResultDateLimitPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryArrivalTimesAtOfficeUserAnswersEntry: Arbitrary[(ArrivalTimesAtOfficePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ArrivalTimesAtOfficePage]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySealIdDetailsUserAnswersEntry: Arbitrary[(SealIdDetailsPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SealIdDetailsPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddSealsUserAnswersEntry: Arbitrary[(AddSealsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddSealsPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryCustomsApprovedLocationUserAnswersEntry: Arbitrary[(CustomsApprovedLocationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CustomsApprovedLocationPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddCustomsApprovedLocationUserAnswersEntry: Arbitrary[(AddCustomsApprovedLocationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddCustomsApprovedLocationPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTotalGrossMassUserAnswersEntry: Arbitrary[(TotalGrossMassPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TotalGrossMassPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAuthorisedLocationCodeUserAnswersEntry: Arbitrary[(AuthorisedLocationCodePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AuthorisedLocationCodePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTotalPackagesUserAnswersEntry: Arbitrary[(TotalPackagesPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TotalPackagesPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDeclarePackagesUserAnswersEntry: Arbitrary[(DeclarePackagesPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DeclarePackagesPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddAnotherTransitOfficeUserAnswersEntry: Arbitrary[(AddAnotherTransitOfficePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddAnotherTransitOfficePage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDestinationOfficeUserAnswersEntry: Arbitrary[(DestinationOfficePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DestinationOfficePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddTransitOfficeUserAnswersEntry: Arbitrary[(AddTransitOfficePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddTransitOfficePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryModeAtBorderUserAnswersEntry: Arbitrary[(ModeAtBorderPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ModeAtBorderPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryNationalityCrossingBorderUserAnswersEntry: Arbitrary[(NationalityCrossingBorderPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[NationalityCrossingBorderPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryModeCrossingBorderUserAnswersEntry: Arbitrary[(ModeCrossingBorderPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ModeCrossingBorderPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryInlandModeUserAnswersEntry: Arbitrary[(InlandModePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[InlandModePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddIdAtDepartureUserAnswersEntry: Arbitrary[(AddIdAtDeparturePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddIdAtDeparturePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIdCrossingBorderUserAnswersEntry: Arbitrary[(IdCrossingBorderPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IdCrossingBorderPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDestinationCountryUserAnswersEntry: Arbitrary[(DestinationCountryPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DestinationCountryPage.type]
        value <- stringsWithMaxLength(2).suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryChangeAtBorderUserAnswersEntry: Arbitrary[(ChangeAtBorderPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ChangeAtBorderPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryNationalityAtDepartureUserAnswersEntry: Arbitrary[(NationalityAtDeparturePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[NationalityAtDeparturePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIdAtDepartureUserAnswersEntry: Arbitrary[(IdAtDeparturePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IdAtDeparturePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPrincipalAddressUserAnswersEntry: Arbitrary[(PrincipalAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PrincipalAddressPage.type]
        value <- arbitrary[PrincipalAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryConsignorAddressUserAnswersEntry: Arbitrary[(ConsignorAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConsignorAddressPage.type]
        value <- arbitrary[ConsignorAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryOfficeOfDepartureUserAnswersEntry: Arbitrary[(OfficeOfDeparturePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[OfficeOfDeparturePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryConsigneeAddressUserAnswersEntry: Arbitrary[(ConsigneeAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConsigneeAddressPage.type]
        value <- arbitrary[ConsigneeAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryCountryOfDispatchUserAnswersEntry: Arbitrary[(CountryOfDispatchPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CountryOfDispatchPage.type]
        value <- stringsWithMaxLength(2).suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryConsigneeNameUserAnswersEntry: Arbitrary[(ConsigneeNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConsigneeNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsConsigneeEoriUserAnswersEntry: Arbitrary[(WhatIsConsigneeEoriPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsConsigneeEoriPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryConsignorNameUserAnswersEntry: Arbitrary[(ConsignorNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConsignorNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddConsigneeUserAnswersEntry: Arbitrary[(AddConsigneePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddConsigneePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsConsigneeEoriKnownUserAnswersEntry: Arbitrary[(IsConsigneeEoriKnownPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsConsigneeEoriKnownPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddConsignorUserAnswersEntry: Arbitrary[(AddConsignorPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddConsignorPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsConsignorEoriKnownUserAnswersEntry: Arbitrary[(IsConsignorEoriKnownPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsConsignorEoriKnownPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryConsignorEoriUserAnswersEntry: Arbitrary[(ConsignorEoriPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConsignorEoriPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPrincipalNameUserAnswersEntry: Arbitrary[(PrincipalNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PrincipalNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsPrincipalEoriKnownUserAnswersEntry: Arbitrary[(IsPrincipalEoriKnownPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsPrincipalEoriKnownPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsPrincipalEoriUserAnswersEntry: Arbitrary[(WhatIsPrincipalEoriPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsPrincipalEoriPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryRepresentativeCapacityUserAnswersEntry: Arbitrary[(RepresentativeCapacityPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[RepresentativeCapacityPage.type]
        value <- arbitrary[RepresentativeCapacity].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryRepresentativeNameUserAnswersEntry: Arbitrary[(RepresentativeNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[RepresentativeNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryContainersUsedPageUserAnswersEntry: Arbitrary[(ContainersUsedPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ContainersUsedPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDeclarationForSomeoneElseUserAnswersEntry: Arbitrary[(DeclarationForSomeoneElsePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DeclarationForSomeoneElsePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDeclarationPlaceUserAnswersEntry: Arbitrary[(DeclarationPlacePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DeclarationPlacePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryProcedureTypeUserAnswersEntry: Arbitrary[(ProcedureTypePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ProcedureTypePage.type]
        value <- arbitrary[ProcedureType].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDeclarationTypeUserAnswersEntry: Arbitrary[(DeclarationTypePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DeclarationTypePage.type]
        value <- arbitrary[DeclarationType].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddSecurityDetailsUserAnswersEntry: Arbitrary[(AddSecurityDetailsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddSecurityDetailsPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryLocalReferenceNumberUserAnswersEntry: Arbitrary[(LocalReferenceNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[LocalReferenceNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }
}
