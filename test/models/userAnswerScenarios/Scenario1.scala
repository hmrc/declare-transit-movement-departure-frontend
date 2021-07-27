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

package models.userAnswerScenarios

import cats.data.NonEmptyList
import models.DeclarationType.Option2
import models.ProcedureType.Normal
import models.RepresentativeCapacity.Direct
import models.domain.SealDomain
import models.journeyDomain.GoodsSummary.GoodSummaryNormalDetailsWithoutPreLodge
import models.journeyDomain.GuaranteeDetails.GuaranteeReference
import models.journeyDomain.ItemTraderDetails.RequiredDetails
import models.journeyDomain.MovementDetails.{DeclarationForSomeoneElse, NormalMovementDetails}
import models.journeyDomain.Packages.{BulkPackages, OtherPackages, UnpackedPackages}
import models.journeyDomain.RouteDetailsWithTransitInformation.TransitInformation
import models.journeyDomain.SafetyAndSecurity.TraderEori
import models.journeyDomain.TransportDetails.DetailsAtBorder.SameDetailsAtBorder
import models.journeyDomain.TransportDetails.InlandMode.NonSpecialMode
import models.journeyDomain.addItems.{ItemsSecurityTraderDetails, SecurityPersonalInformation, SecurityTraderEori}
import models.journeyDomain.traderDetails.{PrincipalTraderPersonalInfo, TraderDetails}
import models.journeyDomain.{
  Container,
  CurrencyCode,
  DefaultLiabilityAmount,
  GoodsSummary,
  ItemDetails,
  ItemSection,
  Itinerary,
  JourneyDomain,
  OtherLiabilityAmount,
  PreTaskListDetails,
  PreviousReferences,
  RouteDetailsWithTransitInformation,
  SafetyAndSecurity,
  SpecialMentionDomain,
  StandardDocument,
  TransportDetails
}
import models.reference._
import models.{CommonAddress, DeclarationType, EoriNumber, GuaranteeType, Index, LocalReferenceNumber, ProcedureType, RepresentativeCapacity, UserAnswers}
import play.api.libs.json.Json
import java.time.LocalDateTime

case object Scenario1 extends UserAnswerScenario {

  private val firstGoodItem: Index      = Index(0)
  private val secondGoodItem: Index     = Index(1)
  private val eoriNumber: EoriNumber    = EoriNumber("EoriNumber")
  private val lrn: LocalReferenceNumber = LocalReferenceNumber("ABCD1234567890123").get

  val userAnswers: UserAnswers = UserAnswers(lrn, eoriNumber, Json.obj())
    .unsafeSetVal(pages.ProcedureTypePage)(ProcedureType.Normal)
    .unsafeSetVal(pages.AddSecurityDetailsPage)(true)
    .unsafeSetVal(pages.OfficeOfDeparturePage)(CustomsOffice("OOD1234A", "OfficeOfDeparturePage", CountryCode("GB"), None))
    /*
     * General Information Section
     * */
    .unsafeSetVal(pages.DeclarationTypePage)(DeclarationType.Option2)
    .unsafeSetVal(pages.movementDetails.PreLodgeDeclarationPage)(false)
    .unsafeSetVal(pages.ContainersUsedPage)(true)
    .unsafeSetVal(pages.DeclarationPlacePage)("XX1 1XX")
    .unsafeSetVal(pages.DeclarationForSomeoneElsePage)(true)
    .unsafeSetVal(pages.RepresentativeNamePage)("John Doe")
    .unsafeSetVal(pages.RepresentativeCapacityPage)(RepresentativeCapacity.Direct)
    /*
     * RouteDetails
     * */
    .unsafeSetVal(pages.CountryOfDispatchPage)(CountryOfDispatch(CountryCode("SC"), false))
    .unsafeSetVal(pages.DestinationCountryPage)(CountryCode("DC"))
    .unsafeSetVal(pages.MovementDestinationCountryPage)(CountryCode("MD"))
    .unsafeSetVal(pages.DestinationOfficePage)(CustomsOffice("DOP1234A", "DestinationOfficePage", CountryCode("DO"), None))
    .unsafeSetVal(pages.OfficeOfTransitCountryPage(Index(0)))(CountryCode("OT1"))
    .unsafeSetVal(pages.AddAnotherTransitOfficePage(Index(0)))("TOP12341")
    .unsafeSetVal(pages.ArrivalTimesAtOfficePage(Index(0)))(LocalDateTime.of(2020, 5, 5, 5, 12))
    .unsafeSetVal(pages.AddTransitOfficePage)(true)
    .unsafeSetVal(pages.OfficeOfTransitCountryPage(Index(1)))(CountryCode("OT2"))
    .unsafeSetVal(pages.AddAnotherTransitOfficePage(Index(1)))("TOP12342")
    .unsafeSetVal(pages.ArrivalTimesAtOfficePage(Index(1)))(LocalDateTime.of(2020, 5, 7, 21, 12))
    .unsafeSetVal(pages.AddTransitOfficePage)(false)
    /*
     * Transport Details
     * */
    .unsafeSetVal(pages.InlandModePage)("4")
    .unsafeSetVal(pages.AddIdAtDeparturePage)(false)
    .unsafeSetVal(pages.AddNationalityAtDeparturePage)(true)
    .unsafeSetVal(pages.NationalityAtDeparturePage)(CountryCode("ND"))
    .unsafeSetVal(pages.ChangeAtBorderPage)(false)
    /*
     * Traders Details
     * */
    .unsafeSetVal(pages.IsPrincipalEoriKnownPage)(false)
    .unsafeSetVal(pages.PrincipalNamePage)("PrincipalName")
    .unsafeSetVal(pages.PrincipalAddressPage)(CommonAddress("PrincipalStreet", "PrincipalTown", "AA1 1AA", Country(CountryCode("FR"), "France")))
    .unsafeSetVal(pages.AddConsignorPage)(false)
    .unsafeSetVal(pages.AddConsigneePage)(false)
    /*
     * Safety & Security Details
     * */
    .unsafeSetVal(pages.safetyAndSecurity.AddCircumstanceIndicatorPage)(true)
    .unsafeSetVal(pages.safetyAndSecurity.CircumstanceIndicatorPage)("A")
    .unsafeSetVal(pages.safetyAndSecurity.AddTransportChargesPaymentMethodPage)(false)
    .unsafeSetVal(pages.safetyAndSecurity.AddCommercialReferenceNumberPage)(true)
    .unsafeSetVal(pages.safetyAndSecurity.AddCommercialReferenceNumberAllItemsPage)(false)
    .unsafeSetVal(pages.safetyAndSecurity.AddConveyanceReferenceNumberPage)(true)
    .unsafeSetVal(pages.safetyAndSecurity.ConveyanceReferenceNumberPage)("SomeConv")
    .unsafeSetVal(pages.safetyAndSecurity.PlaceOfUnloadingCodePage)("PlaceOfUnloadingPage")
    .unsafeSetVal(pages.safetyAndSecurity.CountryOfRoutingPage(Index(0)))(CountryCode("CA"))
    .unsafeSetVal(pages.safetyAndSecurity.AddAnotherCountryOfRoutingPage)(true)
    .unsafeSetVal(pages.safetyAndSecurity.CountryOfRoutingPage(Index(1)))(CountryCode("CB"))
    .unsafeSetVal(pages.safetyAndSecurity.AddAnotherCountryOfRoutingPage)(false)
    .unsafeSetVal(pages.safetyAndSecurity.AddSafetyAndSecurityConsigneePage)(false)
    .unsafeSetVal(pages.safetyAndSecurity.AddSafetyAndSecurityConsignorPage)(false)
    .unsafeSetVal(pages.safetyAndSecurity.AddCarrierPage)(true)
    .unsafeSetVal(pages.safetyAndSecurity.AddCarrierEoriPage)(true)
    .unsafeSetVal(pages.safetyAndSecurity.CarrierEoriPage)("CarrierEori")
    /*
     * Item Details section - Item One
     * */
    .unsafeSetVal(pages.ItemDescriptionPage(firstGoodItem))("ItemOnesDescription")
    .unsafeSetVal(pages.ItemTotalGrossMassPage(firstGoodItem))(25000.000)
    .unsafeSetVal(pages.AddTotalNetMassPage(firstGoodItem))(true)
    .unsafeSetVal(pages.TotalNetMassPage(firstGoodItem))("12342")
    .unsafeSetVal(pages.IsCommodityCodeKnownPage(firstGoodItem))(true)
    .unsafeSetVal(pages.addItems.CommodityCodePage(firstGoodItem))("ComoCode1")
    .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorEoriKnownPage(firstGoodItem))(true)
    .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorEoriNumberPage(firstGoodItem))("Conor123")
    .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorNamePage(firstGoodItem))("ConorName")
    .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorAddressPage(firstGoodItem))(
      CommonAddress("ConorLine1", "ConorLine2", "ConorL3", Country(CountryCode("GA"), "SomethingCO"))
    )
    .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeEoriKnownPage(firstGoodItem))(true)
    .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeEoriNumberPage(firstGoodItem))("Conee123")
    .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeNamePage(firstGoodItem))("ConeeName")
    .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeAddressPage(firstGoodItem))(
      CommonAddress("ConeeLine1", "ConeeLine2", "ConeeL3", Country(CountryCode("GA"), "SomethingCE"))
    )
    .unsafeSetVal(pages.PackageTypePage(firstGoodItem, Index(0)))(PackageType(PackageType.bulkCodes.head, "GD1PKG1"))
    .unsafeSetVal(pages.addItems.DeclareNumberOfPackagesPage(firstGoodItem, Index(0)))(false)
    .unsafeSetVal(pages.addItems.AddMarkPage(firstGoodItem, Index(0)))(false)
    .unsafeSetVal(pages.addItems.AddAnotherPackagePage(firstGoodItem))(true)
    .unsafeSetVal(pages.PackageTypePage(firstGoodItem, Index(1)))(PackageType(PackageType.unpackedCodes.head, "GD1PKG2"))
    .unsafeSetVal(pages.addItems.DeclareNumberOfPackagesPage(firstGoodItem, Index(1)))(false)
    .unsafeSetVal(pages.addItems.TotalPiecesPage(firstGoodItem, Index(1)))(12)
    .unsafeSetVal(pages.addItems.AddMarkPage(firstGoodItem, Index(1)))(true)
    .unsafeSetVal(pages.addItems.DeclareMarkPage(firstGoodItem, Index(1)))("GD1PK2MK")
    .unsafeSetVal(pages.addItems.AddAnotherPackagePage(firstGoodItem))(true)
    .unsafeSetVal(pages.PackageTypePage(firstGoodItem, Index(2)))(PackageType("BAG", "GD1PKG3"))
    .unsafeSetVal(pages.addItems.HowManyPackagesPage(firstGoodItem, Index(2)))(2)
    .unsafeSetVal(pages.addItems.DeclareMarkPage(firstGoodItem, Index(2)))("GD1PK3MK")
    .unsafeSetVal(pages.addItems.containers.ContainerNumberPage(firstGoodItem, Index(0)))("GD1CN1NUM1")
    .unsafeSetVal(pages.addItems.containers.ContainerNumberPage(firstGoodItem, Index(1)))("GD1CN2NUMS")
    .unsafeSetVal(pages.addItems.specialMentions.AddSpecialMentionPage(firstGoodItem))(true)
    .unsafeSetVal(pages.addItems.specialMentions.SpecialMentionTypePage(firstGoodItem, Index(0)))("GD1S1")
    .unsafeSetVal(pages.addItems.specialMentions.SpecialMentionAdditionalInfoPage(firstGoodItem, Index(0)))("GD1SPMT1Info")
    .unsafeSetVal(pages.addItems.specialMentions.AddAnotherSpecialMentionPage(firstGoodItem))(true)
    .unsafeSetVal(pages.addItems.specialMentions.SpecialMentionTypePage(firstGoodItem, Index(1)))("DG0")
    .unsafeSetVal(pages.addItems.specialMentions.SpecialMentionAdditionalInfoPage(firstGoodItem, Index(1)))("GD1S2Info")
    .unsafeSetVal(pages.addItems.specialMentions.AddAnotherSpecialMentionPage(firstGoodItem))(false)
    .unsafeSetVal(pages.addItems.AddDocumentsPage(firstGoodItem))(true)
    .unsafeSetVal(pages.addItems.DocumentTypePage(firstGoodItem, Index(0)))("G1D1")
    .unsafeSetVal(pages.addItems.DocumentReferencePage(firstGoodItem, Index(0)))("G1D1Ref")
    .unsafeSetVal(pages.addItems.AddExtraDocumentInformationPage(firstGoodItem, Index(0)))(true)
    .unsafeSetVal(pages.addItems.DocumentExtraInformationPage(firstGoodItem, Index(0)))("G1D1Info")
    .unsafeSetVal(pages.addItems.AddAnotherDocumentPage(firstGoodItem))(true)
    .unsafeSetVal(pages.addItems.DocumentTypePage(firstGoodItem, Index(1)))("G1D2")
    .unsafeSetVal(pages.addItems.DocumentReferencePage(firstGoodItem, Index(1)))("G1D2Ref")
    .unsafeSetVal(pages.addItems.AddExtraDocumentInformationPage(firstGoodItem, Index(1)))(false)
    .unsafeSetVal(pages.addItems.AddAnotherDocumentPage(firstGoodItem))(false)
    .unsafeSetVal(pages.addItems.AddAdministrativeReferencePage(firstGoodItem))(true)
    .unsafeSetVal(pages.addItems.ReferenceTypePage(firstGoodItem, Index(0)))("GD1PR1")
    .unsafeSetVal(pages.addItems.PreviousReferencePage(firstGoodItem, Index(0)))("GD1PR1Ref")
    .unsafeSetVal(pages.addItems.AddExtraInformationPage(firstGoodItem, Index(0)))(true)
    .unsafeSetVal(pages.addItems.ExtraInformationPage(firstGoodItem, Index(0)))("GD1PR1Info")
    .unsafeSetVal(pages.addItems.AddAnotherPreviousAdministrativeReferencePage(firstGoodItem))(true)
    .unsafeSetVal(pages.addItems.ReferenceTypePage(firstGoodItem, Index(1)))("GD1PR2")
    .unsafeSetVal(pages.addItems.PreviousReferencePage(firstGoodItem, Index(1)))("GD1PR2Ref")
    .unsafeSetVal(pages.addItems.IsNonEuOfficePage)(false)
    .unsafeSetVal(pages.addItems.AddExtraInformationPage(firstGoodItem, Index(1)))(false)
    .unsafeSetVal(pages.addItems.AddAnotherPreviousAdministrativeReferencePage(firstGoodItem))(false)
    .unsafeSetVal(pages.addItems.securityDetails.TransportChargesPage(firstGoodItem))("T")
    .unsafeSetVal(pages.addItems.securityDetails.CommercialReferenceNumberPage(firstGoodItem))("GD1CRN")
    .unsafeSetVal(pages.addItems.securityDetails.AddDangerousGoodsCodePage(firstGoodItem))(true)
    .unsafeSetVal(pages.addItems.securityDetails.DangerousGoodsCodePage(firstGoodItem))("GD1C")
    .unsafeSetVal(pages.addItems.traderSecurityDetails.AddSecurityConsignorsEoriPage(firstGoodItem))(true)
    .unsafeSetVal(pages.addItems.traderSecurityDetails.SecurityConsignorEoriPage(firstGoodItem))("GD1SECCONOR")
    .unsafeSetVal(pages.addItems.traderSecurityDetails.AddSecurityConsigneesEoriPage(firstGoodItem))(true)
    .unsafeSetVal(pages.addItems.traderSecurityDetails.SecurityConsigneeEoriPage(firstGoodItem))("GD1SECCONEE")
    .unsafeSetVal(pages.addItems.AddAnotherItemPage)(true)
    /*
     * Item Details section - Item Two
     * */
    .unsafeSetVal(pages.ItemDescriptionPage(secondGoodItem))("ItemTwosDescription")
    .unsafeSetVal(pages.ItemTotalGrossMassPage(secondGoodItem))(12345.000)
    .unsafeSetVal(pages.AddTotalNetMassPage(secondGoodItem))(false)
    .unsafeSetVal(pages.IsCommodityCodeKnownPage(secondGoodItem))(false)
    .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorEoriKnownPage(secondGoodItem))(false)
    .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorNamePage(secondGoodItem))("ConorName")
    .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorAddressPage(secondGoodItem))(
      CommonAddress("ConorLine1", "ConorLine2", "ConorL3", Country(CountryCode("GB"), "SomethingCO"))
    )
    .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeEoriKnownPage(secondGoodItem))(false)
    .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeNamePage(secondGoodItem))("ConeeName")
    .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeAddressPage(secondGoodItem))(
      CommonAddress("ConeeLine1", "ConeeLine2", "ConeeL3", Country(CountryCode("GB"), "SomethingCE"))
    )
    .unsafeSetVal(pages.PackageTypePage(secondGoodItem, Index(0)))(PackageType(PackageType.bulkCodes.head, "GD2PKG1"))
    .unsafeSetVal(pages.addItems.DeclareNumberOfPackagesPage(secondGoodItem, Index(0)))(false)
    .unsafeSetVal(pages.addItems.AddMarkPage(secondGoodItem, Index(0)))(false)
    .unsafeSetVal(pages.addItems.AddAnotherPackagePage(secondGoodItem))(true)
    .unsafeSetVal(pages.PackageTypePage(secondGoodItem, Index(1)))(PackageType(PackageType.unpackedCodes.head, "GD2PKG2"))
    .unsafeSetVal(pages.addItems.DeclareNumberOfPackagesPage(secondGoodItem, Index(1)))(true)
    .unsafeSetVal(pages.addItems.HowManyPackagesPage(secondGoodItem, Index(1)))(23)
    .unsafeSetVal(pages.addItems.TotalPiecesPage(secondGoodItem, Index(1)))(12)
    .unsafeSetVal(pages.addItems.AddMarkPage(secondGoodItem, Index(1)))(true)
    .unsafeSetVal(pages.addItems.DeclareMarkPage(secondGoodItem, Index(1)))("GD2PK2MK")
    .unsafeSetVal(pages.addItems.AddAnotherPackagePage(secondGoodItem))(true)
    .unsafeSetVal(pages.PackageTypePage(secondGoodItem, Index(2)))(PackageType("BAG", "GD2PKG3"))
    .unsafeSetVal(pages.addItems.HowManyPackagesPage(secondGoodItem, Index(2)))(2)
    .unsafeSetVal(pages.addItems.DeclareMarkPage(secondGoodItem, Index(2)))("GD2PK3MK")
    .unsafeSetVal(pages.addItems.containers.ContainerNumberPage(secondGoodItem, Index(0)))("GD2CN1NUM1")
    .unsafeSetVal(pages.addItems.specialMentions.AddSpecialMentionPage(secondGoodItem))(true)
    .unsafeSetVal(pages.addItems.specialMentions.SpecialMentionTypePage(secondGoodItem, Index(0)))("GD2S1")
    .unsafeSetVal(pages.addItems.specialMentions.SpecialMentionAdditionalInfoPage(secondGoodItem, Index(0)))("GD2S1Info")
    .unsafeSetVal(pages.addItems.specialMentions.AddAnotherSpecialMentionPage(secondGoodItem))(false)
    .unsafeSetVal(pages.addItems.AddDocumentsPage(secondGoodItem))(true)
    .unsafeSetVal(pages.addItems.DocumentTypePage(secondGoodItem, Index(0)))("G2D1")
    .unsafeSetVal(pages.addItems.DocumentReferencePage(secondGoodItem, Index(0)))("G2D1Ref")
    .unsafeSetVal(pages.addItems.AddExtraDocumentInformationPage(secondGoodItem, Index(0)))(true)
    .unsafeSetVal(pages.addItems.DocumentExtraInformationPage(secondGoodItem, Index(0)))("G2D1Info")
    .unsafeSetVal(pages.addItems.AddAnotherDocumentPage(secondGoodItem))(false)
    .unsafeSetVal(pages.addItems.AddAdministrativeReferencePage(secondGoodItem))(true)
    .unsafeSetVal(pages.addItems.ReferenceTypePage(secondGoodItem, Index(0)))("GD2PR1")
    .unsafeSetVal(pages.addItems.PreviousReferencePage(secondGoodItem, Index(0)))("GD2PR1Ref")
    .unsafeSetVal(pages.addItems.AddExtraInformationPage(secondGoodItem, Index(0)))(true)
    .unsafeSetVal(pages.addItems.ExtraInformationPage(secondGoodItem, Index(0)))("GD2PR1Info")
    .unsafeSetVal(pages.addItems.AddAnotherPreviousAdministrativeReferencePage(secondGoodItem))(false)
    .unsafeSetVal(pages.addItems.securityDetails.TransportChargesPage(secondGoodItem))("U")
    .unsafeSetVal(pages.addItems.securityDetails.CommercialReferenceNumberPage(secondGoodItem))("GD2CRN")
    .unsafeSetVal(pages.addItems.securityDetails.AddDangerousGoodsCodePage(secondGoodItem))(false)
    .unsafeSetVal(pages.addItems.traderSecurityDetails.AddSecurityConsignorsEoriPage(secondGoodItem))(false)
    .unsafeSetVal(pages.addItems.traderSecurityDetails.SecurityConsignorNamePage(secondGoodItem))("GD2SECCONORName")
    .unsafeSetVal(pages.addItems.traderSecurityDetails.SecurityConsignorAddressPage(secondGoodItem))(
      CommonAddress("GD2CONORL1", "GD2CONORL2", "GD2CONL1", Country(CountryCode("GB"), "GD2CONNOR"))
    )
    .unsafeSetVal(pages.addItems.traderSecurityDetails.AddSecurityConsigneesEoriPage(secondGoodItem))(false)
    .unsafeSetVal(pages.addItems.traderSecurityDetails.SecurityConsigneeNamePage(secondGoodItem))("GD2SECCONEEName")
    .unsafeSetVal(pages.addItems.traderSecurityDetails.SecurityConsigneeAddressPage(secondGoodItem))(
      CommonAddress("GD2CONEEL1", "GD2CONEEL2", "GD2CEEL1", Country(CountryCode("GB"), "GD2CONNEE"))
    )
    .unsafeSetVal(pages.addItems.AddAnotherItemPage)(false)
    /*
     * Goods Summary
     */
    .unsafeSetVal(pages.TotalPackagesPage)(1)
    .unsafeSetVal(pages.LoadingPlacePage)("LoadPLace")
    .unsafeSetVal(pages.AddCustomsApprovedLocationPage)(true)
    .unsafeSetVal(pages.CustomsApprovedLocationPage)("CUSAPPLOC")
    .unsafeSetVal(pages.AddSealsPage)(true)
    .unsafeSetVal(pages.SealIdDetailsPage(Index(0)))(SealDomain("SEAL1"))
    .unsafeSetVal(pages.SealsInformationPage)(true)
    .unsafeSetVal(pages.SealIdDetailsPage(Index(1)))(SealDomain("SEAL2"))
    /*
     * guarantee Details
     */
    .unsafeSetVal(pages.guaranteeDetails.GuaranteeTypePage(Index(0)))(GuaranteeType.ComprehensiveGuarantee)
    .unsafeSetVal(pages.guaranteeDetails.GuaranteeReferencePage(Index(0)))("GUA1Ref")
    .unsafeSetVal(pages.DefaultAmountPage(Index(0)))(true)
    .unsafeSetVal(pages.AccessCodePage(Index(0)))("1234")
    .unsafeSetVal(pages.AddAnotherGuaranteePage)(true)
    .unsafeSetVal(pages.guaranteeDetails.GuaranteeTypePage(Index(1)))(GuaranteeType.GuaranteeWaiver)
    .unsafeSetVal(pages.guaranteeDetails.GuaranteeReferencePage(Index(1)))("GUA2Ref")
    .unsafeSetVal(pages.LiabilityAmountPage(Index(1)))("500")
    .unsafeSetVal(pages.AccessCodePage(Index(1)))("4321")

  private val preTaskListDetails =
    PreTaskListDetails(lrn, Normal, CustomsOffice("OOD1234A", "OfficeOfDeparturePage", CountryCode("GB"), None), Option2, true)

  private val movementDetails = NormalMovementDetails(false, true, "XX1 1XX", DeclarationForSomeoneElse("John Doe", Direct))

  private val routeDetails = RouteDetailsWithTransitInformation(
    CountryOfDispatch(CountryCode("SC"), false),
    CountryCode("DC"),
    CustomsOffice("DOP1234A", "DestinationOfficePage", CountryCode("DO"), None),
    Some(
      NonEmptyList(TransitInformation("TOP12341", Some(LocalDateTime.of(2020, 5, 5, 5, 12))),
                   List(TransitInformation("TOP12342", Some(LocalDateTime.of(2020, 5, 7, 21, 12))))
      )
    )
  )

  private val transportDetails = TransportDetails(NonSpecialMode(4, Some(CountryCode("ND")), None), SameDetailsAtBorder)

  private val traderDetails = TraderDetails(
    PrincipalTraderPersonalInfo("PrincipalName", CommonAddress("PrincipalStreet", "PrincipalTown", "AA1 1AA", Country(CountryCode("FR"), "France")), None),
    None,
    None
  )

  private val item1 = ItemSection(
    itemDetails = ItemDetails("ItemOnesDescription", "25000.000", Some("12342"), Some("ComoCode1")),
    consignor = Some(
      RequiredDetails("ConorName",
                      CommonAddress("ConorLine1", "ConorLine2", "ConorL3", Country(CountryCode("GA"), "SomethingCO")),
                      Some(EoriNumber("Conor123"))
      )
    ),
    consignee = Some(
      RequiredDetails("ConeeName",
                      CommonAddress("ConeeLine1", "ConeeLine2", "ConeeL3", Country(CountryCode("GA"), "SomethingCE")),
                      Some(EoriNumber("Conee123"))
      )
    ),
    packages = NonEmptyList(
      BulkPackages(PackageType("VQ", "GD1PKG1"), None),
      List(UnpackedPackages(PackageType("NE", "GD1PKG2"), 12, Some("GD1PK2MK")), OtherPackages(PackageType("BAG", "GD1PKG3"), 2, "GD1PK3MK"))
    ),
    containers = Some(NonEmptyList(Container("GD1CN1NUM1"), List(Container("GD1CN2NUMS")))),
    specialMentions = Some(
      NonEmptyList(
        SpecialMentionDomain("GD1S1", "GD1SPMT1Info", CustomsOffice("OOD1234A", "OfficeOfDeparturePage", CountryCode("GB"), None)),
        List(SpecialMentionDomain("DG0", "GD1S2Info", CustomsOffice("OOD1234A", "OfficeOfDeparturePage", CountryCode("GB"), None)))
      )
    ),
    producedDocuments = Some(
      NonEmptyList(
        StandardDocument("G1D1", "G1D1Ref", Some("G1D1Info")),
        List(StandardDocument("G1D2", "G1D2Ref", None))
      )
    ),
    itemSecurityTraderDetails = Some(
      ItemsSecurityTraderDetails(Some("T"),
                                 Some("GD1CRN"),
                                 Some("GD1C"),
                                 Some(SecurityTraderEori(EoriNumber("GD1SECCONOR"))),
                                 Some(SecurityTraderEori(EoriNumber("GD1SECCONEE")))
      )
    ),
    previousReferences = Some(
      NonEmptyList(
        PreviousReferences("GD1PR1", "GD1PR1Ref", Some("GD1PR1Info")),
        List(PreviousReferences("GD1PR2", "GD1PR2Ref", None))
      )
    )
  )

  val item2 = ItemSection(
    itemDetails = ItemDetails("ItemTwosDescription", "12345.000", None, None),
    consignor = Some(RequiredDetails("ConorName", CommonAddress("ConorLine1", "ConorLine2", "ConorL3", Country(CountryCode("GB"), "SomethingCO")), None)),
    consignee = Some(RequiredDetails("ConeeName", CommonAddress("ConeeLine1", "ConeeLine2", "ConeeL3", Country(CountryCode("GB"), "SomethingCE")), None)),
    packages = NonEmptyList(
      BulkPackages(PackageType("VQ", "GD2PKG1"), None),
      List(UnpackedPackages(PackageType("NE", "GD2PKG2"), 12, Some("GD2PK2MK")), OtherPackages(PackageType("BAG", "GD2PKG3"), 2, "GD2PK3MK"))
    ),
    containers = Some(NonEmptyList(Container("GD2CN1NUM1"), List.empty)),
    specialMentions =
      Some(NonEmptyList(SpecialMentionDomain("GD2S1", "GD2S1Info", CustomsOffice("OOD1234A", "OfficeOfDeparturePage", CountryCode("GB"), None)), List.empty)),
    producedDocuments = Some(NonEmptyList(StandardDocument("G2D1", "G2D1Ref", Some("G2D1Info")), List.empty)),
    itemSecurityTraderDetails = Some(
      ItemsSecurityTraderDetails(
        Some("U"),
        Some("GD2CRN"),
        None,
        Some(
          SecurityPersonalInformation("GD2SECCONORName", CommonAddress("GD2CONORL1", "GD2CONORL2", "GD2CONL1", Country(CountryCode("GB"), "GD2CONNOR")))
        ),
        Some(
          SecurityPersonalInformation("GD2SECCONEEName", CommonAddress("GD2CONEEL1", "GD2CONEEL2", "GD2CEEL1", Country(CountryCode("GB"), "GD2CONNEE")))
        )
      )
    ),
    previousReferences = Some(NonEmptyList(PreviousReferences("GD2PR1", "GD2PR1Ref", Some("GD2PR1Info")), List.empty))
  )

  private val goodsSummary =
    GoodsSummary(Some("LoadPLace"), GoodSummaryNormalDetailsWithoutPreLodge(None, Some("CUSAPPLOC")), List(SealDomain("SEAL1"), SealDomain("SEAL2")))

  private val guarantee = NonEmptyList(
    GuaranteeReference(GuaranteeType.ComprehensiveGuarantee, "GUA1Ref", DefaultLiabilityAmount, "1234"),
    List(GuaranteeReference(GuaranteeType.GuaranteeWaiver, "GUA2Ref", OtherLiabilityAmount("500", CurrencyCode.GBP), "4321"))
  )

  private val safetyAndSecurity = Some(
    SafetyAndSecurity(
      Some("A"),
      None,
      None,
      Some("SomeConv"),
      Some("PlaceOfUnloadingPage"),
      None,
      None,
      Some(
        TraderEori(EoriNumber("CarrierEori"))
      ),
      NonEmptyList(Itinerary(CountryCode("CA")), List(Itinerary(CountryCode("CB"))))
    )
  )

  val toModel: JourneyDomain = JourneyDomain(
    preTaskListDetails,
    movementDetails,
    routeDetails,
    transportDetails,
    traderDetails,
    NonEmptyList(item1, List(item2)),
    goodsSummary,
    guarantee,
    safetyAndSecurity
  )
}
