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

package services

import models.domain.SealDomain
import models.reference.{Country, CountryCode, CustomsOffice, PackageType}
import models.{CarrierAddress, ConsigneeAddress, ConsignorAddress, DeclarationType, EoriNumber, GuaranteeType, Index, LocalReferenceNumber, PrincipalAddress, ProcedureType, RepresentativeCapacity, UserAnswers}
import org.mockito.Mockito.{reset, when}
import org.scalatest.EitherValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages.addItems.traderDetails.TraderDetailsConsigneeEoriKnownPage
import pages.movementDetails.PreLodgeDeclarationPage
import pages.safetyAndSecurity._
import pages._
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import repositories.MongoSuite
import xml.XMLWrites._

import java.time.{LocalDate, LocalDateTime}

class UserAnswersToXmlConversionSpec extends AnyFreeSpec with Matchers with UserAnswersSpecHelper with XMLComparatorSpec
  with MongoSuite
  with ScalaFutures
  with GuiceOneAppPerSuite
  with IntegrationPatience
  with MockDateTimeService
  with EitherValues {

  implicit override lazy val app: Application = new GuiceApplicationBuilder()
    .overrides(
      bind[DateTimeService].toInstance(mockTimeService)
    )
    .build()

  class Setup {
    val emptyUserAnswers: UserAnswers = UserAnswers(
      LocalReferenceNumber("TestRefNumber").get,
      EoriNumber("1234567890")
    )

    val service: DeclarationRequestService = app.injector.instanceOf[DeclarationRequestService]

    reset(mockTimeService)

    when(mockTimeService.currentDateTime).thenReturn(LocalDateTime.of(2020, 12, 12, 20, 30))
    when(mockTimeService.dateFormatted).thenReturn("20201212")
  }

  "UserAnswers to XML conversion" - {
    "Scenario 1: " in new Setup {
      val firstGoodItem: Index =  Index(0)
      val secondGoodItem: Index =  Index(1)

      val userAnswers: UserAnswers = emptyUserAnswers
        .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
        .unsafeSetVal(AddSecurityDetailsPage)(true)
        /*
        * General Information Section
        * */
        .unsafeSetVal(DeclarationTypePage)(DeclarationType.Option2)
        .unsafeSetVal(PreLodgeDeclarationPage)(true)
        .unsafeSetVal(ContainersUsedPage)(true)
        .unsafeSetVal(DeclarationPlacePage)("XX1 1XX")
        .unsafeSetVal(DeclarationForSomeoneElsePage)(true)
        .unsafeSetVal(RepresentativeNamePage)("John Doe")
        .unsafeSetVal(RepresentativeCapacityPage)(RepresentativeCapacity.Direct)
        /*
        * RouteDetails
        * */
        .unsafeSetVal(CountryOfDispatchPage)(CountryCode("SC"))
        .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("OOD", "OfficeOfDeparturePage", CountryCode("CC"), Nil, None))
        .unsafeSetVal(DestinationCountryPage)(CountryCode("DC"))
        .unsafeSetVal(MovementDestinationCountryPage)(CountryCode("MD"))
        .unsafeSetVal(DestinationOfficePage)(CustomsOffice("DOP", "DestinationOfficePage", CountryCode("DO"), Nil, None))
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(0)))(CountryCode("OT1"))
        .unsafeSetVal(AddAnotherTransitOfficePage(Index(0)))("AddAnotherTransitOfficePageOT1")
        .unsafeSetVal(ArrivalTimesAtOfficePage(Index(0)))(LocalDateTime.of(2020, 5, 5, 5, 12))
        .unsafeSetVal(AddTransitOfficePage)(true)
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(1)))(CountryCode("OT2"))
        .unsafeSetVal(AddAnotherTransitOfficePage(Index(1)))("AddAnotherTransitOfficePageOT2")
        .unsafeSetVal(ArrivalTimesAtOfficePage(Index(1)))(LocalDateTime.of(2020, 5, 7, 21, 12))
        .unsafeSetVal(AddTransitOfficePage)(false)
        /*
        * Transport Details
        * */
        .unsafeSetVal(InlandModePage)("4")
        .unsafeSetVal(AddIdAtDeparturePage)(false)
        .unsafeSetVal(AddNationalityAtDeparturePage)(true)
        .unsafeSetVal(NationalityAtDeparturePage)(CountryCode("NDP"))
        .unsafeSetVal(ChangeAtBorderPage)(false)
        /*
        * Traders Details
        * */
        .unsafeSetVal(IsPrincipalEoriKnownPage)(false)
        .unsafeSetVal(PrincipalNamePage)("PrincipalName")
        .unsafeSetVal(PrincipalAddressPage)(PrincipalAddress("PrincipalStreet", "PrincipalTown", "AA1 1AA"))
        .unsafeSetVal(AddConsignorPage)(false)
        .unsafeSetVal(AddConsigneePage)(false)
        /*
        * Safety & Security Details
        * */
        .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
        .unsafeSetVal(CircumstanceIndicatorPage)("A")
        .unsafeSetVal(AddTransportChargesPaymentMethodPage)(true)
        .unsafeSetVal(TransportChargesPaymentMethodPage)("Card")
        .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
        .unsafeSetVal(pages.safetyAndSecurity.AddConveyanceReferenceNumberPage)(true)
        .unsafeSetVal(ConveyanceReferenceNumberPage)("SomeConv")
        .unsafeSetVal(PlaceOfUnloadingCodePage)("PlaceOfUnloadingPage")
        .unsafeSetVal(CountryOfRoutingPage(Index(0)))(CountryCode("CORP1"))
        .unsafeSetVal(AddAnotherCountryOfRoutingPage)(true)
        .unsafeSetVal(CountryOfRoutingPage(Index(1)))(CountryCode("CORP2"))
        .unsafeSetVal(AddAnotherCountryOfRoutingPage)(false)
        .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(false)
        .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(false)
        .unsafeSetVal(AddCarrierPage)(true)
        .unsafeSetVal(AddCarrierEoriPage)(true)
        .unsafeSetVal(CarrierEoriPage)("CarrierEori")
        /*
        * Item Details section - Item One
        * */
        .unsafeSetVal(ItemDescriptionPage(firstGoodItem))("ItemOnesDescription")
        .unsafeSetVal(ItemTotalGrossMassPage(firstGoodItem))("25000")
        .unsafeSetVal(AddTotalNetMassPage(firstGoodItem))(true)
        .unsafeSetVal(TotalNetMassPage(firstGoodItem))("12342")
        .unsafeSetVal(pages.IsCommodityCodeKnownPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.CommodityCodePage(firstGoodItem))("ComoCode1")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorEoriKnownPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorEoriNumberPage(firstGoodItem))("Conor123")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorNamePage(firstGoodItem))("ConorName")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorAddressPage(firstGoodItem))(ConsignorAddress("ConorLine1", "ConorLine2", "ConorLine3", Country(CountryCode("GD1"), "SomethingCO")))
        .unsafeSetVal(TraderDetailsConsigneeEoriKnownPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeEoriNumberPage(firstGoodItem))("Conee123")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeNamePage(firstGoodItem))("ConeeName")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeAddressPage(firstGoodItem))(ConsigneeAddress("ConeeLine1", "ConeeLine2", "ConeeLine3", Country(CountryCode("GD1"), "SomethingCE")))
        .unsafeSetVal(pages.PackageTypePage(firstGoodItem, Index(0)))(PackageType(PackageType.bulkCodes.head, "GD1PKG1"))
        .unsafeSetVal(pages.addItems.DeclareNumberOfPackagesPage(firstGoodItem, Index(0)))(false)
        .unsafeSetVal(pages.addItems.AddMarkPage(firstGoodItem, Index(0)))(false)
        .unsafeSetVal(pages.addItems.AddAnotherPackagePage(firstGoodItem))(true)
        .unsafeSetVal(pages.PackageTypePage(firstGoodItem, Index(1)))(PackageType(PackageType.unpackedCodes.head, "GD1PKG2"))
        .unsafeSetVal(pages.addItems.DeclareNumberOfPackagesPage(firstGoodItem, Index(1)))(false)
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
        .unsafeSetVal(pages.addItems.specialMentions.SpecialMentionTypePage(firstGoodItem, Index(0)))("GD1SPMT1")
        .unsafeSetVal(pages.addItems.specialMentions.SpecialMentionAdditionalInfoPage(firstGoodItem, Index(0)))("GD1SPMT1Info")
        .unsafeSetVal(pages.addItems.specialMentions.AddAnotherSpecialMentionPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.specialMentions.SpecialMentionTypePage(firstGoodItem, Index(0)))("GD1SPMT2")
        .unsafeSetVal(pages.addItems.specialMentions.SpecialMentionAdditionalInfoPage(firstGoodItem, Index(0)))("GD1SPMT2Info")
        .unsafeSetVal(pages.addItems.specialMentions.AddAnotherSpecialMentionPage(firstGoodItem))(false)
        .unsafeSetVal(pages.addItems.AddDocumentsPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.DocumentTypePage(firstGoodItem, Index(0)))("GD1DOC1")
        .unsafeSetVal(pages.addItems.DocumentReferencePage(firstGoodItem, Index(0)))("GD1DOC1Ref")
        .unsafeSetVal(pages.addItems.AddExtraDocumentInformationPage(firstGoodItem, Index(0)))(true)
        .unsafeSetVal(pages.addItems.DocumentExtraInformationPage(firstGoodItem, Index(0)))("GD1DOC1Info")
        .unsafeSetVal(pages.addItems.AddAnotherDocumentPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.DocumentTypePage(firstGoodItem, Index(1)))("GD1DOC2")
        .unsafeSetVal(pages.addItems.DocumentReferencePage(firstGoodItem, Index(1)))("GD1DOC2Ref")
        .unsafeSetVal(pages.addItems.AddExtraDocumentInformationPage(firstGoodItem, Index(1)))(false)
        .unsafeSetVal(pages.addItems.AddAnotherDocumentPage(firstGoodItem))(false)
        .unsafeSetVal(pages.addItems.AddAdministrativeReferencePage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.ReferenceTypePage(firstGoodItem, Index(0)))("GD1PREREF1")
        .unsafeSetVal(pages.addItems.PreviousReferencePage(firstGoodItem, Index(0)))("GD1PREREF1Ref")
        .unsafeSetVal(pages.addItems.AddExtraInformationPage(firstGoodItem, Index(0)))(true)
        .unsafeSetVal(pages.addItems.ExtraInformationPage(firstGoodItem, Index(0)))("GD1PREREF1Info")
        .unsafeSetVal(pages.addItems.AddAnotherPreviousAdministrativeReferencePage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.ReferenceTypePage(firstGoodItem, Index(1)))("GD1PREREF2")
        .unsafeSetVal(pages.addItems.PreviousReferencePage(firstGoodItem, Index(1)))("GD1PREREF2Ref")
        .unsafeSetVal(pages.addItems.AddExtraInformationPage(firstGoodItem, Index(1)))(false)
        .unsafeSetVal(pages.addItems.AddAnotherPreviousAdministrativeReferencePage(firstGoodItem))(false)
        .unsafeSetVal(pages.addItems.securityDetails.CommercialReferenceNumberPage(firstGoodItem))("GD1CRN")
        .unsafeSetVal(pages.addItems.securityDetails.AddDangerousGoodsCodePage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.securityDetails.DangerousGoodsCodePage(firstGoodItem))("GD1DGGDSCODE")
        .unsafeSetVal(pages.addItems.traderSecurityDetails.AddSecurityConsignorsEoriPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.traderSecurityDetails.SecurityConsignorEoriPage(firstGoodItem))("GD1SECCONOR")
        .unsafeSetVal(pages.addItems.traderSecurityDetails.AddSecurityConsigneesEoriPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.traderSecurityDetails.SecurityConsigneeEoriPage(firstGoodItem))("GD1SECCONEE")
        /*
          * Item Details section - Item One
          * */
        .unsafeSetVal(ItemDescriptionPage(secondGoodItem))("ItemTwosDescription")
        .unsafeSetVal(ItemTotalGrossMassPage(secondGoodItem))("25001")
        .unsafeSetVal(AddTotalNetMassPage(secondGoodItem))(false)
        .unsafeSetVal(pages.IsCommodityCodeKnownPage(secondGoodItem))(false)
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorEoriKnownPage(secondGoodItem))(false)
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorNamePage(secondGoodItem))("ConorName")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorAddressPage(secondGoodItem))(ConsignorAddress("ConorLine1", "ConorLine2", "ConorLine3", Country(CountryCode("GD2"), "SomethingCO")))
        .unsafeSetVal(TraderDetailsConsigneeEoriKnownPage(secondGoodItem))(false)
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeNamePage(secondGoodItem))("ConeeName")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeAddressPage(secondGoodItem))(ConsigneeAddress("ConeeLine1", "ConeeLine2", "ConeeLine3", Country(CountryCode("GD2"), "SomethingCE")))
        .unsafeSetVal(pages.PackageTypePage(secondGoodItem, Index(0)))(PackageType(PackageType.bulkCodes.head, "GD2PKG1"))
        .unsafeSetVal(pages.addItems.DeclareNumberOfPackagesPage(secondGoodItem, Index(0)))(false)
        .unsafeSetVal(pages.addItems.AddMarkPage(secondGoodItem, Index(0)))(false)
        .unsafeSetVal(pages.addItems.AddAnotherPackagePage(secondGoodItem))(true)
        .unsafeSetVal(pages.PackageTypePage(secondGoodItem, Index(1)))(PackageType(PackageType.unpackedCodes.head, "GD2PKG2"))
        .unsafeSetVal(pages.addItems.DeclareNumberOfPackagesPage(secondGoodItem, Index(1)))(false)
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
        .unsafeSetVal(pages.addItems.specialMentions.SpecialMentionTypePage(secondGoodItem, Index(0)))("GD2SPMT1")
        .unsafeSetVal(pages.addItems.specialMentions.SpecialMentionAdditionalInfoPage(secondGoodItem, Index(0)))("GD2SPMT1Info")
        .unsafeSetVal(pages.addItems.specialMentions.AddAnotherSpecialMentionPage(secondGoodItem))(false)
        .unsafeSetVal(pages.addItems.AddDocumentsPage(secondGoodItem))(true)
        .unsafeSetVal(pages.addItems.DocumentTypePage(secondGoodItem, Index(0)))("GD2DOC1")
        .unsafeSetVal(pages.addItems.DocumentReferencePage(secondGoodItem, Index(0)))("GD2DOC1Ref")
        .unsafeSetVal(pages.addItems.AddExtraDocumentInformationPage(secondGoodItem, Index(0)))(true)
        .unsafeSetVal(pages.addItems.DocumentExtraInformationPage(secondGoodItem, Index(0)))("GD2DOC1Info")
        .unsafeSetVal(pages.addItems.AddAnotherDocumentPage(secondGoodItem))(false)
        .unsafeSetVal(pages.addItems.AddAdministrativeReferencePage(secondGoodItem))(true)
        .unsafeSetVal(pages.addItems.ReferenceTypePage(secondGoodItem, Index(0)))("GD2PREREF1")
        .unsafeSetVal(pages.addItems.PreviousReferencePage(secondGoodItem, Index(0)))("GD2PREREF1Ref")
        .unsafeSetVal(pages.addItems.AddExtraInformationPage(secondGoodItem, Index(0)))(true)
        .unsafeSetVal(pages.addItems.ExtraInformationPage(secondGoodItem, Index(0)))("GD2PREREF1Info")
        .unsafeSetVal(pages.addItems.AddAnotherPreviousAdministrativeReferencePage(secondGoodItem))(false)
        .unsafeSetVal(pages.addItems.securityDetails.CommercialReferenceNumberPage(secondGoodItem))("GD2CRN")
        .unsafeSetVal(pages.addItems.securityDetails.AddDangerousGoodsCodePage(secondGoodItem))(false)
        .unsafeSetVal(pages.addItems.traderSecurityDetails.AddSecurityConsignorsEoriPage(secondGoodItem))(false)
        .unsafeSetVal(pages.addItems.traderSecurityDetails.SecurityConsignorNamePage(secondGoodItem))("GD2SECCONORName")
        .unsafeSetVal(pages.addItems.traderSecurityDetails.SecurityConsignorAddressPage(secondGoodItem))(ConsignorAddress("GD2CONORL1", "GD2CONORL2", "GD2CONLOR1", Country(CountryCode("GD2"), "GD2CONNOR")))
        .unsafeSetVal(pages.addItems.traderSecurityDetails.AddSecurityConsigneesEoriPage(secondGoodItem))(false)
        .unsafeSetVal(pages.addItems.traderSecurityDetails.SecurityConsigneeNamePage(secondGoodItem))("GD2SECCONEEName")
        .unsafeSetVal(pages.addItems.traderSecurityDetails.SecurityConsigneeAddressPage(secondGoodItem))(ConsigneeAddress("GD2CONORL1", "GD2CONORL2", "GD2CONLOR1", Country(CountryCode("GD2"), "GD2CONNOR")))
      /*
      * Goods Summary
      */
        .unsafeSetVal(pages.DeclarePackagesPage)(true)
        .unsafeSetVal(pages.TotalPackagesPage)(1)
        .unsafeSetVal(pages.TotalGrossMassPage)("12131415")
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

      /*
      * TODO:
      *  MesSenMES3 is missing in the service. It is mandatory
      *  PRODOCDC2 not working properly for first item
      */
      val expectedXml = <CC015B>
        <SynIdeMES1>UNOC</SynIdeMES1>
        <SynVerNumMES2>3</SynVerNumMES2>
        <MesSenMES3>NCTS</MesSenMES3>
        <MesRecMES6>NCTS</MesRecMES6>
        <DatOfPreMES9>20201212</DatOfPreMES9>
        <TimOfPreMES10>2030</TimOfPreMES10>
        <IntConRefMES11>DF20201212150</IntConRefMES11>
        <AppRefMES14>NCTS</AppRefMES14>
        <PriMES15></PriMES15>
        <AckReqMES16></AckReqMES16>
        <ComAgrIdMES17></ComAgrIdMES17>
        <TesIndMES18>0</TesIndMES18>
        <MesIdeMES19>1</MesIdeMES19>
        <MesTypMES20>GB015B</MesTypMES20>
        <ComAccRefMES21></ComAccRefMES21>
        <MesSeqNumMES22></MesSeqNumMES22>
        <HEAHEA>
          <RefNumHEA4>TestRefNumber</RefNumHEA4>
          <TypOfDecHEA24>T2</TypOfDecHEA24>
          <CouOfDesCodHEA30>DC</CouOfDesCodHEA30>
          <AgrLocOfGooCodHEA38></AgrLocOfGooCodHEA38>
          <AgrLocOfGooHEA39>Pre-lodge</AgrLocOfGooHEA39>
          <AgrLocOfGooHEA39LNG>EN</AgrLocOfGooHEA39LNG>
          <AutLocOfGooCodHEA41></AutLocOfGooCodHEA41>
          <PlaOfLoaCodHEA46>LoadPLace</PlaOfLoaCodHEA46>
          <CouOfDisCodHEA55>SC</CouOfDisCodHEA55>
          <CusSubPlaHEA66>CUSAPPLOC</CusSubPlaHEA66>
          <InlTraModHEA75>4</InlTraModHEA75>
          <TraModAtBorHEA76>4</TraModAtBorHEA76>
          <IdeOfMeaOfTraAtDHEA78></IdeOfMeaOfTraAtDHEA78>
          <NatOfMeaOfTraAtDHEA80>NDP</NatOfMeaOfTraAtDHEA80>
          <IdeOfMeaOfTraCroHEA85></IdeOfMeaOfTraCroHEA85>
          <NatOfMeaOfTraCroHEA87>NDP</NatOfMeaOfTraCroHEA87>
          <TypOfMeaOfTraCroHEA88>4</TypOfMeaOfTraCroHEA88>
          <ConIndHEA96>1</ConIndHEA96>
          <DiaLanIndAtDepHEA254>EN</DiaLanIndAtDepHEA254>
          <NCTSAccDocHEA601LNG>EN</NCTSAccDocHEA601LNG>
          <TotNumOfIteHEA305>2</TotNumOfIteHEA305>
          <TotNumOfPacHEA306>1</TotNumOfPacHEA306>
          <TotGroMasHEA307>12131415</TotGroMasHEA307>
          <DecDatHEA383>20201212</DecDatHEA383>
          <DecPlaHEA394>XX1 1XX</DecPlaHEA394>
          <DecPlaHEA394LNG>EN</DecPlaHEA394LNG>
          <SpeCirIndHEA1>A</SpeCirIndHEA1>
          <TraChaMetOfPayHEA1>Card</TraChaMetOfPayHEA1>
          <ComRefNumHEA></ComRefNumHEA>
          <SecHEA358>1</SecHEA358>
          <ConRefNumHEA>SomeConv</ConRefNumHEA>
          <CodPlUnHEA357>PlaceOfUnloadingPage</CodPlUnHEA357>
          <CodPlUnHEA357LNG>EN</CodPlUnHEA357LNG>
        </HEAHEA>
        <TRAPRIPC1>
          <NamPC17>PrincipalName</NamPC17>
          <StrAndNumPC122>PrincipalStreet</StrAndNumPC122>
          <PosCodPC123>AA1 1AA</PosCodPC123>
          <CitPC124>PrincipalTown</CitPC124>
          <CouPC125>GB</CouPC125>
          <NADLNGPC>EN</NADLNGPC>
        </TRAPRIPC1>
        <CUSOFFDEPEPT>
          <RefNumEPT1>OOD</RefNumEPT1>
        </CUSOFFDEPEPT>
        <CUSOFFTRARNS>
          <RefNumRNS1>AddAnotherTransitOfficePageOT1</RefNumRNS1>
          <ArrTimTRACUS085>202005050512</ArrTimTRACUS085>
        </CUSOFFTRARNS>
        <CUSOFFTRARNS>
          <RefNumRNS1>AddAnotherTransitOfficePageOT2</RefNumRNS1>
          <ArrTimTRACUS085>202005072112</ArrTimTRACUS085>
        </CUSOFFTRARNS>
        <CUSOFFDESEST>
          <RefNumEST1>DOP</RefNumEST1>
        </CUSOFFDESEST>
        <CONRESERS>
          <ConResCodERS16></ConResCodERS16>
          <DatLimERS69></DatLimERS69>
        </CONRESERS>
        <REPREP>
          <NamREP5>John Doe</NamREP5>
          <RepCapREP18>direct</RepCapREP18>
          <RepCapREP18LNG>EN</RepCapREP18LNG>
        </REPREP>
        <SEAINFSLI>
          <SeaNumSLI2>2</SeaNumSLI2>
          <SEAIDSID>
            <SeaIdeSID1>SEAL1</SeaIdeSID1>
            <SeaIdeSID1LNG>EN</SeaIdeSID1LNG>
          </SEAIDSID>
          <SEAIDSID>
            <SeaIdeSID1>SEAL2</SeaIdeSID1>
            <SeaIdeSID1LNG>EN</SeaIdeSID1LNG>
          </SEAIDSID>
        </SEAINFSLI>
        <GUAGUA>
          <GuaTypGUA1>1</GuaTypGUA1>
          <GUAREFREF>
            <GuaRefNumGRNREF1>GUA1Ref</GuaRefNumGRNREF1>
            <OthGuaRefREF4></OthGuaRefREF4>
            <AccCodeREF6></AccCodeREF6>
            <VALLIMECVLE>
              <NotValForECVLE1></NotValForECVLE1>
            </VALLIMECVLE>
            <VALLIMNONECLIM>
              <NotValForOthConPLIM2></NotValForOthConPLIM2>
            </VALLIMNONECLIM>
          </GUAREFREF>
        </GUAGUA>
        <GUAGUA>
          <GuaTypGUA1>0</GuaTypGUA1>
          <GUAREFREF>
            <GuaRefNumGRNREF1>GUA2Ref</GuaRefNumGRNREF1>
            <OthGuaRefREF4></OthGuaRefREF4>
            <AccCodeREF6></AccCodeREF6>
            <VALLIMECVLE>
              <NotValForECVLE1></NotValForECVLE1>
            </VALLIMECVLE>
            <VALLIMNONECLIM>
              <NotValForOthConPLIM2></NotValForOthConPLIM2>
            </VALLIMNONECLIM>
          </GUAREFREF>
        </GUAGUA>
        <GOOITEGDS>
          <IteNumGDS7>1</IteNumGDS7>
          <ComCodTarCodGDS10>ComoCode1</ComCodTarCodGDS10>
          <DecTypGDS15></DecTypGDS15>
          <GooDesGDS23>ItemOnesDescription</GooDesGDS23>
          <GroMasGDS46>25000</GroMasGDS46>
          <NetMasGDS48>12342</NetMasGDS48>
          <CouOfDisGDS58></CouOfDisGDS58>
          <CouOfDesGDS59></CouOfDesGDS59>
          <MetOfPayGDI12></MetOfPayGDI12>
          <ComRefNumGIM1>GD1CRN</ComRefNumGIM1>
          <UNDanGooCodGDI1>GD1DGGDSCODE</UNDanGooCodGDI1>
          <PREADMREFAR2>
            <PreDocTypAR21>GD1PREREF1</PreDocTypAR21>
            <PreDocRefAR26>GD1PREREF1Ref</PreDocRefAR26>
            <ComOfInfAR29>GD1PREREF1Info</ComOfInfAR29>
          </PREADMREFAR2>
          <PREADMREFAR2>
            <PreDocTypAR21>GD1PREREF2</PreDocTypAR21>
            <PreDocRefAR26>GD1PREREF2Ref</PreDocRefAR26>
          </PREADMREFAR2>
          <PRODOCDC2>
            <DocTypDC21></DocTypDC21>
            <DocRefDC23></DocRefDC23>
            <ComOfInfDC25></ComOfInfDC25>
          </PRODOCDC2>
          <PRODOCDC2>
            <DocTypDC21></DocTypDC21>
            <DocRefDC23></DocRefDC23>
            <ComOfInfDC25></ComOfInfDC25>
          </PRODOCDC2>
          <SPEMENMT2>
            <AddInfMT21>10000EURGUA1Ref</AddInfMT21>
            <AddInfCodMT23>CAL</AddInfCodMT23>
            <ExpFroECMT24></ExpFroECMT24>
            <ExpFroCouMT25></ExpFroCouMT25>
          </SPEMENMT2>
          <SPEMENMT2>
            <AddInfMT21>500GBPGUA2Ref</AddInfMT21>
            <AddInfCodMT23>CAL</AddInfCodMT23>
            <ExpFroECMT24></ExpFroECMT24>
            <ExpFroCouMT25></ExpFroCouMT25>
          </SPEMENMT2>
          <SPEMENMT2>
            <AddInfMT21>GD1SPMT2Info</AddInfMT21>
            <AddInfCodMT23>GD1SPMT2</AddInfCodMT23>
            <ExpFroECMT24></ExpFroECMT24>
            <ExpFroCouMT25></ExpFroCouMT25>
          </SPEMENMT2>
          <TRACONCO2>
            <NamCO27>ConorName</NamCO27>
            <StrAndNumCO222>ConorLine1</StrAndNumCO222>
            <PosCodCO223>ConorLine3</PosCodCO223>
            <CitCO224>ConorLine2</CitCO224>
            <CouCO225>GD1</CouCO225>
            <TINCO259>Conor123</TINCO259>
          </TRACONCO2>
          <TRACONCE2>
            <NamCE27>ConeeName</NamCE27>
            <StrAndNumCE222>ConeeLine1</StrAndNumCE222>
            <PosCodCE223>ConeeLine3</PosCodCE223>
            <CitCE224>ConeeLine2</CitCE224>
            <CouCE225>GD1</CouCE225>
            <TINCE259>Conee123</TINCE259>
          </TRACONCE2>
          <CONNR2>
            <ConNumNR21></ConNumNR21>
          </CONNR2>
          <PACGS2>
            <KinOfPacGS23>VQ</KinOfPacGS23>
            <NumOfPacGS24>2</NumOfPacGS24>
            <NumOfPieGS25>12</NumOfPieGS25>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD1PK2MK</MarNumOfPacGS21>
            <KinOfPacGS23>NE</KinOfPacGS23>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD1PK3MK</MarNumOfPacGS21>
            <KinOfPacGS23>BAG</KinOfPacGS23>
          </PACGS2>
          <SGICODSD2>
            <SenGooCodSD22></SenGooCodSD22>
            <SenQuaSD23></SenQuaSD23>
          </SGICODSD2>
          <TRACORSECGOO021>
            <NamTRACORSECGOO025></NamTRACORSECGOO025>
            <StrNumTRACORSECGOO027></StrNumTRACORSECGOO027>
            <PosCodTRACORSECGOO026></PosCodTRACORSECGOO026>
            <CitTRACORSECGOO022></CitTRACORSECGOO022>
            <CouCodTRACORSECGOO023></CouCodTRACORSECGOO023>
            <TINTRACORSECGOO028></TINTRACORSECGOO028>
          </TRACORSECGOO021>
          <TRACONSECGOO013>
            <NamTRACONSECGOO017></NamTRACONSECGOO017>
            <StrNumTRACONSECGOO019></StrNumTRACONSECGOO019>
            <PosCodTRACONSECGOO018></PosCodTRACONSECGOO018>
            <CityTRACONSECGOO014></CityTRACONSECGOO014>
            <CouCodTRACONSECGOO015></CouCodTRACONSECGOO015>
            <TINTRACONSECGOO020></TINTRACONSECGOO020>
          </TRACONSECGOO013>
        </GOOITEGDS>
        <GOOITEGDS>
          <IteNumGDS7>2</IteNumGDS7>
          <DecTypGDS15></DecTypGDS15>
          <GooDesGDS23>ItemTwosDescription</GooDesGDS23>
          <GroMasGDS46>25001</GroMasGDS46>
          <CouOfDisGDS58></CouOfDisGDS58>
          <CouOfDesGDS59></CouOfDesGDS59>
          <MetOfPayGDI12></MetOfPayGDI12>
          <ComRefNumGIM1>GD2CRN</ComRefNumGIM1>
          <PREADMREFAR2>
            <PreDocTypAR21>GD2PREREF1</PreDocTypAR21>
            <PreDocRefAR26>GD2PREREF1Ref</PreDocRefAR26>
            <ComOfInfAR29>GD2PREREF1Info</ComOfInfAR29>
          </PREADMREFAR2>
          <PRODOCDC2>
            <DocTypDC21>GD2DOC1</DocTypDC21>
            <DocRefDC23>GD2DOC1Ref</DocRefDC23>
            <ComOfInfDC25>GD2DOC1Info</ComOfInfDC25>
          </PRODOCDC2>
          <SPEMENMT2>
            <AddInfMT21>GD2SPMT1Info</AddInfMT21>
            <AddInfCodMT23>GD2SPMT1</AddInfCodMT23>
            <ExpFroECMT24></ExpFroECMT24>
            <ExpFroCouMT25></ExpFroCouMT25>
          </SPEMENMT2>
          <TRACONCO2>
            <NamCO27>ConorName</NamCO27>
            <StrAndNumCO222>ConorLine1</StrAndNumCO222>
            <PosCodCO223>ConorLine3</PosCodCO223>
            <CitCO224>ConorLine2</CitCO224>
            <CouCO225>GD2</CouCO225>
          </TRACONCO2>
          <TRACONCE2>
            <NamCE27>ConeeName</NamCE27>
            <StrAndNumCE222>ConeeLine1</StrAndNumCE222>
            <PosCodCE223>ConeeLine3</PosCodCE223>
            <CitCE224>ConeeLine2</CitCE224>
            <CouCE225>GD2</CouCE225>
          </TRACONCE2>
          <CONNR2>
            <ConNumNR21></ConNumNR21>
          </CONNR2>
          <PACGS2>
            <KinOfPacGS23>VQ</KinOfPacGS23>
            <NumOfPacGS24>2</NumOfPacGS24>
            <NumOfPieGS25>12</NumOfPieGS25>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD2PK2MK</MarNumOfPacGS21>
            <KinOfPacGS23>NE</KinOfPacGS23>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD2PK3MK</MarNumOfPacGS21>
            <KinOfPacGS23>BAG</KinOfPacGS23>
          </PACGS2>
          <SGICODSD2>
            <SenGooCodSD22></SenGooCodSD22>
            <SenQuaSD23></SenQuaSD23>
          </SGICODSD2>
          <TRACORSECGOO021>
            <NamTRACORSECGOO025></NamTRACORSECGOO025>
            <StrNumTRACORSECGOO027></StrNumTRACORSECGOO027>
            <PosCodTRACORSECGOO026></PosCodTRACORSECGOO026>
            <CitTRACORSECGOO022></CitTRACORSECGOO022>
            <CouCodTRACORSECGOO023></CouCodTRACORSECGOO023>
            <TINTRACORSECGOO028></TINTRACORSECGOO028>
          </TRACORSECGOO021>
          <TRACONSECGOO013>
            <NamTRACONSECGOO017></NamTRACONSECGOO017>
            <StrNumTRACONSECGOO019></StrNumTRACONSECGOO019>
            <PosCodTRACONSECGOO018></PosCodTRACONSECGOO018>
            <CityTRACONSECGOO014></CityTRACONSECGOO014>
            <CouCodTRACONSECGOO015></CouCodTRACONSECGOO015>
            <TINTRACONSECGOO020></TINTRACONSECGOO020>
          </TRACONSECGOO013>
        </GOOITEGDS>
        <ITI>
          <CouOfRouCodITI1>CORP1</CouOfRouCodITI1>
        </ITI>
        <ITI>
          <CouOfRouCodITI1>CORP2</CouOfRouCodITI1>
        </ITI>
        <CARTRA100>
          <NamCARTRA121></NamCARTRA121>
          <StrAndNumCARTRA254></StrAndNumCARTRA254>
          <PosCodCARTRA121></PosCodCARTRA121>
          <CitCARTRA789></CitCARTRA789>
          <CouCodCARTRA587></CouCodCARTRA587>
          <NADCARTRA121></NADCARTRA121>
          <TINCARTRA254>CarrierEori</TINCARTRA254>
        </CARTRA100>
        <TRACORSEC037>
          <NamTRACORSEC041></NamTRACORSEC041>
          <StrNumTRACORSEC043></StrNumTRACORSEC043>
          <PosCodTRACORSEC042></PosCodTRACORSEC042>
          <CitTRACORSEC038></CitTRACORSEC038>
          <CouCodTRACORSEC039></CouCodTRACORSEC039>
          <TINTRACORSEC044></TINTRACORSEC044>
        </TRACORSEC037>
        <TRACONSEC029>
          <NameTRACONSEC033></NameTRACONSEC033>
          <StrNumTRAACONSEC035></StrNumTRAACONSEC035>
          <PosCodTRACONSEC034></PosCodTRACONSEC034>
          <CitTRACONSEC030></CitTRACONSEC030>
          <CouCodTRACONSEC031></CouCodTRACONSEC031>
          <TINTRACONSEC036></TINTRACONSEC036>
        </TRACONSEC029>
      </CC015B>

      println(service.convert(userAnswers)
        .futureValue
        .right.value.toXml.map(scala.xml.Utility.trim))
      service.convert(userAnswers)
        .futureValue
        .right.value.toXml.map(scala.xml.Utility.trim) xmlMustEqual expectedXml.map(scala.xml.Utility.trim)
    }
    "Scenario 2: " ignore new Setup {
      val firstGoodItem: Index =  Index(0)

      val userAnswers: UserAnswers = emptyUserAnswers
        .unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
        .unsafeSetVal(AddSecurityDetailsPage)(false)
        /*
        * General Information Section
        * */
        .unsafeSetVal(DeclarationTypePage)(DeclarationType.Option2)
        .unsafeSetVal(ContainersUsedPage)(false)
        .unsafeSetVal(DeclarationPlacePage)("XX1 1XX")
        .unsafeSetVal(DeclarationForSomeoneElsePage)(false)
        /*
        * RouteDetails
        * */
        .unsafeSetVal(CountryOfDispatchPage)(CountryCode("SC"))
        .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("OOD", "OfficeOfDeparturePage", CountryCode("CC"), Nil, None))
        .unsafeSetVal(DestinationCountryPage)(CountryCode("DC"))
        .unsafeSetVal(MovementDestinationCountryPage)(CountryCode("MD"))
        .unsafeSetVal(DestinationOfficePage)(CustomsOffice("DOP", "DestinationOfficePage", CountryCode("DO"), Nil, None))
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(0)))(CountryCode("OT1"))
        .unsafeSetVal(AddAnotherTransitOfficePage(Index(0)))("AddAnotherTransitOfficePageOT1")
        .unsafeSetVal(ArrivalTimesAtOfficePage(Index(0)))(LocalDateTime.of(2020, 5, 5, 5, 12))
        .unsafeSetVal(AddTransitOfficePage)(true)
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(0)))(CountryCode("OT1"))
        .unsafeSetVal(AddAnotherTransitOfficePage(Index(0)))("AddAnotherTransitOfficePageOT1")
        .unsafeSetVal(ArrivalTimesAtOfficePage(Index(0)))(LocalDateTime.of(2020, 5, 7, 21, 12))
        .unsafeSetVal(AddTransitOfficePage)(false)
        /*
        * Transport Details
        * */
        .unsafeSetVal(InlandModePage)("3")
        .unsafeSetVal(pages.IdAtDeparturePage)("SomeIdAtDeparture")
        .unsafeSetVal(NationalityAtDeparturePage)(CountryCode("NDP"))
        .unsafeSetVal(ChangeAtBorderPage)(true)
        .unsafeSetVal(pages.ModeAtBorderPage)("3")
        .unsafeSetVal(pages.ModeCrossingBorderPage)("8")
        .unsafeSetVal(pages.IdCrossingBorderPage)("IDCBP")
        .unsafeSetVal(pages.NationalityCrossingBorderPage)(CountryCode("NCBP"))
        /*
        * Traders Details
        * */
        .unsafeSetVal(pages.WhatIsPrincipalEoriPage)("PRINCEORI")
        .unsafeSetVal(AddConsignorPage)(true)
        .unsafeSetVal(pages.IsConsignorEoriKnownPage)(false)
        .unsafeSetVal(pages.ConsignorNamePage)("ConsignorName")
        .unsafeSetVal(pages.ConsignorAddressPage)(ConsignorAddress("ConorLine1", "ConorLine2", "ConorLine3", Country(CountryCode("CN"), "SomethingCO")))
        .unsafeSetVal(AddConsigneePage)(true)
        .unsafeSetVal(pages.IsConsigneeEoriKnownPage)(false)
        .unsafeSetVal(pages.ConsigneeNamePage)("ConsigneeName")
        .unsafeSetVal(pages.ConsigneeAddressPage)(ConsigneeAddress("ConeeLine1", "ConeeLine2", "ConeeLine3", Country(CountryCode("CN"), "SomethingCE")))
        /*
        * Item Details section - Item One
        * */
        .unsafeSetVal(ItemDescriptionPage(firstGoodItem))("ItemOnesDescription")
        .unsafeSetVal(ItemTotalGrossMassPage(firstGoodItem))("25000")
        .unsafeSetVal(AddTotalNetMassPage(firstGoodItem))(false)
        .unsafeSetVal(pages.IsCommodityCodeKnownPage(firstGoodItem))(false)
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
        .unsafeSetVal(pages.addItems.specialMentions.AddSpecialMentionPage(firstGoodItem))(false)
        .unsafeSetVal(pages.addItems.AddDocumentsPage(firstGoodItem))(false)
        .unsafeSetVal(pages.addItems.AddAdministrativeReferencePage(firstGoodItem))(false)
        /*
        * Goods Summary
        */
        .unsafeSetVal(pages.DeclarePackagesPage)(true)
        .unsafeSetVal(pages.TotalPackagesPage)(1)
        .unsafeSetVal(pages.TotalGrossMassPage)("12131415")
        .unsafeSetVal(pages.AuthorisedLocationCodePage)("AuthLocationCode")
        .unsafeSetVal(pages.ControlResultDateLimitPage)(LocalDate.of(2020, 12, 12))
        .unsafeSetVal(pages.AddSealsPage)(false)
        /*
        * guarantee Details
        */
        .unsafeSetVal(pages.guaranteeDetails.GuaranteeTypePage(Index(0)))(GuaranteeType.CashDepositGuarantee)
        .unsafeSetVal(pages.OtherReferencePage(Index(0)))("GUA1Reference")
        .unsafeSetVal(pages.AddAnotherGuaranteePage)(false)

      val expectedXml = <CC015B>
        <SynIdeMES1>UNOC</SynIdeMES1>
        <SynVerNumMES2>3</SynVerNumMES2>
        <MesSenMES3>NCTS</MesSenMES3>
        <MesRecMES6>NCTS</MesRecMES6>
        <DatOfPreMES9>20201212</DatOfPreMES9>
        <TimOfPreMES10>2030</TimOfPreMES10>
        <IntConRefMES11>DFnull150</IntConRefMES11>
        <AppRefMES14>NCTS</AppRefMES14>
        <MesIdeMES19>1</MesIdeMES19>
        <MesTypMES20>GB015B</MesTypMES20>
        <HEAHEA>
          <RefNumHEA4></RefNumHEA4>
          <TypOfDecHEA24></TypOfDecHEA24>
          <CouOfDesCodHEA30></CouOfDesCodHEA30>
          <AgrLocOfGooCodHEA38></AgrLocOfGooCodHEA38>
          <AgrLocOfGooHEA39></AgrLocOfGooHEA39>
          <AutLocOfGooCodHEA41></AutLocOfGooCodHEA41>
          <PlaOfLoaCodHEA46></PlaOfLoaCodHEA46>
          <CouOfDisCodHEA55></CouOfDisCodHEA55>
          <CusSubPlaHEA66></CusSubPlaHEA66>
          <TraModAtBorHEA76></TraModAtBorHEA76>
          <IdeOfMeaOfTraAtDHEA78></IdeOfMeaOfTraAtDHEA78>
          <NatOfMeaOfTraAtDHEA80></NatOfMeaOfTraAtDHEA80>
          <IdeOfMeaOfTraCroHEA85></IdeOfMeaOfTraCroHEA85>
          <NatOfMeaOfTraCroHEA87></NatOfMeaOfTraCroHEA87>
          <ConIndHEA96></ConIndHEA96>
          <NCTSAccDocHEA601LNG></NCTSAccDocHEA601LNG>
          <TotNumOfIteHEA305></TotNumOfIteHEA305>
          <TotNumOfPacHEA306></TotNumOfPacHEA306>
          <TotGroMasHEA307></TotGroMasHEA307>
          <DecDatHEA383></DecDatHEA383>
          <DecPlaHEA394></DecPlaHEA394>
          <SpeCirIndHEA1></SpeCirIndHEA1>
          <TraChaMetOfPayHEA1></TraChaMetOfPayHEA1>
          <ComRefNumHEA></ComRefNumHEA>
          <CodPlUnHEA357></CodPlUnHEA357>
        </HEAHEA>
        <TRAPRIPC1>
          <NamPC17>PrincipalName</NamPC17>
          <StrAndNumPC122>PrincipalStreet</StrAndNumPC122>
          <PosCodPC123>AA1 1AA</PosCodPC123>
          <CitPC124>PrincipalTown</CitPC124>
          <CouPC125>GB</CouPC125>
          <TINPC159></TINPC159>
          <HITPC126></HITPC126>
        </TRAPRIPC1>
        <TRACONCO1>
          <NamCO17></NamCO17>
          <StrAndNumCO122></StrAndNumCO122>
          <PosCodCO123></PosCodCO123>
          <CitCO124></CitCO124>
          <CouCO125></CouCO125>
          <TINCO159></TINCO159>
        </TRACONCO1>
        <TRACONCE1>
          <NamCE17></NamCE17>
          <StrAndNumCE122></StrAndNumCE122>
          <PosCodCE123></PosCodCE123>
          <CitCE124></CitCE124>
          <CouCE125></CouCE125>
          <TINCE159></TINCE159>
        </TRACONCE1>
        <TRAAUTCONTRA>
          <TINTRA59></TINTRA59>
        </TRAAUTCONTRA>
        <CUSOFFDEPEPT>
          <RefNumEPT1></RefNumEPT1>
        </CUSOFFDEPEPT>
        <CUSOFFTRARNS>
          <RefNumRNS1></RefNumRNS1>
          <ArrTimTRACUS085></ArrTimTRACUS085>
        </CUSOFFTRARNS>
        <CUSOFFDESEST>
          <RefNumEST1></RefNumEST1>
        </CUSOFFDESEST>
        <CONRESERS>
          <ConResCodERS16></ConResCodERS16>
          <DatLimERS69></DatLimERS69>
        </CONRESERS>
        <REPREP>
          <NamREP5></NamREP5>
          <RepCapREP18></RepCapREP18>
        </REPREP>
        <SEAINFSLIType>
          <SeaNumSL12></SeaNumSL12>
          <SEAIDSID>
            <SeaIdeSID1></SeaIdeSID1>
          </SEAIDSID>
        </SEAINFSLIType>
        <GUAGUA>
          <GuaTypGUA1></GuaTypGUA1>
          <GUAREFREF>
            <GuaRefNumGRNREF1></GuaRefNumGRNREF1>
            <OthGuaRefREF4></OthGuaRefREF4>
            <AccCodeREF6></AccCodeREF6>
            <VALLIMECVLE>
              <NotValForECVLE1></NotValForECVLE1>
            </VALLIMECVLE>
            <VALLIMNONECLIM>
              <NotValForOthConPLIM2></NotValForOthConPLIM2>
            </VALLIMNONECLIM>
          </GUAREFREF>
        </GUAGUA>
        <GOOITEGDS>
          <IteNumGDS7>1</IteNumGDS7>
          <ComCodTarCodGDS10></ComCodTarCodGDS10>
          <DecTypGDS15></DecTypGDS15>
          <GooDesGDS23></GooDesGDS23>
          <GroMasGDS46></GroMasGDS46>
          <NetMasGDS48></NetMasGDS48>
          <CouOfDisGDS58></CouOfDisGDS58>
          <CouOfDesGDS59></CouOfDesGDS59>
          <MetOfPayGDI12></MetOfPayGDI12>
          <ComRefNumGIM1></ComRefNumGIM1>
          <UNDanGooCodGDI1></UNDanGooCodGDI1>
          <PREADMREFAR2>
            <PreDocTypAR21></PreDocTypAR21>
            <PreDocRefAR26></PreDocRefAR26>
            <ComOfInfAR29></ComOfInfAR29>
          </PREADMREFAR2>
          <PRODOCDC2>
            <DocTypDC21></DocTypDC21>
            <DocRefDC23></DocRefDC23>
            <ComOfInfDC25></ComOfInfDC25>
          </PRODOCDC2>
          <SPEMENMT2>
            <AddInfMT21></AddInfMT21>
            <AddInfCodMT23></AddInfCodMT23>
            <ExpFroECMT24></ExpFroECMT24>
            <ExpFroCouMT25></ExpFroCouMT25>
          </SPEMENMT2>
          <TRACONCO2>
            <NamCO27></NamCO27>
            <StrAndNumCO222></StrAndNumCO222>
            <PosCodCO223></PosCodCO223>
            <CitCO224></CitCO224>
            <CouCO225></CouCO225>
            <TINCO259></TINCO259>
          </TRACONCO2>
          <TRACONCE2>
            <NamCE27></NamCE27>
            <StrAndNumCE222></StrAndNumCE222>
            <PosCodCE223></PosCodCE223>
            <CitCE224></CitCE224>
            <CouCE225></CouCE225>
            <TINCE259></TINCE259>
          </TRACONCE2>
          <CONNR2>
            <ConNumNR21></ConNumNR21>
          </CONNR2>
          <PACGS2>
            <MarNumOfPacGS21></MarNumOfPacGS21>
            <KinOfPacGS23></KinOfPacGS23>
            <NumOfPacGS24></NumOfPacGS24>
            <NumOfPieGS25></NumOfPieGS25>
          </PACGS2>
          <SGICODSD2>
            <SenGooCodSD22></SenGooCodSD22>
            <SenQuaSD23></SenQuaSD23>
          </SGICODSD2>
          <TRACORSECGOO021>
            <NamTRACORSECGOO025></NamTRACORSECGOO025>
            <StrNumTRACORSECGOO027></StrNumTRACORSECGOO027>
            <PosCodTRACORSECGOO026></PosCodTRACORSECGOO026>
            <CitTRACORSECGOO022></CitTRACORSECGOO022>
            <CouCodTRACORSECGOO023></CouCodTRACORSECGOO023>
            <TINTRACORSECGOO028></TINTRACORSECGOO028>
          </TRACORSECGOO021>
          <TRACONSECGOO013>
            <NamTRACONSECGOO017></NamTRACONSECGOO017>
            <StrNumTRACONSECGOO019></StrNumTRACONSECGOO019>
            <PosCodTRACONSECGOO018></PosCodTRACONSECGOO018>
            <CityTRACONSECGOO014></CityTRACONSECGOO014>
            <CouCodTRACONSECGOO015></CouCodTRACONSECGOO015>
            <TINTRACONSECGOO020></TINTRACONSECGOO020>
          </TRACONSECGOO013>
        </GOOITEGDS>
        <ITI>
          <CouOfRouCodITI1></CouOfRouCodITI1>
        </ITI>
        <CARTRA100>
          <NamCARTRA121></NamCARTRA121>
          <StrAndNumCARTRA254></StrAndNumCARTRA254>
          <PosCodCARTRA121></PosCodCARTRA121>
          <CitCARTRA789></CitCARTRA789>
          <CouCodCARTRA587></CouCodCARTRA587>
          <NADCARTRA121></NADCARTRA121>
          <TINCARTRA254></TINCARTRA254>
        </CARTRA100>
        <TRACORSECC037>
          <NamTRACORSEC041></NamTRACORSEC041>
          <StrNumTRACORSEC043></StrNumTRACORSEC043>
          <PosCodTRACORSEC042></PosCodTRACORSEC042>
          <CitTRACORSEC038></CitTRACORSEC038>
          <CouCodTRACORSEC039></CouCodTRACORSEC039>
          <TINTRACORSEC044></TINTRACORSEC044>
        </TRACORSECC037>
        <TRACONSEC029>
          <NameTRACONSEC033></NameTRACONSEC033>
          <StrNumTRAACONSEC035></StrNumTRAACONSEC035>
          <PosCodTRACONSEC034></PosCodTRACONSEC034>
          <CitTRACONSEC030></CitTRACONSEC030>
          <CouCodTRACONSEC031></CouCodTRACONSEC031>
          <TINTRACONSEC036></TINTRACONSEC036>
        </TRACONSEC029>
      </CC015B>

      service.convert(userAnswers)
        .futureValue
        .right.value.toXml.map(scala.xml.Utility.trim) xmlMustEqual expectedXml.map(scala.xml.Utility.trim)
    }
    "Scenario 3: " ignore new Setup {
      val firstGoodItem: Index =  Index(0)
      val secondGoodItem: Index =  Index(1)

      val userAnswers: UserAnswers = emptyUserAnswers
        .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
        .unsafeSetVal(AddSecurityDetailsPage)(true)
        /*
        * General Information Section
        * */
        .unsafeSetVal(DeclarationTypePage)(DeclarationType.Option2)
        .unsafeSetVal(PreLodgeDeclarationPage)(true)
        .unsafeSetVal(ContainersUsedPage)(true)
        .unsafeSetVal(DeclarationPlacePage)("XX1 1XX")
        .unsafeSetVal(DeclarationForSomeoneElsePage)(true)
        .unsafeSetVal(RepresentativeNamePage)("John Doe")
        .unsafeSetVal(RepresentativeCapacityPage)(RepresentativeCapacity.Direct)
        /*
        * RouteDetails
        * */
        .unsafeSetVal(CountryOfDispatchPage)(CountryCode("SC"))
        .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("OOD", "OfficeOfDeparturePage", CountryCode("CC"), Nil, None))
        .unsafeSetVal(DestinationCountryPage)(CountryCode("DC"))
        .unsafeSetVal(MovementDestinationCountryPage)(CountryCode("MD"))
        .unsafeSetVal(DestinationOfficePage)(CustomsOffice("DOP", "DestinationOfficePage", CountryCode("DO"), Nil, None))
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(0)))(CountryCode("OT1"))
        .unsafeSetVal(AddAnotherTransitOfficePage(Index(0)))("AddAnotherTransitOfficePageOT1")
        .unsafeSetVal(ArrivalTimesAtOfficePage(Index(0)))(LocalDateTime.of(2020, 5, 5, 5, 12))
        .unsafeSetVal(AddTransitOfficePage)(true)
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(0)))(CountryCode("OT1"))
        .unsafeSetVal(AddAnotherTransitOfficePage(Index(0)))("AddAnotherTransitOfficePageOT1")
        .unsafeSetVal(ArrivalTimesAtOfficePage(Index(0)))(LocalDateTime.of(2020, 5, 7, 21, 12))
        .unsafeSetVal(AddTransitOfficePage)(false)
        /*
        * Transport Details
        * */
        .unsafeSetVal(InlandModePage)("5")
        .unsafeSetVal(AddIdAtDeparturePage)(false)
        .unsafeSetVal(AddNationalityAtDeparturePage)(true)
        .unsafeSetVal(NationalityAtDeparturePage)(CountryCode("NDP"))
        .unsafeSetVal(ChangeAtBorderPage)(false)
        /*
        * Traders Details
        * */
        .unsafeSetVal(IsPrincipalEoriKnownPage)(false)
        .unsafeSetVal(PrincipalNamePage)("PrincipalName")
        .unsafeSetVal(PrincipalAddressPage)(PrincipalAddress("PrincipalStreet", "PrincipalTown", "AA1 1AA"))
        .unsafeSetVal(AddConsignorPage)(true)
        .unsafeSetVal(pages.IsConsignorEoriKnownPage)(false)
        .unsafeSetVal(pages.ConsignorNamePage)("ConsignorName")
        .unsafeSetVal(pages.ConsignorAddressPage)(ConsignorAddress("ConorLine1", "ConorLine2", "ConorLine3", Country(CountryCode("CN"), "SomethingCO")))
        .unsafeSetVal(AddConsigneePage)(true)
        .unsafeSetVal(pages.IsConsigneeEoriKnownPage)(false)
        .unsafeSetVal(pages.ConsigneeNamePage)("ConsigneeName")
        .unsafeSetVal(pages.ConsigneeAddressPage)(ConsigneeAddress("ConeeLine1", "ConeeLine2", "ConeeLine3", Country(CountryCode("CN"), "SomethingCE")))
        /*
        * Safety & Security Details
        * */
        .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
        .unsafeSetVal(AddTransportChargesPaymentMethodPage)(false)
        .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
        .unsafeSetVal(pages.safetyAndSecurity.AddConveyanceReferenceNumberPage)(false)
        .unsafeSetVal(PlaceOfUnloadingCodePage)("PlaceOfUnloadingPage")
        .unsafeSetVal(CountryOfRoutingPage(Index(0)))(CountryCode("CORP1"))
        .unsafeSetVal(AddAnotherCountryOfRoutingPage)(false)
        .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(true)
        .unsafeSetVal(pages.safetyAndSecurity.AddSafetyAndSecurityConsignorEoriPage)(false)
        .unsafeSetVal(pages.safetyAndSecurity.SafetyAndSecurityConsignorNamePage)("SafeSecName")
        .unsafeSetVal(pages.safetyAndSecurity.SafetyAndSecurityConsignorAddressPage)(ConsignorAddress("SecConorLine1", "SecConorLine2", "SecConorLine3", Country(CountryCode("CN"), "SomethingSecCO")))
        .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(true)
        .unsafeSetVal(pages.safetyAndSecurity.AddSafetyAndSecurityConsigneeEoriPage)(false)
        .unsafeSetVal(pages.safetyAndSecurity.SafetyAndSecurityConsigneeNamePage)("SafeSecName")
        .unsafeSetVal(pages.safetyAndSecurity.SafetyAndSecurityConsigneeAddressPage)(ConsigneeAddress("SecConeeLine1", "SecConeeLine2", "SecConeeLine3", Country(CountryCode("CN"), "SomethingSecCE")))
        .unsafeSetVal(AddCarrierPage)(false)
        /*
        * Item Details section - Item One
        * */
        .unsafeSetVal(ItemDescriptionPage(firstGoodItem))("ItemOnesDescription")
        .unsafeSetVal(ItemTotalGrossMassPage(firstGoodItem))("25000")
        .unsafeSetVal(AddTotalNetMassPage(firstGoodItem))(true)
        .unsafeSetVal(TotalNetMassPage(firstGoodItem))("12342")
        .unsafeSetVal(pages.IsCommodityCodeKnownPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.CommodityCodePage(firstGoodItem))("ComoCode1")
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
        .unsafeSetVal(pages.addItems.specialMentions.SpecialMentionTypePage(firstGoodItem, Index(0)))("GD1SPMT1")
        .unsafeSetVal(pages.addItems.specialMentions.SpecialMentionAdditionalInfoPage(firstGoodItem, Index(0)))("GD1SPMT1Info")
        .unsafeSetVal(pages.addItems.specialMentions.AddAnotherSpecialMentionPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.specialMentions.SpecialMentionTypePage(firstGoodItem, Index(0)))("GD1SPMT2")
        .unsafeSetVal(pages.addItems.specialMentions.SpecialMentionAdditionalInfoPage(firstGoodItem, Index(0)))("GD1SPMT2Info")
        .unsafeSetVal(pages.addItems.specialMentions.AddAnotherSpecialMentionPage(firstGoodItem))(false)
        .unsafeSetVal(pages.addItems.AddDocumentsPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.DocumentTypePage(firstGoodItem, Index(0)))("GD1DOC1")
        .unsafeSetVal(pages.addItems.DocumentReferencePage(firstGoodItem, Index(0)))("GD1DOC1Ref")
        .unsafeSetVal(pages.addItems.AddExtraDocumentInformationPage(firstGoodItem, Index(0)))(true)
        .unsafeSetVal(pages.addItems.DocumentExtraInformationPage(firstGoodItem, Index(0)))("GD1DOC1Info")
        .unsafeSetVal(pages.addItems.AddAnotherDocumentPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.DocumentTypePage(firstGoodItem, Index(1)))("GD1DOC2")
        .unsafeSetVal(pages.addItems.DocumentReferencePage(firstGoodItem, Index(1)))("GD1DOC2Ref")
        .unsafeSetVal(pages.addItems.AddExtraDocumentInformationPage(firstGoodItem, Index(1)))(false)
        .unsafeSetVal(pages.addItems.AddAnotherDocumentPage(firstGoodItem))(false)
        .unsafeSetVal(pages.addItems.AddAdministrativeReferencePage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.ReferenceTypePage(firstGoodItem, Index(0)))("GD1PREREF1")
        .unsafeSetVal(pages.addItems.PreviousReferencePage(firstGoodItem, Index(0)))("GD1PREREF1Ref")
        .unsafeSetVal(pages.addItems.AddExtraInformationPage(firstGoodItem, Index(0)))(true)
        .unsafeSetVal(pages.addItems.ExtraInformationPage(firstGoodItem, Index(0)))("GD1PREREF1Info")
        .unsafeSetVal(pages.addItems.AddAnotherPreviousAdministrativeReferencePage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.ReferenceTypePage(firstGoodItem, Index(1)))("GD1PREREF2")
        .unsafeSetVal(pages.addItems.PreviousReferencePage(firstGoodItem, Index(1)))("GD1PREREF2Ref")
        .unsafeSetVal(pages.addItems.AddExtraInformationPage(firstGoodItem, Index(1)))(false)
        .unsafeSetVal(pages.addItems.AddAnotherPreviousAdministrativeReferencePage(firstGoodItem))(false)
        .unsafeSetVal(pages.addItems.securityDetails.TransportChargesPage(firstGoodItem))("MoneyCard")
        .unsafeSetVal(pages.addItems.securityDetails.CommercialReferenceNumberPage(firstGoodItem))("GD1CRN")
        .unsafeSetVal(pages.addItems.securityDetails.AddDangerousGoodsCodePage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.securityDetails.DangerousGoodsCodePage(firstGoodItem))("GD1DGGDSCODE")
        .unsafeSetVal(pages.addItems.traderSecurityDetails.AddSecurityConsignorsEoriPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.traderSecurityDetails.SecurityConsignorEoriPage(firstGoodItem))("GD1SECCONOR")
        .unsafeSetVal(pages.addItems.traderSecurityDetails.AddSecurityConsigneesEoriPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.traderSecurityDetails.SecurityConsigneeEoriPage(firstGoodItem))("GD1SECCONEE")
        .unsafeSetVal(pages.addItems.AddAnotherItemPage)(false)
        /*
        * Goods Summary
        */
        .unsafeSetVal(pages.DeclarePackagesPage)(true)
        .unsafeSetVal(pages.TotalPackagesPage)(1)
        .unsafeSetVal(pages.TotalGrossMassPage)("12131415")
        .unsafeSetVal(pages.LoadingPlacePage)("LoadPLace")
        .unsafeSetVal(pages.AddCustomsApprovedLocationPage)(false)
        .unsafeSetVal(pages.AddSealsPage)(true)
        .unsafeSetVal(pages.SealIdDetailsPage(Index(0)))(SealDomain("SEAL1"))
        .unsafeSetVal(pages.SealsInformationPage)(false)
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

      val expectedXml = <CC015B>
        <SynIdeMES1>UNOC</SynIdeMES1>
        <SynVerNumMES2>3</SynVerNumMES2>
        <MesSenMES3>NCTS</MesSenMES3>
        <MesRecMES6>NCTS</MesRecMES6>
        <DatOfPreMES9>20201212</DatOfPreMES9>
        <TimOfPreMES10>2030</TimOfPreMES10>
        <IntConRefMES11>DFnull150</IntConRefMES11>
        <AppRefMES14>NCTS</AppRefMES14>
        <MesIdeMES19>1</MesIdeMES19>
        <MesTypMES20>GB015B</MesTypMES20>
        <HEAHEA>
          <RefNumHEA4></RefNumHEA4>
          <TypOfDecHEA24></TypOfDecHEA24>
          <CouOfDesCodHEA30></CouOfDesCodHEA30>
          <AgrLocOfGooCodHEA38></AgrLocOfGooCodHEA38>
          <AgrLocOfGooHEA39></AgrLocOfGooHEA39>
          <AutLocOfGooCodHEA41></AutLocOfGooCodHEA41>
          <PlaOfLoaCodHEA46></PlaOfLoaCodHEA46>
          <CouOfDisCodHEA55></CouOfDisCodHEA55>
          <CusSubPlaHEA66></CusSubPlaHEA66>
          <TraModAtBorHEA76></TraModAtBorHEA76>
          <IdeOfMeaOfTraAtDHEA78></IdeOfMeaOfTraAtDHEA78>
          <NatOfMeaOfTraAtDHEA80></NatOfMeaOfTraAtDHEA80>
          <IdeOfMeaOfTraCroHEA85></IdeOfMeaOfTraCroHEA85>
          <NatOfMeaOfTraCroHEA87></NatOfMeaOfTraCroHEA87>
          <ConIndHEA96></ConIndHEA96>
          <NCTSAccDocHEA601LNG></NCTSAccDocHEA601LNG>
          <TotNumOfIteHEA305></TotNumOfIteHEA305>
          <TotNumOfPacHEA306></TotNumOfPacHEA306>
          <TotGroMasHEA307></TotGroMasHEA307>
          <DecDatHEA383></DecDatHEA383>
          <DecPlaHEA394></DecPlaHEA394>
          <SpeCirIndHEA1></SpeCirIndHEA1>
          <TraChaMetOfPayHEA1></TraChaMetOfPayHEA1>
          <ComRefNumHEA></ComRefNumHEA>
          <CodPlUnHEA357></CodPlUnHEA357>
        </HEAHEA>
        <TRAPRIPC1>
          <NamPC17>PrincipalName</NamPC17>
          <StrAndNumPC122>PrincipalStreet</StrAndNumPC122>
          <PosCodPC123>AA1 1AA</PosCodPC123>
          <CitPC124>PrincipalTown</CitPC124>
          <CouPC125>GB</CouPC125>
          <TINPC159></TINPC159>
          <HITPC126></HITPC126>
        </TRAPRIPC1>
        <TRACONCO1>
          <NamCO17></NamCO17>
          <StrAndNumCO122></StrAndNumCO122>
          <PosCodCO123></PosCodCO123>
          <CitCO124></CitCO124>
          <CouCO125></CouCO125>
          <TINCO159></TINCO159>
        </TRACONCO1>
        <TRACONCE1>
          <NamCE17></NamCE17>
          <StrAndNumCE122></StrAndNumCE122>
          <PosCodCE123></PosCodCE123>
          <CitCE124></CitCE124>
          <CouCE125></CouCE125>
          <TINCE159></TINCE159>
        </TRACONCE1>
        <TRAAUTCONTRA>
          <TINTRA59></TINTRA59>
        </TRAAUTCONTRA>
        <CUSOFFDEPEPT>
          <RefNumEPT1></RefNumEPT1>
        </CUSOFFDEPEPT>
        <CUSOFFTRARNS>
          <RefNumRNS1></RefNumRNS1>
          <ArrTimTRACUS085></ArrTimTRACUS085>
        </CUSOFFTRARNS>
        <CUSOFFDESEST>
          <RefNumEST1></RefNumEST1>
        </CUSOFFDESEST>
        <CONRESERS>
          <ConResCodERS16></ConResCodERS16>
          <DatLimERS69></DatLimERS69>
        </CONRESERS>
        <REPREP>
          <NamREP5></NamREP5>
          <RepCapREP18></RepCapREP18>
        </REPREP>
        <SEAINFSLIType>
          <SeaNumSL12></SeaNumSL12>
          <SEAIDSID>
            <SeaIdeSID1></SeaIdeSID1>
          </SEAIDSID>
        </SEAINFSLIType>
        <GUAGUA>
          <GuaTypGUA1></GuaTypGUA1>
          <GUAREFREF>
            <GuaRefNumGRNREF1></GuaRefNumGRNREF1>
            <OthGuaRefREF4></OthGuaRefREF4>
            <AccCodeREF6></AccCodeREF6>
            <VALLIMECVLE>
              <NotValForECVLE1></NotValForECVLE1>
            </VALLIMECVLE>
            <VALLIMNONECLIM>
              <NotValForOthConPLIM2></NotValForOthConPLIM2>
            </VALLIMNONECLIM>
          </GUAREFREF>
        </GUAGUA>
        <GOOITEGDS>
          <IteNumGDS7>1</IteNumGDS7>
          <ComCodTarCodGDS10></ComCodTarCodGDS10>
          <DecTypGDS15></DecTypGDS15>
          <GooDesGDS23></GooDesGDS23>
          <GroMasGDS46></GroMasGDS46>
          <NetMasGDS48></NetMasGDS48>
          <CouOfDisGDS58></CouOfDisGDS58>
          <CouOfDesGDS59></CouOfDesGDS59>
          <MetOfPayGDI12></MetOfPayGDI12>
          <ComRefNumGIM1></ComRefNumGIM1>
          <UNDanGooCodGDI1></UNDanGooCodGDI1>
          <PREADMREFAR2>
            <PreDocTypAR21></PreDocTypAR21>
            <PreDocRefAR26></PreDocRefAR26>
            <ComOfInfAR29></ComOfInfAR29>
          </PREADMREFAR2>
          <PRODOCDC2>
            <DocTypDC21>GD2DOC1</DocTypDC21>
            <DocRefDC23>GD2DOC1Ref</DocRefDC23>
            <ComOfInfDC25>GD2DOC1Info</ComOfInfDC25>
          </PRODOCDC2>
          <SPEMENMT2>
            <AddInfMT21></AddInfMT21>
            <AddInfCodMT23></AddInfCodMT23>
            <ExpFroECMT24></ExpFroECMT24>
            <ExpFroCouMT25></ExpFroCouMT25>
          </SPEMENMT2>
          <TRACONCO2>
            <NamCO27></NamCO27>
            <StrAndNumCO222></StrAndNumCO222>
            <PosCodCO223></PosCodCO223>
            <CitCO224></CitCO224>
            <CouCO225></CouCO225>
            <TINCO259></TINCO259>
          </TRACONCO2>
          <TRACONCE2>
            <NamCE27></NamCE27>
            <StrAndNumCE222></StrAndNumCE222>
            <PosCodCE223></PosCodCE223>
            <CitCE224></CitCE224>
            <CouCE225></CouCE225>
            <TINCE259></TINCE259>
          </TRACONCE2>
          <CONNR2>
            <ConNumNR21></ConNumNR21>
          </CONNR2>
          <PACGS2>
            <MarNumOfPacGS21></MarNumOfPacGS21>
            <KinOfPacGS23></KinOfPacGS23>
            <NumOfPacGS24></NumOfPacGS24>
            <NumOfPieGS25></NumOfPieGS25>
          </PACGS2>
          <SGICODSD2>
            <SenGooCodSD22></SenGooCodSD22>
            <SenQuaSD23></SenQuaSD23>
          </SGICODSD2>
          <TRACORSECGOO021>
            <NamTRACORSECGOO025></NamTRACORSECGOO025>
            <StrNumTRACORSECGOO027></StrNumTRACORSECGOO027>
            <PosCodTRACORSECGOO026></PosCodTRACORSECGOO026>
            <CitTRACORSECGOO022></CitTRACORSECGOO022>
            <CouCodTRACORSECGOO023></CouCodTRACORSECGOO023>
            <TINTRACORSECGOO028></TINTRACORSECGOO028>
          </TRACORSECGOO021>
          <TRACONSECGOO013>
            <NamTRACONSECGOO017></NamTRACONSECGOO017>
            <StrNumTRACONSECGOO019></StrNumTRACONSECGOO019>
            <PosCodTRACONSECGOO018></PosCodTRACONSECGOO018>
            <CityTRACONSECGOO014></CityTRACONSECGOO014>
            <CouCodTRACONSECGOO015></CouCodTRACONSECGOO015>
            <TINTRACONSECGOO020></TINTRACONSECGOO020>
          </TRACONSECGOO013>
        </GOOITEGDS>
        <ITI>
          <CouOfRouCodITI1></CouOfRouCodITI1>
        </ITI>
        <CARTRA100>
          <NamCARTRA121></NamCARTRA121>
          <StrAndNumCARTRA254></StrAndNumCARTRA254>
          <PosCodCARTRA121></PosCodCARTRA121>
          <CitCARTRA789></CitCARTRA789>
          <CouCodCARTRA587></CouCodCARTRA587>
          <NADCARTRA121></NADCARTRA121>
          <TINCARTRA254></TINCARTRA254>
        </CARTRA100>
        <TRACORSECC037>
          <NamTRACORSEC041></NamTRACORSEC041>
          <StrNumTRACORSEC043></StrNumTRACORSEC043>
          <PosCodTRACORSEC042></PosCodTRACORSEC042>
          <CitTRACORSEC038></CitTRACORSEC038>
          <CouCodTRACORSEC039></CouCodTRACORSEC039>
          <TINTRACORSEC044></TINTRACORSEC044>
        </TRACORSECC037>
        <TRACONSEC029>
          <NameTRACONSEC033></NameTRACONSEC033>
          <StrNumTRAACONSEC035></StrNumTRAACONSEC035>
          <PosCodTRACONSEC034></PosCodTRACONSEC034>
          <CitTRACONSEC030></CitTRACONSEC030>
          <CouCodTRACONSEC031></CouCodTRACONSEC031>
          <TINTRACONSEC036></TINTRACONSEC036>
        </TRACONSEC029>
      </CC015B>

      service.convert(userAnswers)
        .futureValue
        .right.value.toXml.map(scala.xml.Utility.trim) xmlMustEqual expectedXml.map(scala.xml.Utility.trim)
    }
    "Scenario 4: " ignore new Setup {
      val firstGoodItem: Index =  Index(0)
      val secondGoodItem: Index =  Index(1)

      val userAnswers: UserAnswers = emptyUserAnswers
        .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
        .unsafeSetVal(AddSecurityDetailsPage)(true)
        /*
        * General Information Section
        * */
        .unsafeSetVal(DeclarationTypePage)(DeclarationType.Option2)
        .unsafeSetVal(PreLodgeDeclarationPage)(true)
        .unsafeSetVal(ContainersUsedPage)(true)
        .unsafeSetVal(DeclarationPlacePage)("XX1 1XX")
        .unsafeSetVal(DeclarationForSomeoneElsePage)(true)
        .unsafeSetVal(RepresentativeNamePage)("John Doe")
        .unsafeSetVal(RepresentativeCapacityPage)(RepresentativeCapacity.Direct)
        /*
        * RouteDetails
        * */
        .unsafeSetVal(CountryOfDispatchPage)(CountryCode("SC"))
        .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("OOD", "OfficeOfDeparturePage", CountryCode("CC"), Nil, None))
        .unsafeSetVal(DestinationCountryPage)(CountryCode("DC"))
        .unsafeSetVal(MovementDestinationCountryPage)(CountryCode("MD"))
        .unsafeSetVal(DestinationOfficePage)(CustomsOffice("DOP", "DestinationOfficePage", CountryCode("DO"), Nil, None))
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(0)))(CountryCode("OT1"))
        .unsafeSetVal(AddAnotherTransitOfficePage(Index(0)))("AddAnotherTransitOfficePageOT1")
        .unsafeSetVal(ArrivalTimesAtOfficePage(Index(0)))(LocalDateTime.of(2020, 5, 5, 5, 12))
        .unsafeSetVal(AddTransitOfficePage)(true)
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(0)))(CountryCode("OT1"))
        .unsafeSetVal(AddAnotherTransitOfficePage(Index(0)))("AddAnotherTransitOfficePageOT1")
        .unsafeSetVal(ArrivalTimesAtOfficePage(Index(0)))(LocalDateTime.of(2020, 5, 7, 21, 12))
        .unsafeSetVal(AddTransitOfficePage)(false)
        /*
        * Transport Details
        * */
        .unsafeSetVal(InlandModePage)("2")
        .unsafeSetVal(AddIdAtDeparturePage)(false)
        .unsafeSetVal(pages.IdAtDeparturePage)("IDADEP")
        .unsafeSetVal(ChangeAtBorderPage)(false)
        /*
        * Traders Details
        * */
        .unsafeSetVal(IsPrincipalEoriKnownPage)(false)
        .unsafeSetVal(PrincipalNamePage)("PrincipalName")
        .unsafeSetVal(PrincipalAddressPage)(PrincipalAddress("PrincipalStreet", "PrincipalTown", "AA1 1AA"))
        .unsafeSetVal(AddConsignorPage)(false)
        .unsafeSetVal(AddConsigneePage)(false)
        /*
        * Safety & Security Details
        * */
        .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
        .unsafeSetVal(CircumstanceIndicatorPage)("E")
        .unsafeSetVal(AddTransportChargesPaymentMethodPage)(false)
        .unsafeSetVal(AddCommercialReferenceNumberPage)(true)
        .unsafeSetVal(pages.safetyAndSecurity.AddCommercialReferenceNumberAllItemsPage)(true)
        .unsafeSetVal(pages.safetyAndSecurity.CommercialReferenceNumberAllItemsPage)("COMREFALL")
        .unsafeSetVal(pages.safetyAndSecurity.AddConveyanceReferenceNumberPage)(false)
        .unsafeSetVal(pages.safetyAndSecurity.AddPlaceOfUnloadingCodePage)(false)
        .unsafeSetVal(CountryOfRoutingPage(Index(0)))(CountryCode("CORP1"))
        .unsafeSetVal(AddAnotherCountryOfRoutingPage)(false)
        .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(true)
        .unsafeSetVal(pages.safetyAndSecurity.AddSafetyAndSecurityConsignorEoriPage)(true)
        .unsafeSetVal(pages.safetyAndSecurity.SafetyAndSecurityConsignorEoriPage)("SafeSecConorEori")
        .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(true)
        .unsafeSetVal(pages.safetyAndSecurity.AddSafetyAndSecurityConsigneeEoriPage)(true)
        .unsafeSetVal(pages.safetyAndSecurity.SafetyAndSecurityConsigneeEoriPage)("SafeSecConeeEori")
        .unsafeSetVal(AddCarrierPage)(true)
        .unsafeSetVal(AddCarrierEoriPage)(false)
        .unsafeSetVal(pages.safetyAndSecurity.CarrierNamePage)("CarrierName")
        .unsafeSetVal(pages.safetyAndSecurity.CarrierAddressPage)(CarrierAddress("CarAddLin1", "CarAddLin2", "CarAddLin3", Country(CountryCode("CA"), "CARRDESC")))
        /*
        * Item Details section - Item One
        * */
        .unsafeSetVal(ItemDescriptionPage(firstGoodItem))("ItemOnesDescription")
        .unsafeSetVal(ItemTotalGrossMassPage(firstGoodItem))("25000")
        .unsafeSetVal(AddTotalNetMassPage(firstGoodItem))(true)
        .unsafeSetVal(TotalNetMassPage(firstGoodItem))("12342")
        .unsafeSetVal(pages.IsCommodityCodeKnownPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.CommodityCodePage(firstGoodItem))("ComoCode1")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorEoriKnownPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorEoriNumberPage(firstGoodItem))("Conor123")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorNamePage(firstGoodItem))("ConorName")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorAddressPage(firstGoodItem))(ConsignorAddress("ConorLine1", "ConorLine2", "ConorLine3", Country(CountryCode("GD1"), "SomethingCO")))
        .unsafeSetVal(TraderDetailsConsigneeEoriKnownPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeEoriNumberPage(firstGoodItem))("Conee123")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeNamePage(firstGoodItem))("ConeeName")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeAddressPage(firstGoodItem))(ConsigneeAddress("ConeeLine1", "ConeeLine2", "ConeeLine3", Country(CountryCode("GD1"), "SomethingCE")))
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
        .unsafeSetVal(pages.addItems.specialMentions.SpecialMentionTypePage(firstGoodItem, Index(0)))("GD1SPMT1")
        .unsafeSetVal(pages.addItems.specialMentions.SpecialMentionAdditionalInfoPage(firstGoodItem, Index(0)))("GD1SPMT1Info")
        .unsafeSetVal(pages.addItems.specialMentions.AddAnotherSpecialMentionPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.specialMentions.SpecialMentionTypePage(firstGoodItem, Index(0)))("GD1SPMT2")
        .unsafeSetVal(pages.addItems.specialMentions.SpecialMentionAdditionalInfoPage(firstGoodItem, Index(0)))("GD1SPMT2Info")
        .unsafeSetVal(pages.addItems.specialMentions.AddAnotherSpecialMentionPage(firstGoodItem))(false)
        .unsafeSetVal(pages.addItems.AddDocumentsPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.DocumentTypePage(firstGoodItem, Index(0)))("GD1DOC1")
        .unsafeSetVal(pages.addItems.DocumentReferencePage(firstGoodItem, Index(0)))("GD1DOC1Ref")
        .unsafeSetVal(pages.addItems.AddExtraDocumentInformationPage(firstGoodItem, Index(0)))(true)
        .unsafeSetVal(pages.addItems.DocumentExtraInformationPage(firstGoodItem, Index(0)))("GD1DOC1Info")
        .unsafeSetVal(pages.addItems.AddAnotherDocumentPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.DocumentTypePage(firstGoodItem, Index(1)))("GD1DOC2")
        .unsafeSetVal(pages.addItems.DocumentReferencePage(firstGoodItem, Index(1)))("GD1DOC2Ref")
        .unsafeSetVal(pages.addItems.AddExtraDocumentInformationPage(firstGoodItem, Index(1)))(false)
        .unsafeSetVal(pages.addItems.AddAnotherDocumentPage(firstGoodItem))(false)
        .unsafeSetVal(pages.addItems.AddAdministrativeReferencePage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.ReferenceTypePage(firstGoodItem, Index(0)))("GD1PREREF1")
        .unsafeSetVal(pages.addItems.PreviousReferencePage(firstGoodItem, Index(0)))("GD1PREREF1Ref")
        .unsafeSetVal(pages.addItems.AddExtraInformationPage(firstGoodItem, Index(0)))(true)
        .unsafeSetVal(pages.addItems.ExtraInformationPage(firstGoodItem, Index(0)))("GD1PREREF1Info")
        .unsafeSetVal(pages.addItems.AddAnotherPreviousAdministrativeReferencePage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.ReferenceTypePage(firstGoodItem, Index(1)))("GD1PREREF2")
        .unsafeSetVal(pages.addItems.PreviousReferencePage(firstGoodItem, Index(1)))("GD1PREREF2Ref")
        .unsafeSetVal(pages.addItems.AddExtraInformationPage(firstGoodItem, Index(1)))(false)
        .unsafeSetVal(pages.addItems.AddAnotherPreviousAdministrativeReferencePage(firstGoodItem))(false)
        .unsafeSetVal(pages.addItems.securityDetails.TransportChargesPage(firstGoodItem))("MoneyCardWee")
        .unsafeSetVal(pages.addItems.securityDetails.CommercialReferenceNumberPage(firstGoodItem))("GD1CRN")
        .unsafeSetVal(pages.addItems.securityDetails.AddDangerousGoodsCodePage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.securityDetails.DangerousGoodsCodePage(firstGoodItem))("GD1DGGDSCODE")
        .unsafeSetVal(pages.addItems.traderSecurityDetails.AddSecurityConsignorsEoriPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.traderSecurityDetails.SecurityConsignorEoriPage(firstGoodItem))("GD1SECCONOR")
        .unsafeSetVal(pages.addItems.traderSecurityDetails.AddSecurityConsigneesEoriPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.traderSecurityDetails.SecurityConsigneeEoriPage(firstGoodItem))("GD1SECCONEE")
        /*
        * Goods Summary
        */
        .unsafeSetVal(pages.DeclarePackagesPage)(true)
        .unsafeSetVal(pages.TotalPackagesPage)(1)
        .unsafeSetVal(pages.TotalGrossMassPage)("12131415")
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

      val expectedXml = <CC015B>
        <SynIdeMES1>UNOC</SynIdeMES1>
        <SynVerNumMES2>3</SynVerNumMES2>
        <MesSenMES3>NCTS</MesSenMES3>
        <MesRecMES6>NCTS</MesRecMES6>
        <DatOfPreMES9>20201212</DatOfPreMES9>
        <TimOfPreMES10>2030</TimOfPreMES10>
        <IntConRefMES11>DFnull150</IntConRefMES11>
        <AppRefMES14>NCTS</AppRefMES14>
        <MesIdeMES19>1</MesIdeMES19>
        <MesTypMES20>GB015B</MesTypMES20>
        <HEAHEA>
          <RefNumHEA4></RefNumHEA4>
          <TypOfDecHEA24></TypOfDecHEA24>
          <CouOfDesCodHEA30></CouOfDesCodHEA30>
          <AgrLocOfGooCodHEA38></AgrLocOfGooCodHEA38>
          <AgrLocOfGooHEA39></AgrLocOfGooHEA39>
          <AutLocOfGooCodHEA41></AutLocOfGooCodHEA41>
          <PlaOfLoaCodHEA46></PlaOfLoaCodHEA46>
          <CouOfDisCodHEA55></CouOfDisCodHEA55>
          <CusSubPlaHEA66></CusSubPlaHEA66>
          <TraModAtBorHEA76></TraModAtBorHEA76>
          <IdeOfMeaOfTraAtDHEA78></IdeOfMeaOfTraAtDHEA78>
          <NatOfMeaOfTraAtDHEA80></NatOfMeaOfTraAtDHEA80>
          <IdeOfMeaOfTraCroHEA85></IdeOfMeaOfTraCroHEA85>
          <NatOfMeaOfTraCroHEA87></NatOfMeaOfTraCroHEA87>
          <ConIndHEA96></ConIndHEA96>
          <NCTSAccDocHEA601LNG></NCTSAccDocHEA601LNG>
          <TotNumOfIteHEA305></TotNumOfIteHEA305>
          <TotNumOfPacHEA306></TotNumOfPacHEA306>
          <TotGroMasHEA307></TotGroMasHEA307>
          <DecDatHEA383></DecDatHEA383>
          <DecPlaHEA394></DecPlaHEA394>
          <SpeCirIndHEA1></SpeCirIndHEA1>
          <TraChaMetOfPayHEA1></TraChaMetOfPayHEA1>
          <ComRefNumHEA></ComRefNumHEA>
          <CodPlUnHEA357></CodPlUnHEA357>
        </HEAHEA>
        <TRAPRIPC1>
          <NamPC17>PrincipalName</NamPC17>
          <StrAndNumPC122>PrincipalStreet</StrAndNumPC122>
          <PosCodPC123>AA1 1AA</PosCodPC123>
          <CitPC124>PrincipalTown</CitPC124>
          <CouPC125>GB</CouPC125>
          <TINPC159></TINPC159>
          <HITPC126></HITPC126>
        </TRAPRIPC1>
        <TRACONCO1>
          <NamCO17></NamCO17>
          <StrAndNumCO122></StrAndNumCO122>
          <PosCodCO123></PosCodCO123>
          <CitCO124></CitCO124>
          <CouCO125></CouCO125>
          <TINCO159></TINCO159>
        </TRACONCO1>
        <TRACONCE1>
          <NamCE17></NamCE17>
          <StrAndNumCE122></StrAndNumCE122>
          <PosCodCE123></PosCodCE123>
          <CitCE124></CitCE124>
          <CouCE125></CouCE125>
          <TINCE159></TINCE159>
        </TRACONCE1>
        <TRAAUTCONTRA>
          <TINTRA59></TINTRA59>
        </TRAAUTCONTRA>
        <CUSOFFDEPEPT>
          <RefNumEPT1></RefNumEPT1>
        </CUSOFFDEPEPT>
        <CUSOFFTRARNS>
          <RefNumRNS1></RefNumRNS1>
          <ArrTimTRACUS085></ArrTimTRACUS085>
        </CUSOFFTRARNS>
        <CUSOFFDESEST>
          <RefNumEST1></RefNumEST1>
        </CUSOFFDESEST>
        <CONRESERS>
          <ConResCodERS16></ConResCodERS16>
          <DatLimERS69></DatLimERS69>
        </CONRESERS>
        <REPREP>
          <NamREP5></NamREP5>
          <RepCapREP18></RepCapREP18>
        </REPREP>
        <SEAINFSLIType>
          <SeaNumSL12></SeaNumSL12>
          <SEAIDSID>
            <SeaIdeSID1></SeaIdeSID1>
          </SEAIDSID>
        </SEAINFSLIType>
        <GUAGUA>
          <GuaTypGUA1></GuaTypGUA1>
          <GUAREFREF>
            <GuaRefNumGRNREF1></GuaRefNumGRNREF1>
            <OthGuaRefREF4></OthGuaRefREF4>
            <AccCodeREF6></AccCodeREF6>
            <VALLIMECVLE>
              <NotValForECVLE1></NotValForECVLE1>
            </VALLIMECVLE>
            <VALLIMNONECLIM>
              <NotValForOthConPLIM2></NotValForOthConPLIM2>
            </VALLIMNONECLIM>
          </GUAREFREF>
        </GUAGUA>
        <GOOITEGDS>
          <IteNumGDS7>1</IteNumGDS7>
          <ComCodTarCodGDS10></ComCodTarCodGDS10>
          <DecTypGDS15></DecTypGDS15>
          <GooDesGDS23></GooDesGDS23>
          <GroMasGDS46></GroMasGDS46>
          <NetMasGDS48></NetMasGDS48>
          <CouOfDisGDS58></CouOfDisGDS58>
          <CouOfDesGDS59></CouOfDesGDS59>
          <MetOfPayGDI12></MetOfPayGDI12>
          <ComRefNumGIM1></ComRefNumGIM1>
          <UNDanGooCodGDI1></UNDanGooCodGDI1>
          <PREADMREFAR2>
            <PreDocTypAR21></PreDocTypAR21>
            <PreDocRefAR26></PreDocRefAR26>
            <ComOfInfAR29></ComOfInfAR29>
          </PREADMREFAR2>
          <PRODOCDC2>
            <DocTypDC21></DocTypDC21>
            <DocRefDC23></DocRefDC23>
            <ComOfInfDC25></ComOfInfDC25>
          </PRODOCDC2>
          <SPEMENMT2>
            <AddInfMT21></AddInfMT21>
            <AddInfCodMT23></AddInfCodMT23>
            <ExpFroECMT24></ExpFroECMT24>
            <ExpFroCouMT25></ExpFroCouMT25>
          </SPEMENMT2>
          <TRACONCO2>
            <NamCO27></NamCO27>
            <StrAndNumCO222></StrAndNumCO222>
            <PosCodCO223></PosCodCO223>
            <CitCO224></CitCO224>
            <CouCO225></CouCO225>
            <TINCO259></TINCO259>
          </TRACONCO2>
          <TRACONCE2>
            <NamCE27></NamCE27>
            <StrAndNumCE222></StrAndNumCE222>
            <PosCodCE223></PosCodCE223>
            <CitCE224></CitCE224>
            <CouCE225></CouCE225>
            <TINCE259></TINCE259>
          </TRACONCE2>
          <CONNR2>
            <ConNumNR21></ConNumNR21>
          </CONNR2>
          <PACGS2>
            <MarNumOfPacGS21></MarNumOfPacGS21>
            <KinOfPacGS23></KinOfPacGS23>
            <NumOfPacGS24></NumOfPacGS24>
            <NumOfPieGS25></NumOfPieGS25>
          </PACGS2>
          <SGICODSD2>
            <SenGooCodSD22></SenGooCodSD22>
            <SenQuaSD23></SenQuaSD23>
          </SGICODSD2>
          <TRACORSECGOO021>
            <NamTRACORSECGOO025></NamTRACORSECGOO025>
            <StrNumTRACORSECGOO027></StrNumTRACORSECGOO027>
            <PosCodTRACORSECGOO026></PosCodTRACORSECGOO026>
            <CitTRACORSECGOO022></CitTRACORSECGOO022>
            <CouCodTRACORSECGOO023></CouCodTRACORSECGOO023>
            <TINTRACORSECGOO028></TINTRACORSECGOO028>
          </TRACORSECGOO021>
          <TRACONSECGOO013>
            <NamTRACONSECGOO017></NamTRACONSECGOO017>
            <StrNumTRACONSECGOO019></StrNumTRACONSECGOO019>
            <PosCodTRACONSECGOO018></PosCodTRACONSECGOO018>
            <CityTRACONSECGOO014></CityTRACONSECGOO014>
            <CouCodTRACONSECGOO015></CouCodTRACONSECGOO015>
            <TINTRACONSECGOO020></TINTRACONSECGOO020>
          </TRACONSECGOO013>
        </GOOITEGDS>
        <ITI>
          <CouOfRouCodITI1></CouOfRouCodITI1>
        </ITI>
        <CARTRA100>
          <NamCARTRA121></NamCARTRA121>
          <StrAndNumCARTRA254></StrAndNumCARTRA254>
          <PosCodCARTRA121></PosCodCARTRA121>
          <CitCARTRA789></CitCARTRA789>
          <CouCodCARTRA587></CouCodCARTRA587>
          <NADCARTRA121></NADCARTRA121>
          <TINCARTRA254></TINCARTRA254>
        </CARTRA100>
        <TRACORSECC037>
          <NamTRACORSEC041></NamTRACORSEC041>
          <StrNumTRACORSEC043></StrNumTRACORSEC043>
          <PosCodTRACORSEC042></PosCodTRACORSEC042>
          <CitTRACORSEC038></CitTRACORSEC038>
          <CouCodTRACORSEC039></CouCodTRACORSEC039>
          <TINTRACORSEC044></TINTRACORSEC044>
        </TRACORSECC037>
        <TRACONSEC029>
          <NameTRACONSEC033></NameTRACONSEC033>
          <StrNumTRAACONSEC035></StrNumTRAACONSEC035>
          <PosCodTRACONSEC034></PosCodTRACONSEC034>
          <CitTRACONSEC030></CitTRACONSEC030>
          <CouCodTRACONSEC031></CouCodTRACONSEC031>
          <TINTRACONSEC036></TINTRACONSEC036>
        </TRACONSEC029>
      </CC015B>

      service.convert(userAnswers)
        .futureValue
        .right.value.toXml.map(scala.xml.Utility.trim) xmlMustEqual expectedXml.map(scala.xml.Utility.trim)
    }
  }
}
