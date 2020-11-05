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

import models.Index
import org.scalacheck.Arbitrary
import pages._
import pages.addItems._
import pages.addItems.specialMentions._
import pages.addItems.containers.{AddAnotherContainerPage, ContainerNumberPage}
import pages.addItems.traderDetails._
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage}
import pages.movementDetails.PreLodgeDeclarationPage

trait PageGenerators {

  implicit lazy val arbitraryRemoveSpecialMentionPage: Arbitrary[RemoveSpecialMentionPage] =
    Arbitrary(RemoveSpecialMentionPage(Index(0)))

  implicit lazy val arbitraryAddAnotherSpecialMentionPage: Arbitrary[AddAnotherSpecialMentionPage.type] =
    Arbitrary(AddAnotherSpecialMentionPage)

  implicit lazy val arbitrarySpecialMentionAdditionalInfoPage: Arbitrary[SpecialMentionAdditionalInfoPage.type] =
    Arbitrary(SpecialMentionAdditionalInfoPage)

  implicit lazy val arbitrarySpecialMentionTypePage: Arbitrary[SpecialMentionTypePage.type] =
    Arbitrary(SpecialMentionTypePage)

  implicit lazy val arbitraryAddSpecialMentionPage: Arbitrary[AddSpecialMentionPage] =
    Arbitrary(AddSpecialMentionPage(Index(0)))

  implicit lazy val arbitraryAddAnotherContainerPage: Arbitrary[AddAnotherContainerPage.type] =
    Arbitrary(AddAnotherContainerPage)

  implicit lazy val arbitraryContainerNumberPage: Arbitrary[ContainerNumberPage.type] =
    Arbitrary(ContainerNumberPage)

  implicit lazy val arbitraryAddAnotherPreviousAdministrativeReferencePage: Arbitrary[AddAnotherPreviousAdministrativeReferencePage] =
    Arbitrary(AddAnotherPreviousAdministrativeReferencePage(Index(0)))

  implicit lazy val arbitraryConfirmRemovePreviousAdministrativeReferencePage: Arbitrary[ConfirmRemovePreviousAdministrativeReferencePage] =
    Arbitrary(ConfirmRemovePreviousAdministrativeReferencePage(Index(0), Index(0)))

  implicit lazy val arbitraryExtraInformationPage: Arbitrary[ExtraInformationPage] =
    Arbitrary(ExtraInformationPage(Index(0), Index(0)))

  implicit lazy val arbitraryRemovePackagePage: Arbitrary[RemovePackagePage.type] =
    Arbitrary(RemovePackagePage)

  implicit lazy val arbitraryAddExtraInformationPage: Arbitrary[AddExtraInformationPage] =
    Arbitrary(AddExtraInformationPage(Index(0), Index(0)))

  implicit lazy val arbitraryConsignorForAllItemsPage: Arbitrary[ConsignorForAllItemsPage.type] =
    Arbitrary(ConsignorForAllItemsPage)

  implicit lazy val arbitraryAddItemsSameConsigneeForAllItemsPage: Arbitrary[AddItemsSameConsigneeForAllItemsPage] =
    Arbitrary(AddItemsSameConsigneeForAllItemsPage(Index(0)))

  implicit lazy val arbitraryReferenceTypePage: Arbitrary[ReferenceTypePage] =
    Arbitrary(ReferenceTypePage(Index(0), Index(0)))

  implicit lazy val arbitraryPreviousReferencePage: Arbitrary[PreviousReferencePage] =
    Arbitrary(addItems.PreviousReferencePage(Index(0), Index(0)))

  implicit lazy val arbitraryAddItemsSameConsignorForAllItemsPage: Arbitrary[AddItemsSameConsignorForAllItemsPage] =
    Arbitrary(addItems.AddItemsSameConsignorForAllItemsPage(Index(0)))

  implicit lazy val arbitraryAddAdministrativeReferencePage: Arbitrary[AddAdministrativeReferencePage] =
    Arbitrary(addItems.AddAdministrativeReferencePage(Index(0)))

  implicit lazy val arbitraryRemoveItemPage: Arbitrary[ConfirmRemoveItemPage.type] =
    Arbitrary(ConfirmRemoveItemPage)

  implicit lazy val arbitraryConsigneeForAllItemsPage: Arbitrary[ConsigneeForAllItemsPage.type] =
    Arbitrary(ConsigneeForAllItemsPage)

  implicit lazy val arbitraryHowManyPackagesPage: Arbitrary[HowManyPackagesPage] =
    Arbitrary(HowManyPackagesPage(Index(0), Index(0)))

  implicit lazy val arbitraryAddAnotherPackagePage: Arbitrary[AddAnotherPackagePage] =
    Arbitrary(AddAnotherPackagePage(Index(0)))

  implicit lazy val arbitraryDeclareMarkPage: Arbitrary[DeclareMarkPage] =
    Arbitrary(DeclareMarkPage(Index(0), Index(0)))

  implicit lazy val arbitraryAddMarkPage: Arbitrary[AddMarkPage] =
    Arbitrary(AddMarkPage(Index(0), Index(0)))

  implicit lazy val arbitraryTotalPiecesPage: Arbitrary[TotalPiecesPage] =
    Arbitrary(TotalPiecesPage(Index(0), Index(0)))

  implicit lazy val arbitraryDeclareNumberOfPackagesPage: Arbitrary[DeclareNumberOfPackagesPage] =
    Arbitrary(DeclareNumberOfPackagesPage(Index(0), Index(0)))

  implicit lazy val arbitraryCommodityCodePage: Arbitrary[CommodityCodePage] =
    Arbitrary(addItems.CommodityCodePage(Index(0)))

  implicit lazy val arbitraryAddAnotherItemPage: Arbitrary[AddAnotherItemPage.type] =
    Arbitrary(AddAnotherItemPage)

  implicit lazy val arbitraryTraderDetailsConsignorNamePage: Arbitrary[TraderDetailsConsignorNamePage] =
    Arbitrary(TraderDetailsConsignorNamePage(Index(0)))

  implicit lazy val arbitraryTraderDetailsConsignorEoriNumberPage: Arbitrary[TraderDetailsConsignorEoriNumberPage] =
    Arbitrary(TraderDetailsConsignorEoriNumberPage(Index(0)))

  implicit lazy val arbitraryTraderDetailsConsignorEoriKnownPage: Arbitrary[TraderDetailsConsignorEoriKnownPage] =
    Arbitrary(TraderDetailsConsignorEoriKnownPage(Index(0)))

  implicit lazy val arbitraryTraderDetailsConsignorAddressPage: Arbitrary[TraderDetailsConsignorAddressPage] =
    Arbitrary(TraderDetailsConsignorAddressPage(Index(0)))

  implicit lazy val arbitraryTraderDetailsConsigneeNamePage: Arbitrary[TraderDetailsConsigneeNamePage] =
    Arbitrary(TraderDetailsConsigneeNamePage(Index(0)))

  implicit lazy val arbitraryTraderDetailsConsigneeEoriNumberPage: Arbitrary[TraderDetailsConsigneeEoriNumberPage] =
    Arbitrary(TraderDetailsConsigneeEoriNumberPage(Index(0)))

  implicit lazy val arbitraryTraderDetailsConsigneeEoriKnownPage: Arbitrary[TraderDetailsConsigneeEoriKnownPage] =
    Arbitrary(TraderDetailsConsigneeEoriKnownPage(Index(0)))

  implicit lazy val arbitraryTraderDetailsConsigneeAddressPage: Arbitrary[TraderDetailsConsigneeAddressPage] =
    Arbitrary(TraderDetailsConsigneeAddressPage(Index(0)))

  implicit lazy val arbitraryPreLodgeDeclarationPage: Arbitrary[PreLodgeDeclarationPage.type] =
    Arbitrary(PreLodgeDeclarationPage)

  implicit lazy val arbitraryTotalNetMassPage: Arbitrary[TotalNetMassPage] =
    Arbitrary(TotalNetMassPage(Index(0)))

  implicit lazy val arbitraryIsCommodityCodeKnownPage: Arbitrary[IsCommodityCodeKnownPage] =
    Arbitrary(IsCommodityCodeKnownPage(Index(0)))

  implicit lazy val arbitraryAddTotalNetMassPage: Arbitrary[AddTotalNetMassPage] =
    Arbitrary(AddTotalNetMassPage(Index(0)))

  implicit lazy val arbitraryItemDescriptionPage: Arbitrary[ItemDescriptionPage] =
    Arbitrary(ItemDescriptionPage(Index(0)))

  implicit lazy val arbitraryItemTotalGrossMassPage: Arbitrary[ItemTotalGrossMassPage] =
    Arbitrary(ItemTotalGrossMassPage(Index(0)))

  implicit lazy val arbitraryOtherReferenceLiabiityAmountPage: Arbitrary[OtherReferenceLiabilityAmountPage.type] =
    Arbitrary(OtherReferenceLiabilityAmountPage)

  implicit lazy val arbitraryConfirmRemoveSealsPage: Arbitrary[ConfirmRemoveSealsPage.type] =
    Arbitrary(ConfirmRemoveSealsPage)

  implicit lazy val arbitraryAccessCodePage: Arbitrary[AccessCodePage.type] =
    Arbitrary(AccessCodePage)

  implicit lazy val arbitraryOtherReferencePage: Arbitrary[OtherReferencePage.type] =
    Arbitrary(OtherReferencePage)

  implicit lazy val arbitraryGuaranteeTypePage: Arbitrary[GuaranteeTypePage.type] =
    Arbitrary(GuaranteeTypePage)

  implicit lazy val arbitraryLiabilityAmountPage: Arbitrary[LiabilityAmountPage.type] =
    Arbitrary(LiabilityAmountPage)

  implicit lazy val arbitraryGuaranteeReferencePage: Arbitrary[GuaranteeReferencePage.type] =
    Arbitrary(GuaranteeReferencePage)

  implicit lazy val arbitraryConfirmRemoveOfficeOfTransitPage: Arbitrary[ConfirmRemoveOfficeOfTransitPage.type] =
    Arbitrary(ConfirmRemoveOfficeOfTransitPage)

  implicit lazy val arbitrarySealsInformationPage: Arbitrary[SealsInformationPage.type] =
    Arbitrary(SealsInformationPage)

  implicit lazy val arbitraryAddAnotherTransitOfficePage: Arbitrary[AddAnotherTransitOfficePage] =
    Arbitrary(AddAnotherTransitOfficePage(Index(0)))

  implicit lazy val arbitraryArrivalTimesAtOfficePage: Arbitrary[ArrivalTimesAtOfficePage] =
    Arbitrary(ArrivalTimesAtOfficePage(Index(0)))

  implicit lazy val arbitraryControlResultDateLimitPage: Arbitrary[ControlResultDateLimitPage.type] =
    Arbitrary(ControlResultDateLimitPage)

  implicit lazy val arbitrarySealIdDetailsPage: Arbitrary[SealIdDetailsPage] =
    Arbitrary(SealIdDetailsPage(Index(0)))

  implicit lazy val arbitraryAddSealsPage: Arbitrary[AddSealsPage.type] =
    Arbitrary(AddSealsPage)

  implicit lazy val arbitraryCustomsApprovedLocationPage: Arbitrary[CustomsApprovedLocationPage.type] =
    Arbitrary(CustomsApprovedLocationPage)

  implicit lazy val arbitraryAddCustomsApprovedLocationPage: Arbitrary[AddCustomsApprovedLocationPage.type] =
    Arbitrary(AddCustomsApprovedLocationPage)

  implicit lazy val arbitraryTotalGrossMassPage: Arbitrary[TotalGrossMassPage.type] =
    Arbitrary(TotalGrossMassPage)

  implicit lazy val arbitraryAuthorisedLocationCodePage: Arbitrary[AuthorisedLocationCodePage.type] =
    Arbitrary(AuthorisedLocationCodePage)

  implicit lazy val arbitraryTotalPackagesPage: Arbitrary[TotalPackagesPage.type] =
    Arbitrary(TotalPackagesPage)

  implicit lazy val arbitraryDeclarePackagesPage: Arbitrary[DeclarePackagesPage.type] =
    Arbitrary(DeclarePackagesPage)

  implicit lazy val arbitraryAddTransitOfficePage: Arbitrary[AddTransitOfficePage.type] =
    Arbitrary(AddTransitOfficePage)

  implicit lazy val arbitraryModeAtBorderPage: Arbitrary[ModeAtBorderPage.type] =
    Arbitrary(ModeAtBorderPage)

  implicit lazy val arbitraryNationalityCrossingBorderPage: Arbitrary[NationalityCrossingBorderPage.type] =
    Arbitrary(NationalityCrossingBorderPage)

  implicit lazy val arbitraryModeCrossingBorderPage: Arbitrary[ModeCrossingBorderPage.type] =
    Arbitrary(ModeCrossingBorderPage)

  implicit lazy val arbitraryInlandModePage: Arbitrary[InlandModePage.type] =
    Arbitrary(InlandModePage)

  implicit lazy val arbitraryDestinationOfficePage: Arbitrary[DestinationOfficePage.type] =
    Arbitrary(DestinationOfficePage)

  implicit lazy val arbitraryAddIdAtDeparturePage: Arbitrary[AddIdAtDeparturePage.type] =
    Arbitrary(AddIdAtDeparturePage)

  implicit lazy val arbitraryIdCrossingBorderPage: Arbitrary[IdCrossingBorderPage.type] =
    Arbitrary(IdCrossingBorderPage)

  implicit lazy val arbitraryDestinationCountryPage: Arbitrary[DestinationCountryPage.type] =
    Arbitrary(DestinationCountryPage)

  implicit lazy val arbitraryChangeAtBorderPage: Arbitrary[ChangeAtBorderPage.type] =
    Arbitrary(ChangeAtBorderPage)

  implicit lazy val arbitraryNationalityAtDeparturePage: Arbitrary[NationalityAtDeparturePage.type] =
    Arbitrary(NationalityAtDeparturePage)

  implicit lazy val arbitraryIdAtDeparturePage: Arbitrary[IdAtDeparturePage.type] =
    Arbitrary(IdAtDeparturePage)

  implicit lazy val arbitraryOfficeOfDeparturePage: Arbitrary[OfficeOfDeparturePage.type] =
    Arbitrary(OfficeOfDeparturePage)

  implicit lazy val arbitraryConsigneeAddressPage: Arbitrary[ConsigneeAddressPage.type] =
    Arbitrary(ConsigneeAddressPage)

  implicit lazy val arbitraryPrincipalAddressPage: Arbitrary[PrincipalAddressPage.type] =
    Arbitrary(PrincipalAddressPage)

  implicit lazy val arbitraryConsigneeNamePage: Arbitrary[ConsigneeNamePage.type] =
    Arbitrary(ConsigneeNamePage)

  implicit lazy val arbitraryWhatIsConsigneeEoriPage: Arbitrary[WhatIsConsigneeEoriPage.type] =
    Arbitrary(WhatIsConsigneeEoriPage)

  implicit lazy val arbitraryCountryOfDispatchPage: Arbitrary[CountryOfDispatchPage.type] =
    Arbitrary(CountryOfDispatchPage)

  implicit lazy val arbitraryIsConsigneeEoriKnownPage: Arbitrary[IsConsigneeEoriKnownPage.type] =
    Arbitrary(IsConsigneeEoriKnownPage)

  implicit lazy val arbitraryAddConsigneePage: Arbitrary[AddConsigneePage.type] =
    Arbitrary(AddConsigneePage)

  implicit lazy val arbitraryConsignorAddressPage: Arbitrary[ConsignorAddressPage.type] =
    Arbitrary(ConsignorAddressPage)

  implicit lazy val arbitraryConsignorEoriPage: Arbitrary[ConsignorEoriPage.type] =
    Arbitrary(ConsignorEoriPage)

  implicit lazy val arbitraryConsignorNamePage: Arbitrary[ConsignorNamePage.type] =
    Arbitrary(ConsignorNamePage)

  implicit lazy val arbitraryIsConsignorEoriKnownPage: Arbitrary[IsConsignorEoriKnownPage.type] =
    Arbitrary(IsConsignorEoriKnownPage)

  implicit lazy val arbitraryAddConsignorPage: Arbitrary[AddConsignorPage.type] =
    Arbitrary(AddConsignorPage)

  implicit lazy val arbitraryPrincipalNamePage: Arbitrary[PrincipalNamePage.type] =
    Arbitrary(PrincipalNamePage)

  implicit lazy val arbitraryIsPrincipalEoriKnownPage: Arbitrary[IsPrincipalEoriKnownPage.type] =
    Arbitrary(IsPrincipalEoriKnownPage)

  implicit lazy val arbitraryWhatIsPrincipalEoriPage: Arbitrary[WhatIsPrincipalEoriPage.type] =
    Arbitrary(WhatIsPrincipalEoriPage)

  implicit lazy val arbitraryRepresentativeCapacityPage: Arbitrary[RepresentativeCapacityPage.type] =
    Arbitrary(RepresentativeCapacityPage)

  implicit lazy val arbitraryRepresentativeNamePage: Arbitrary[RepresentativeNamePage.type] =
    Arbitrary(RepresentativeNamePage)

  implicit lazy val arbitraryContainersUsedPage: Arbitrary[ContainersUsedPage.type] =
    Arbitrary(ContainersUsedPage)

  implicit lazy val arbitraryDeclarationForSomeoneElsePage: Arbitrary[DeclarationForSomeoneElsePage.type] =
    Arbitrary(DeclarationForSomeoneElsePage)

  implicit lazy val arbitraryDeclarationPlacePage: Arbitrary[DeclarationPlacePage.type] =
    Arbitrary(DeclarationPlacePage)

  implicit lazy val arbitraryProcedureTypePage: Arbitrary[ProcedureTypePage.type] =
    Arbitrary(ProcedureTypePage)

  implicit lazy val arbitraryDeclarationTypePage: Arbitrary[DeclarationTypePage.type] =
    Arbitrary(DeclarationTypePage)

  implicit lazy val arbitraryAddSecurityDetailsPage: Arbitrary[AddSecurityDetailsPage.type] =
    Arbitrary(AddSecurityDetailsPage)

  implicit lazy val arbitraryLocalReferenceNumberPage: Arbitrary[LocalReferenceNumberPage.type] =
    Arbitrary(LocalReferenceNumberPage)
}
