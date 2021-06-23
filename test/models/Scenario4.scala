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

package models

import models.domain.SealDomain
import models.reference._
import play.api.libs.json.Json

import java.time.LocalDateTime

object Scenario4 extends UserAnswerScenario {

  private val firstGoodItem: Index      = Index(0)
  private val eoriNumber: EoriNumber    = EoriNumber("EoriNumber")
  private val lrn: LocalReferenceNumber = LocalReferenceNumber("ABCD1234567890123").get

  val userAnswers: UserAnswers = UserAnswers(lrn, eoriNumber, Json.obj())
    .unsafeSetVal(pages.ProcedureTypePage)(ProcedureType.Normal)
    .unsafeSetVal(pages.AddSecurityDetailsPage)(true)
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
    .unsafeSetVal(pages.OfficeOfDeparturePage)(CustomsOffice("OOD1234A", "OfficeOfDeparturePage", CountryCode("CC"), Nil, None))
    .unsafeSetVal(pages.DestinationCountryPage)(CountryCode("DC"))
    .unsafeSetVal(pages.MovementDestinationCountryPage)(CountryCode("MD"))
    .unsafeSetVal(pages.DestinationOfficePage)(CustomsOffice("DOP1234A", "DestinationOfficePage", CountryCode("DO"), Nil, None))
    .unsafeSetVal(pages.OfficeOfTransitCountryPage(Index(0)))(CountryCode("OT1"))
    .unsafeSetVal(pages.AddAnotherTransitOfficePage(Index(0)))("TOP12341")
    .unsafeSetVal(pages.ArrivalTimesAtOfficePage(Index(0)))(LocalDateTime.of(2020, 5, 5, 5, 12))
    .unsafeSetVal(pages.AddTransitOfficePage)(true)
    .unsafeSetVal(pages.OfficeOfTransitCountryPage(Index(0)))(CountryCode("OT1"))
    .unsafeSetVal(pages.AddAnotherTransitOfficePage(Index(0)))("TOP12341")
    .unsafeSetVal(pages.ArrivalTimesAtOfficePage(Index(0)))(LocalDateTime.of(2020, 5, 7, 21, 12))
    .unsafeSetVal(pages.AddTransitOfficePage)(false)
    /*
     * Transport Details
     * */
    .unsafeSetVal(pages.InlandModePage)("2")
    .unsafeSetVal(pages.AddIdAtDeparturePage)(false)
    .unsafeSetVal(pages.IdAtDeparturePage)("IDADEP")
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
    .unsafeSetVal(pages.safetyAndSecurity.CircumstanceIndicatorPage)("E")
    .unsafeSetVal(pages.safetyAndSecurity.AddTransportChargesPaymentMethodPage)(false)
    .unsafeSetVal(pages.safetyAndSecurity.AddCommercialReferenceNumberPage)(true)
    .unsafeSetVal(pages.safetyAndSecurity.AddCommercialReferenceNumberAllItemsPage)(true)
    .unsafeSetVal(pages.safetyAndSecurity.CommercialReferenceNumberAllItemsPage)("COMREFALL")
    .unsafeSetVal(pages.safetyAndSecurity.AddConveyanceReferenceNumberPage)(false)
    .unsafeSetVal(pages.safetyAndSecurity.AddPlaceOfUnloadingCodePage)(false)
    .unsafeSetVal(pages.safetyAndSecurity.CountryOfRoutingPage(Index(0)))(CountryCode("CA"))
    .unsafeSetVal(pages.safetyAndSecurity.AddAnotherCountryOfRoutingPage)(false)
    .unsafeSetVal(pages.safetyAndSecurity.AddSafetyAndSecurityConsignorPage)(true)
    .unsafeSetVal(pages.safetyAndSecurity.AddSafetyAndSecurityConsignorEoriPage)(true)
    .unsafeSetVal(pages.safetyAndSecurity.SafetyAndSecurityConsignorEoriPage)("SafeSecConorEori")
    .unsafeSetVal(pages.safetyAndSecurity.AddSafetyAndSecurityConsigneePage)(true)
    .unsafeSetVal(pages.safetyAndSecurity.AddSafetyAndSecurityConsigneeEoriPage)(true)
    .unsafeSetVal(pages.safetyAndSecurity.SafetyAndSecurityConsigneeEoriPage)("SafeSecConeeEori")
    .unsafeSetVal(pages.safetyAndSecurity.AddCarrierPage)(true)
    .unsafeSetVal(pages.safetyAndSecurity.AddCarrierEoriPage)(false)
    .unsafeSetVal(pages.safetyAndSecurity.CarrierNamePage)("CarrierName")
    .unsafeSetVal(pages.safetyAndSecurity.CarrierAddressPage)(CommonAddress("CarAddL1", "CarAddL2", "CarAddL3", Country(CountryCode("CA"), "CARRDESC")))
    /*
     * Item Details section - Item One
     * */
    .unsafeSetVal(pages.ItemDescriptionPage(firstGoodItem))("ItemOnesDescription")
    .unsafeSetVal(pages.ItemTotalGrossMassPage(firstGoodItem))("25000")
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
    .unsafeSetVal(pages.addItems.DeclareNumberOfPackagesPage(firstGoodItem, Index(1)))(true)
    .unsafeSetVal(pages.addItems.HowManyPackagesPage(firstGoodItem, Index(1)))(23)
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
    .unsafeSetVal(pages.addItems.specialMentions.SpecialMentionTypePage(firstGoodItem, Index(1)))("GD1S2")
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
    .unsafeSetVal(pages.addItems.AddExtraInformationPage(firstGoodItem, Index(1)))(false)
    .unsafeSetVal(pages.addItems.AddAnotherPreviousAdministrativeReferencePage(firstGoodItem))(false)
    .unsafeSetVal(pages.addItems.securityDetails.TransportChargesPage(firstGoodItem))("W")
    .unsafeSetVal(pages.addItems.securityDetails.CommercialReferenceNumberPage(firstGoodItem))("GD1CRN")
    .unsafeSetVal(pages.addItems.securityDetails.AddDangerousGoodsCodePage(firstGoodItem))(true)
    .unsafeSetVal(pages.addItems.securityDetails.DangerousGoodsCodePage(firstGoodItem))("GD1C")
    .unsafeSetVal(pages.addItems.traderSecurityDetails.AddSecurityConsignorsEoriPage(firstGoodItem))(true)
    .unsafeSetVal(pages.addItems.traderSecurityDetails.SecurityConsignorEoriPage(firstGoodItem))("GD1SECCONOR")
    .unsafeSetVal(pages.addItems.traderSecurityDetails.AddSecurityConsigneesEoriPage(firstGoodItem))(true)
    .unsafeSetVal(pages.addItems.traderSecurityDetails.SecurityConsigneeEoriPage(firstGoodItem))("GD1SECCONEE")
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

}