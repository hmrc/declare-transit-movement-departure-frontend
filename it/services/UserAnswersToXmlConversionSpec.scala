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

import itUtils.{MockDateTimeService, XMLComparatorSpec, XSDSchemaValidationSpec}
import commonTestUtils.UserAnswersSpecHelper
import models.domain.SealDomain
import models.reference.{Country, CountryCode, CountryOfDispatch, CustomsOffice, PackageType}
import models._
import org.mockito.Mockito.{reset, when}
import org.scalatest.EitherValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import repositories.MongoSuite
import xml.XMLWrites._

import scala.concurrent.ExecutionContext.Implicits.global
import java.time.{LocalDate, LocalDateTime}
import scala.util.Success

class UserAnswersToXmlConversionSpec extends AnyFreeSpec with Matchers with UserAnswersSpecHelper with XMLComparatorSpec
  with XSDSchemaValidationSpec
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
    database.flatMap(_.drop()).futureValue
  }

  "UserAnswers to XML conversion" - {
    "Scenario 1: " in new Setup {
      val firstGoodItem: Index =  Index(0)
      val secondGoodItem: Index =  Index(1)

      val userAnswers: UserAnswers = emptyUserAnswers
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
        .unsafeSetVal(pages.PrincipalAddressPage)(CommonAddress("PrincipalStreet", "PrincipalTown", "AA1 1AA", Country(CountryCode("FR"), "France") ))
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
        .unsafeSetVal(pages.ItemTotalGrossMassPage(firstGoodItem))(25000.123)
        .unsafeSetVal(pages.AddTotalNetMassPage(firstGoodItem))(true)
        .unsafeSetVal(pages.TotalNetMassPage(firstGoodItem))("12342")
        .unsafeSetVal(pages.IsCommodityCodeKnownPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.CommodityCodePage(firstGoodItem))("ComoCode1")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorEoriKnownPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorEoriNumberPage(firstGoodItem))("Conor123")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorNamePage(firstGoodItem))("ConorName")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorAddressPage(firstGoodItem))(CommonAddress("ConorLine1", "ConorLine2", "ConorL3", Country(CountryCode("GA"), "SomethingCO")))
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeEoriKnownPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeEoriNumberPage(firstGoodItem))("Conee123")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeNamePage(firstGoodItem))("ConeeName")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeAddressPage(firstGoodItem))(CommonAddress("ConeeLine1", "ConeeLine2", "ConeeL3", Country(CountryCode("GA"), "SomethingCE")))
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
        .unsafeSetVal(pages.ItemTotalGrossMassPage(secondGoodItem))(25000.123)
        .unsafeSetVal(pages.AddTotalNetMassPage(secondGoodItem))(false)
        .unsafeSetVal(pages.IsCommodityCodeKnownPage(secondGoodItem))(false)
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorEoriKnownPage(secondGoodItem))(false)
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorNamePage(secondGoodItem))("ConorName")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorAddressPage(secondGoodItem))(CommonAddress("ConorLine1", "ConorLine2", "ConorL3", Country(CountryCode("GB"), "SomethingCO")))
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeEoriKnownPage(secondGoodItem))(false)
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeNamePage(secondGoodItem))("ConeeName")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeAddressPage(secondGoodItem))(CommonAddress("ConeeLine1", "ConeeLine2", "ConeeL3", Country(CountryCode("GB"), "SomethingCE")))
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
        .unsafeSetVal(pages.addItems.traderSecurityDetails.SecurityConsignorAddressPage(secondGoodItem))(CommonAddress("GD2CONORL1", "GD2CONORL2", "GD2CONL1", Country(CountryCode("GB"), "GD2CONNOR")))
        .unsafeSetVal(pages.addItems.traderSecurityDetails.AddSecurityConsigneesEoriPage(secondGoodItem))(false)
        .unsafeSetVal(pages.addItems.traderSecurityDetails.SecurityConsigneeNamePage(secondGoodItem))("GD2SECCONEEName")
        .unsafeSetVal(pages.addItems.traderSecurityDetails.SecurityConsigneeAddressPage(secondGoodItem))(CommonAddress("GD2CONEEL1", "GD2CONEEL2", "GD2CEEL1", Country(CountryCode("GB"), "GD2CONNEE")))
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

      val expectedXml = <CC015B>
        <SynIdeMES1>UNOC</SynIdeMES1>
        <SynVerNumMES2>3</SynVerNumMES2>
        <MesRecMES6>NCTS</MesRecMES6>
        <DatOfPreMES9>20201212</DatOfPreMES9>
        <TimOfPreMES10>2030</TimOfPreMES10>
        <IntConRefMES11>DF202012121</IntConRefMES11>
        <AppRefMES14>NCTS</AppRefMES14>
        <TesIndMES18>0</TesIndMES18>
        <MesIdeMES19>1</MesIdeMES19>
        <MesTypMES20>GB015B</MesTypMES20>
        <HEAHEA>
          <RefNumHEA4>TestRefNumber</RefNumHEA4>
          <TypOfDecHEA24>T2</TypOfDecHEA24>
          <CouOfDesCodHEA30>DC</CouOfDesCodHEA30>
          <PlaOfLoaCodHEA46>LoadPLace</PlaOfLoaCodHEA46>
          <CouOfDisCodHEA55>SC</CouOfDisCodHEA55>
          <CusSubPlaHEA66>CUSAPPLOC</CusSubPlaHEA66>
          <InlTraModHEA75>4</InlTraModHEA75>
          <TraModAtBorHEA76>4</TraModAtBorHEA76>
          <NatOfMeaOfTraAtDHEA80>ND</NatOfMeaOfTraAtDHEA80>
          <NatOfMeaOfTraCroHEA87>ND</NatOfMeaOfTraCroHEA87>
          <TypOfMeaOfTraCroHEA88>4</TypOfMeaOfTraCroHEA88>
          <ConIndHEA96>1</ConIndHEA96>
          <DiaLanIndAtDepHEA254>EN</DiaLanIndAtDepHEA254>
          <NCTSAccDocHEA601LNG>EN</NCTSAccDocHEA601LNG>
          <TotNumOfIteHEA305>2</TotNumOfIteHEA305>
          <TotNumOfPacHEA306>1</TotNumOfPacHEA306>
          <TotGroMasHEA307>50000.246</TotGroMasHEA307>
          <DecDatHEA383>20201212</DecDatHEA383>
          <DecPlaHEA394>XX1 1XX</DecPlaHEA394>
          <SpeCirIndHEA1>A</SpeCirIndHEA1>
          <SecHEA358>1</SecHEA358>
          <ConRefNumHEA>SomeConv</ConRefNumHEA>
          <CodPlUnHEA357>PlaceOfUnloadingPage</CodPlUnHEA357>
        </HEAHEA>
        <TRAPRIPC1>
          <NamPC17>PrincipalName</NamPC17>
          <StrAndNumPC122>PrincipalStreet</StrAndNumPC122>
          <PosCodPC123>AA1 1AA</PosCodPC123>
          <CitPC124>PrincipalTown</CitPC124>
          <CouPC125>GB</CouPC125>
        </TRAPRIPC1>
        <CUSOFFDEPEPT>
          <RefNumEPT1>OOD1234A</RefNumEPT1>
        </CUSOFFDEPEPT>
        <CUSOFFTRARNS>
          <RefNumRNS1>TOP12341</RefNumRNS1>
          <ArrTimTRACUS085>202005050512</ArrTimTRACUS085>
        </CUSOFFTRARNS>
        <CUSOFFTRARNS>
          <RefNumRNS1>TOP12342</RefNumRNS1>
          <ArrTimTRACUS085>202005072112</ArrTimTRACUS085>
        </CUSOFFTRARNS>
        <CUSOFFDESEST>
          <RefNumEST1>DOP1234A</RefNumEST1>
        </CUSOFFDESEST>
        <REPREP>
          <NamREP5>John Doe</NamREP5>
          <RepCapREP18>direct</RepCapREP18>
        </REPREP>
        <SEAINFSLI>
          <SeaNumSLI2>2</SeaNumSLI2>
          <SEAIDSID>
            <SeaIdeSID1>SEAL1</SeaIdeSID1>
          </SEAIDSID>
          <SEAIDSID>
            <SeaIdeSID1>SEAL2</SeaIdeSID1>
          </SEAIDSID>
        </SEAINFSLI>
        <GUAGUA>
          <GuaTypGUA1>1</GuaTypGUA1>
          <GUAREFREF>
            <GuaRefNumGRNREF1>GUA1Ref</GuaRefNumGRNREF1>
            <AccCodREF6>1234</AccCodREF6>
          </GUAREFREF>
        </GUAGUA>
        <GUAGUA>
          <GuaTypGUA1>0</GuaTypGUA1>
          <GUAREFREF>
            <GuaRefNumGRNREF1>GUA2Ref</GuaRefNumGRNREF1>
            <AccCodREF6>4321</AccCodREF6>
          </GUAREFREF>
        </GUAGUA>
        <GOOITEGDS>
          <IteNumGDS7>1</IteNumGDS7>
          <ComCodTarCodGDS10>ComoCode1</ComCodTarCodGDS10>
          <GooDesGDS23>ItemOnesDescription</GooDesGDS23>
          <GroMasGDS46>25000.123</GroMasGDS46>
          <NetMasGDS48>12342</NetMasGDS48>
          <MetOfPayGDI12>T</MetOfPayGDI12>
          <ComRefNumGIM1>GD1CRN</ComRefNumGIM1>
          <UNDanGooCodGDI1>GD1C</UNDanGooCodGDI1>
          <PREADMREFAR2>
            <PreDocTypAR21>GD1PR1</PreDocTypAR21>
            <PreDocRefAR26>GD1PR1Ref</PreDocRefAR26>
            <ComOfInfAR29>GD1PR1Info</ComOfInfAR29>
          </PREADMREFAR2>
          <PREADMREFAR2>
            <PreDocTypAR21>GD1PR2</PreDocTypAR21>
            <PreDocRefAR26>GD1PR2Ref</PreDocRefAR26>
          </PREADMREFAR2>
          <PRODOCDC2>
            <DocTypDC21>G1D1</DocTypDC21>
            <DocRefDC23>G1D1Ref</DocRefDC23>
            <ComOfInfDC25>G1D1Info</ComOfInfDC25>
          </PRODOCDC2>
          <PRODOCDC2>
            <DocTypDC21>G1D2</DocTypDC21>
            <DocRefDC23>G1D2Ref</DocRefDC23>
          </PRODOCDC2>
          <SPEMENMT2>
            <AddInfMT21>10000EURGUA1Ref</AddInfMT21>
            <AddInfCodMT23>CAL</AddInfCodMT23>
          </SPEMENMT2>
          <SPEMENMT2>
            <AddInfMT21>500GBPGUA2Ref</AddInfMT21>
            <AddInfCodMT23>CAL</AddInfCodMT23>
          </SPEMENMT2>
          <SPEMENMT2>
            <AddInfMT21>GD1SPMT1Info</AddInfMT21>
            <AddInfCodMT23>GD1S1</AddInfCodMT23>
          </SPEMENMT2>
          <SPEMENMT2>
            <AddInfMT21>GD1S2Info</AddInfMT21>
            <AddInfCodMT23>DG0</AddInfCodMT23>
            <ExpFroCouMT25>GB</ExpFroCouMT25>
          </SPEMENMT2>
          <TRACONCO2>
            <NamCO27>ConorName</NamCO27>
            <StrAndNumCO222>ConorLine1</StrAndNumCO222>
            <PosCodCO223>ConorL3</PosCodCO223>
            <CitCO224>ConorLine2</CitCO224>
            <CouCO225>GA</CouCO225>
            <TINCO259>Conor123</TINCO259>
          </TRACONCO2>
          <TRACONCE2>
            <NamCE27>ConeeName</NamCE27>
            <StrAndNumCE222>ConeeLine1</StrAndNumCE222>
            <PosCodCE223>ConeeL3</PosCodCE223>
            <CitCE224>ConeeLine2</CitCE224>
            <CouCE225>GA</CouCE225>
            <TINCE259>Conee123</TINCE259>
          </TRACONCE2>
          <CONNR2>
            <ConNumNR21>GD1CN1NUM1</ConNumNR21>
          </CONNR2>
          <CONNR2>
            <ConNumNR21>GD1CN2NUMS</ConNumNR21>
          </CONNR2>
          <PACGS2>
            <KinOfPacGS23>VQ</KinOfPacGS23>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD1PK2MK</MarNumOfPacGS21>
            <KinOfPacGS23>NE</KinOfPacGS23>
            <NumOfPieGS25>12</NumOfPieGS25>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD1PK3MK</MarNumOfPacGS21>
            <KinOfPacGS23>BAG</KinOfPacGS23>
            <NumOfPacGS24>2</NumOfPacGS24>
          </PACGS2>
          <TRACORSECGOO021>
            <TINTRACORSECGOO028>GD1SECCONOR</TINTRACORSECGOO028>
          </TRACORSECGOO021>
          <TRACONSECGOO013>
            <TINTRACONSECGOO020>GD1SECCONEE</TINTRACONSECGOO020>
          </TRACONSECGOO013>
        </GOOITEGDS>
        <GOOITEGDS>
          <IteNumGDS7>2</IteNumGDS7>
          <GooDesGDS23>ItemTwosDescription</GooDesGDS23>
          <GroMasGDS46>25000.123</GroMasGDS46>
          <MetOfPayGDI12>U</MetOfPayGDI12>
          <ComRefNumGIM1>GD2CRN</ComRefNumGIM1>
          <PREADMREFAR2>
            <PreDocTypAR21>GD2PR1</PreDocTypAR21>
            <PreDocRefAR26>GD2PR1Ref</PreDocRefAR26>
            <ComOfInfAR29>GD2PR1Info</ComOfInfAR29>
          </PREADMREFAR2>
          <PRODOCDC2>
            <DocTypDC21>G2D1</DocTypDC21>
            <DocRefDC23>G2D1Ref</DocRefDC23>
            <ComOfInfDC25>G2D1Info</ComOfInfDC25>
          </PRODOCDC2>
          <SPEMENMT2>
            <AddInfMT21>GD2S1Info</AddInfMT21>
            <AddInfCodMT23>GD2S1</AddInfCodMT23>
          </SPEMENMT2>
          <TRACONCO2>
            <NamCO27>ConorName</NamCO27>
            <StrAndNumCO222>ConorLine1</StrAndNumCO222>
            <PosCodCO223>ConorL3</PosCodCO223>
            <CitCO224>ConorLine2</CitCO224>
            <CouCO225>GB</CouCO225>
          </TRACONCO2>
          <TRACONCE2>
            <NamCE27>ConeeName</NamCE27>
            <StrAndNumCE222>ConeeLine1</StrAndNumCE222>
            <PosCodCE223>ConeeL3</PosCodCE223>
            <CitCE224>ConeeLine2</CitCE224>
            <CouCE225>GB</CouCE225>
          </TRACONCE2>
          <CONNR2>
            <ConNumNR21>GD2CN1NUM1</ConNumNR21>
          </CONNR2>
          <PACGS2>
            <KinOfPacGS23>VQ</KinOfPacGS23>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD2PK2MK</MarNumOfPacGS21>
            <KinOfPacGS23>NE</KinOfPacGS23>
            <NumOfPieGS25>12</NumOfPieGS25>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD2PK3MK</MarNumOfPacGS21>
            <KinOfPacGS23>BAG</KinOfPacGS23>
            <NumOfPacGS24>2</NumOfPacGS24>
          </PACGS2>
          <TRACORSECGOO021>
            <NamTRACORSECGOO025>GD2SECCONORName</NamTRACORSECGOO025>
            <StrNumTRACORSECGOO027>GD2CONORL1</StrNumTRACORSECGOO027>
            <PosCodTRACORSECGOO026>GD2CONL1</PosCodTRACORSECGOO026>
            <CitTRACORSECGOO022>GD2CONORL2</CitTRACORSECGOO022>
            <CouCodTRACORSECGOO023>GB</CouCodTRACORSECGOO023>
          </TRACORSECGOO021>
          <TRACONSECGOO013>
            <NamTRACONSECGOO017>GD2SECCONEEName</NamTRACONSECGOO017>
            <StrNumTRACONSECGOO019>GD2CONEEL1</StrNumTRACONSECGOO019>
            <PosCodTRACONSECGOO018>GD2CEEL1</PosCodTRACONSECGOO018>
            <CityTRACONSECGOO014>GD2CONEEL2</CityTRACONSECGOO014>
            <CouCodTRACONSECGOO015>GB</CouCodTRACONSECGOO015>
          </TRACONSECGOO013>
        </GOOITEGDS>
        <ITI>
          <CouOfRouCodITI1>CA</CouOfRouCodITI1>
        </ITI>
        <ITI>
          <CouOfRouCodITI1>CB</CouOfRouCodITI1>
        </ITI>
        <CARTRA100>
          <TINCARTRA254>CarrierEori</TINCARTRA254>
        </CARTRA100>
      </CC015B>

      val generatedXml = service.convert(userAnswers)
        .futureValue
        .right.value.toXml.map(scala.xml.Utility.trim)

      validate(generatedXml.toString()) mustBe Success(())

      generatedXml xmlMustEqual expectedXml.map(scala.xml.Utility.trim)
    }
    "Scenario 2: " in new Setup {
      val firstGoodItem: Index =  Index(0)

      val userAnswers: UserAnswers = emptyUserAnswers
        .unsafeSetVal(pages.ProcedureTypePage)(ProcedureType.Simplified)
        .unsafeSetVal(pages.AddSecurityDetailsPage)(false)
        /*
        * General Information Section
        * */
        .unsafeSetVal(pages.DeclarationTypePage)(DeclarationType.Option2)
        .unsafeSetVal(pages.ContainersUsedPage)(false)
        .unsafeSetVal(pages.DeclarationPlacePage)("XX1 1XX")
        .unsafeSetVal(pages.DeclarationForSomeoneElsePage)(false)
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
        .unsafeSetVal(pages.OfficeOfTransitCountryPage(Index(1)))(CountryCode("OT2"))
        .unsafeSetVal(pages.AddAnotherTransitOfficePage(Index(1)))("TOP12342")
        .unsafeSetVal(pages.ArrivalTimesAtOfficePage(Index(1)))(LocalDateTime.of(2020, 5, 7, 21, 12))
        .unsafeSetVal(pages.AddTransitOfficePage)(false)
        /*
        * Transport Details
        * */
        .unsafeSetVal(pages.InlandModePage)("3")
        .unsafeSetVal(pages.IdAtDeparturePage)("SomeIdAtDeparture")
        .unsafeSetVal(pages.NationalityAtDeparturePage)(CountryCode("ND"))
        .unsafeSetVal(pages.ChangeAtBorderPage)(true)
        .unsafeSetVal(pages.ModeAtBorderPage)("3")
        .unsafeSetVal(pages.ModeCrossingBorderPage)("8")
        .unsafeSetVal(pages.IdCrossingBorderPage)("IDCBP")
        .unsafeSetVal(pages.NationalityCrossingBorderPage)(CountryCode("NC"))
        /*
        * Traders Details
        * */
        .unsafeSetVal(pages.WhatIsPrincipalEoriPage)("PRINCEORI")
        .unsafeSetVal(pages.AddConsignorPage)(true)
        .unsafeSetVal(pages.IsConsignorEoriKnownPage)(false)
        .unsafeSetVal(pages.ConsignorNamePage)("ConsignorName")
        .unsafeSetVal(pages.ConsignorAddressPage)(CommonAddress("ConorLine1", "ConorLine2", "ConorL3", Country(CountryCode("CN"), "SomethingCO")))
        .unsafeSetVal(pages.AddConsigneePage)(true)
        .unsafeSetVal(pages.IsConsigneeEoriKnownPage)(false)
        .unsafeSetVal(pages.ConsigneeNamePage)("ConsigneeName")
        .unsafeSetVal(pages.ConsigneeAddressPage)(CommonAddress("ConeeLine1", "ConeeLine2", "ConeeL3", Country(CountryCode("CN"), "SomethingCE")))
        /*
        * Item Details section - Item One
        * */
        .unsafeSetVal(pages.ItemDescriptionPage(firstGoodItem))("ItemOnesDescription")
        .unsafeSetVal(pages.ItemTotalGrossMassPage(firstGoodItem))(25000.123)
        .unsafeSetVal(pages.AddTotalNetMassPage(firstGoodItem))(false)
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
        .unsafeSetVal(pages.addItems.specialMentions.AddSpecialMentionPage(firstGoodItem))(false)
        .unsafeSetVal(pages.addItems.AddDocumentsPage(firstGoodItem))(false)
        .unsafeSetVal(pages.addItems.AddAdministrativeReferencePage(firstGoodItem))(false)
        /*
        * Goods Summary
        */
        .unsafeSetVal(pages.TotalPackagesPage)(1)
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
        <MesRecMES6>NCTS</MesRecMES6>
        <DatOfPreMES9>20201212</DatOfPreMES9>
        <TimOfPreMES10>2030</TimOfPreMES10>
        <IntConRefMES11>DF202012121</IntConRefMES11>
        <AppRefMES14>NCTS</AppRefMES14>
        <TesIndMES18>0</TesIndMES18>
        <MesIdeMES19>1</MesIdeMES19>
        <MesTypMES20>GB015B</MesTypMES20>
        <HEAHEA>
          <RefNumHEA4>TestRefNumber</RefNumHEA4>
          <TypOfDecHEA24>T2</TypOfDecHEA24>
          <CouOfDesCodHEA30>DC</CouOfDesCodHEA30>
          <AutLocOfGooCodHEA41>AuthLocationCode</AutLocOfGooCodHEA41>
          <CouOfDisCodHEA55>SC</CouOfDisCodHEA55>
          <InlTraModHEA75>3</InlTraModHEA75>
          <TraModAtBorHEA76>3</TraModAtBorHEA76>
          <IdeOfMeaOfTraAtDHEA78>SomeIdAtDeparture</IdeOfMeaOfTraAtDHEA78>
          <NatOfMeaOfTraAtDHEA80>ND</NatOfMeaOfTraAtDHEA80>
          <IdeOfMeaOfTraCroHEA85>IDCBP</IdeOfMeaOfTraCroHEA85>
          <NatOfMeaOfTraCroHEA87>NC</NatOfMeaOfTraCroHEA87>
          <TypOfMeaOfTraCroHEA88>8</TypOfMeaOfTraCroHEA88>
          <ConIndHEA96>0</ConIndHEA96>
          <DiaLanIndAtDepHEA254>EN</DiaLanIndAtDepHEA254>
          <NCTSAccDocHEA601LNG>EN</NCTSAccDocHEA601LNG>
          <TotNumOfIteHEA305>1</TotNumOfIteHEA305>
          <TotNumOfPacHEA306>1</TotNumOfPacHEA306>
          <TotGroMasHEA307>25000.123</TotGroMasHEA307>
          <DecDatHEA383>20201212</DecDatHEA383>
          <DecPlaHEA394>XX1 1XX</DecPlaHEA394>
        </HEAHEA>
        <TRAPRIPC1>
          <TINPC159>PRINCEORI</TINPC159>
        </TRAPRIPC1>
        <TRACONCO1>
          <NamCO17>ConsignorName</NamCO17>
          <StrAndNumCO122>ConorLine1</StrAndNumCO122>
          <PosCodCO123>ConorL3</PosCodCO123>
          <CitCO124>ConorLine2</CitCO124>
          <CouCO125>CN</CouCO125>
        </TRACONCO1>
        <TRACONCE1>
          <NamCE17>ConsigneeName</NamCE17>
          <StrAndNumCE122>ConeeLine1</StrAndNumCE122>
          <PosCodCE123>ConeeL3</PosCodCE123>
          <CitCE124>ConeeLine2</CitCE124>
          <CouCE125>CN</CouCE125>
        </TRACONCE1>
        <CUSOFFDEPEPT>
          <RefNumEPT1>OOD1234A</RefNumEPT1>
        </CUSOFFDEPEPT>
        <CUSOFFTRARNS>
          <RefNumRNS1>TOP12341</RefNumRNS1>
        </CUSOFFTRARNS>
        <CUSOFFTRARNS>
          <RefNumRNS1>TOP12342</RefNumRNS1>
        </CUSOFFTRARNS>
        <CUSOFFDESEST>
          <RefNumEST1>DOP1234A</RefNumEST1>
        </CUSOFFDESEST>
        <CONRESERS>
          <ConResCodERS16>A3</ConResCodERS16>
          <DatLimERS69>20201212</DatLimERS69>
        </CONRESERS>
        <GUAGUA>
          <GuaTypGUA1>3</GuaTypGUA1>
          <GUAREFREF>
            <OthGuaRefREF4>GUA1Reference</OthGuaRefREF4>
          </GUAREFREF>
        </GUAGUA>
        <GOOITEGDS>
          <IteNumGDS7>1</IteNumGDS7>
          <GooDesGDS23>ItemOnesDescription</GooDesGDS23>
          <GroMasGDS46>25000.123</GroMasGDS46>
          <PACGS2>
            <KinOfPacGS23>VQ</KinOfPacGS23>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD1PK2MK</MarNumOfPacGS21>
            <KinOfPacGS23>NE</KinOfPacGS23>
            <NumOfPieGS25>12</NumOfPieGS25>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD1PK3MK</MarNumOfPacGS21>
            <KinOfPacGS23>BAG</KinOfPacGS23>
            <NumOfPacGS24>2</NumOfPacGS24>
          </PACGS2>
        </GOOITEGDS>
      </CC015B>

      val generatedXml = service.convert(userAnswers)
        .futureValue
        .right.value.toXml.map(scala.xml.Utility.trim)

      validate(generatedXml.toString()) mustBe Success(())

      generatedXml xmlMustEqual expectedXml.map(scala.xml.Utility.trim)
    }
    "Scenario 3: " in new Setup {
      val firstGoodItem: Index =  Index(0)

      val userAnswers: UserAnswers = emptyUserAnswers
        .unsafeSetVal(pages.ProcedureTypePage)(ProcedureType.Normal)
        .unsafeSetVal(pages.AddSecurityDetailsPage)(true)
        /*
        * General Information Section
        * */
        .unsafeSetVal(pages.DeclarationTypePage)(DeclarationType.Option2)
        .unsafeSetVal(pages.movementDetails.PreLodgeDeclarationPage)(true)
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
        .unsafeSetVal(pages.InlandModePage)("5")
        .unsafeSetVal(pages.AddIdAtDeparturePage)(false)
        .unsafeSetVal(pages.AddNationalityAtDeparturePage)(true)
        .unsafeSetVal(pages.NationalityAtDeparturePage)(CountryCode("ND"))
        .unsafeSetVal(pages.ChangeAtBorderPage)(false)
        /*
        * Traders Details
        * */
        .unsafeSetVal(pages.IsPrincipalEoriKnownPage)(false)
        .unsafeSetVal(pages.PrincipalNamePage)("PrincipalName")
        .unsafeSetVal(pages.PrincipalAddressPage)(CommonAddress("PrincipalStreet", "PrincipalTown", "AA1 1AA", Country(CountryCode("FR"), "France") ))
        .unsafeSetVal(pages.AddConsignorPage)(true)
        .unsafeSetVal(pages.IsConsignorEoriKnownPage)(true)
        .unsafeSetVal(pages.ConsignorEoriPage)("ConorEori")
        .unsafeSetVal(pages.ConsignorNamePage)("ConsignorName")
        .unsafeSetVal(pages.ConsignorAddressPage)(CommonAddress("ConorLine1", "ConorLine2", "ConorL3", Country(CountryCode("CN"), "SomethingCO")))
        .unsafeSetVal(pages.AddConsigneePage)(true)
        .unsafeSetVal(pages.IsConsigneeEoriKnownPage)(true)
        .unsafeSetVal(pages.WhatIsConsigneeEoriPage)("ConeeEori")
        .unsafeSetVal(pages.ConsigneeNamePage)("ConsigneeName")
        .unsafeSetVal(pages.ConsigneeAddressPage)(CommonAddress("ConeeLine1", "ConeeLine2", "ConeeL3", Country(CountryCode("CN"), "SomethingCE")))
        /*
        * Safety & Security Details
        * */
        .unsafeSetVal(pages.safetyAndSecurity.AddCircumstanceIndicatorPage)(false)
        .unsafeSetVal(pages.safetyAndSecurity.AddTransportChargesPaymentMethodPage)(false)
        .unsafeSetVal(pages.safetyAndSecurity.AddCommercialReferenceNumberPage)(false)
        .unsafeSetVal(pages.safetyAndSecurity.AddConveyanceReferenceNumberPage)(false)
        .unsafeSetVal(pages.safetyAndSecurity.PlaceOfUnloadingCodePage)("PlaceOfUnloadingPage")
        .unsafeSetVal(pages.safetyAndSecurity.CountryOfRoutingPage(Index(0)))(CountryCode("CA"))
        .unsafeSetVal(pages.safetyAndSecurity.AddAnotherCountryOfRoutingPage)(false)
        .unsafeSetVal(pages.safetyAndSecurity.AddSafetyAndSecurityConsignorPage)(true)
        .unsafeSetVal(pages.safetyAndSecurity.AddSafetyAndSecurityConsignorEoriPage)(false)
        .unsafeSetVal(pages.safetyAndSecurity.SafetyAndSecurityConsignorNamePage)("SafeSecName")
        .unsafeSetVal(pages.safetyAndSecurity.SafetyAndSecurityConsignorAddressPage)(CommonAddress("SecConorLine1", "SecConorLine2", "SecCorL3", Country(CountryCode("CN"), "SomethingSecCO")))
        .unsafeSetVal(pages.safetyAndSecurity.AddSafetyAndSecurityConsigneePage)(true)
        .unsafeSetVal(pages.safetyAndSecurity.AddSafetyAndSecurityConsigneeEoriPage)(false)
        .unsafeSetVal(pages.safetyAndSecurity.SafetyAndSecurityConsigneeNamePage)("SafeSecName")
        .unsafeSetVal(pages.safetyAndSecurity.SafetyAndSecurityConsigneeAddressPage)(CommonAddress("SecConeeLine1", "SecConeeLine2", "SecCeeL3", Country(CountryCode("CN"), "SomethingSecCE")))
        .unsafeSetVal(pages.safetyAndSecurity.AddCarrierPage)(false)
        /*
        * Item Details section - Item One
        * */
        .unsafeSetVal(pages.ItemDescriptionPage(firstGoodItem))("ItemOnesDescription")
        .unsafeSetVal(pages.ItemTotalGrossMassPage(firstGoodItem))(25000.123)
        .unsafeSetVal(pages.AddTotalNetMassPage(firstGoodItem))(true)
        .unsafeSetVal(pages.TotalNetMassPage(firstGoodItem))("12342")
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
        .unsafeSetVal(pages.addItems.securityDetails.TransportChargesPage(firstGoodItem))("M")
        .unsafeSetVal(pages.addItems.securityDetails.CommercialReferenceNumberPage(firstGoodItem))("GD1CRN")
        .unsafeSetVal(pages.addItems.securityDetails.AddDangerousGoodsCodePage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.securityDetails.DangerousGoodsCodePage(firstGoodItem))("GD1C")
        .unsafeSetVal(pages.addItems.traderSecurityDetails.AddSecurityConsignorsEoriPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.traderSecurityDetails.SecurityConsignorEoriPage(firstGoodItem))("GD1SECCONOR")
        .unsafeSetVal(pages.addItems.traderSecurityDetails.AddSecurityConsigneesEoriPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.traderSecurityDetails.SecurityConsigneeEoriPage(firstGoodItem))("GD1SECCONEE")
        .unsafeSetVal(pages.addItems.AddAnotherItemPage)(false)
        /*
        * Goods Summary
        */
        .unsafeSetVal(pages.TotalPackagesPage)(1)
        .unsafeSetVal(pages.LoadingPlacePage)("LoadPLace")
        .unsafeSetVal(pages.AddAgreedLocationOfGoodsPage)(false)
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
        <MesRecMES6>NCTS</MesRecMES6>
        <DatOfPreMES9>20201212</DatOfPreMES9>
        <TimOfPreMES10>2030</TimOfPreMES10>
        <IntConRefMES11>DF202012121</IntConRefMES11>
        <AppRefMES14>NCTS</AppRefMES14>
        <TesIndMES18>0</TesIndMES18>
        <MesIdeMES19>1</MesIdeMES19>
        <MesTypMES20>GB015B</MesTypMES20>
        <HEAHEA>
          <RefNumHEA4>TestRefNumber</RefNumHEA4>
          <TypOfDecHEA24>T2</TypOfDecHEA24>
          <CouOfDesCodHEA30>DC</CouOfDesCodHEA30>
          <AgrLocOfGooCodHEA38>Pre-lodge</AgrLocOfGooCodHEA38>
          <PlaOfLoaCodHEA46>LoadPLace</PlaOfLoaCodHEA46>
          <CouOfDisCodHEA55>SC</CouOfDisCodHEA55>
          <InlTraModHEA75>5</InlTraModHEA75>
          <TraModAtBorHEA76>5</TraModAtBorHEA76>
          <TypOfMeaOfTraCroHEA88>5</TypOfMeaOfTraCroHEA88>
          <ConIndHEA96>1</ConIndHEA96>
          <DiaLanIndAtDepHEA254>EN</DiaLanIndAtDepHEA254>
          <NCTSAccDocHEA601LNG>EN</NCTSAccDocHEA601LNG>
          <TotNumOfIteHEA305>1</TotNumOfIteHEA305>
          <TotNumOfPacHEA306>1</TotNumOfPacHEA306>
          <TotGroMasHEA307>25000.123</TotGroMasHEA307>
          <DecDatHEA383>20201212</DecDatHEA383>
          <DecPlaHEA394>XX1 1XX</DecPlaHEA394>
          <SecHEA358>1</SecHEA358>
          <CodPlUnHEA357>PlaceOfUnloadingPage</CodPlUnHEA357>
        </HEAHEA>
        <TRAPRIPC1>
          <NamPC17>PrincipalName</NamPC17>
          <StrAndNumPC122>PrincipalStreet</StrAndNumPC122>
          <PosCodPC123>AA1 1AA</PosCodPC123>
          <CitPC124>PrincipalTown</CitPC124>
          <CouPC125>GB</CouPC125>
        </TRAPRIPC1>
        <TRACONCO1>
          <NamCO17>ConsignorName</NamCO17>
          <StrAndNumCO122>ConorLine1</StrAndNumCO122>
          <PosCodCO123>ConorL3</PosCodCO123>
          <CitCO124>ConorLine2</CitCO124>
          <CouCO125>CN</CouCO125>
          <TINCO159>ConorEori</TINCO159>
        </TRACONCO1>
        <TRACONCE1>
          <NamCE17>ConsigneeName</NamCE17>
          <StrAndNumCE122>ConeeLine1</StrAndNumCE122>
          <PosCodCE123>ConeeL3</PosCodCE123>
          <CitCE124>ConeeLine2</CitCE124>
          <CouCE125>CN</CouCE125>
          <TINCE159>ConeeEori</TINCE159>
        </TRACONCE1>
        <CUSOFFDEPEPT>
          <RefNumEPT1>OOD1234A</RefNumEPT1>
        </CUSOFFDEPEPT>
        <CUSOFFTRARNS>
          <RefNumRNS1>TOP12341</RefNumRNS1>
          <ArrTimTRACUS085>202005072112</ArrTimTRACUS085>
        </CUSOFFTRARNS>
        <CUSOFFDESEST>
          <RefNumEST1>DOP1234A</RefNumEST1>
        </CUSOFFDESEST>
        <REPREP>
          <NamREP5>John Doe</NamREP5>
          <RepCapREP18>direct</RepCapREP18>
        </REPREP>
        <SEAINFSLI>
          <SeaNumSLI2>1</SeaNumSLI2>
          <SEAIDSID>
            <SeaIdeSID1>SEAL1</SeaIdeSID1>
          </SEAIDSID>
        </SEAINFSLI>
        <GUAGUA>
          <GuaTypGUA1>1</GuaTypGUA1>
          <GUAREFREF>
            <GuaRefNumGRNREF1>GUA1Ref</GuaRefNumGRNREF1>
            <AccCodREF6>1234</AccCodREF6>
          </GUAREFREF>
        </GUAGUA>
        <GUAGUA>
          <GuaTypGUA1>0</GuaTypGUA1>
          <GUAREFREF>
            <GuaRefNumGRNREF1>GUA2Ref</GuaRefNumGRNREF1>
            <AccCodREF6>4321</AccCodREF6>
          </GUAREFREF>
        </GUAGUA>
        <GOOITEGDS>
          <IteNumGDS7>1</IteNumGDS7>
          <ComCodTarCodGDS10>ComoCode1</ComCodTarCodGDS10>
          <GooDesGDS23>ItemOnesDescription</GooDesGDS23>
          <GroMasGDS46>25000.123</GroMasGDS46>
          <NetMasGDS48>12342</NetMasGDS48>
          <MetOfPayGDI12>M</MetOfPayGDI12>
          <ComRefNumGIM1>GD1CRN</ComRefNumGIM1>
          <UNDanGooCodGDI1>GD1C</UNDanGooCodGDI1>
          <PREADMREFAR2>
            <PreDocTypAR21>GD1PR1</PreDocTypAR21>
            <PreDocRefAR26>GD1PR1Ref</PreDocRefAR26>
            <ComOfInfAR29>GD1PR1Info</ComOfInfAR29>
          </PREADMREFAR2>
          <PREADMREFAR2>
            <PreDocTypAR21>GD1PR2</PreDocTypAR21>
            <PreDocRefAR26>GD1PR2Ref</PreDocRefAR26>
          </PREADMREFAR2>
          <PRODOCDC2>
            <DocTypDC21>G1D1</DocTypDC21>
            <DocRefDC23>G1D1Ref</DocRefDC23>
            <ComOfInfDC25>G1D1Info</ComOfInfDC25>
          </PRODOCDC2>
          <PRODOCDC2>
            <DocTypDC21>G1D2</DocTypDC21>
            <DocRefDC23>G1D2Ref</DocRefDC23>
          </PRODOCDC2>
          <SPEMENMT2>
            <AddInfMT21>10000EURGUA1Ref</AddInfMT21>
            <AddInfCodMT23>CAL</AddInfCodMT23>
          </SPEMENMT2>
          <SPEMENMT2>
            <AddInfMT21>500GBPGUA2Ref</AddInfMT21>
            <AddInfCodMT23>CAL</AddInfCodMT23>
          </SPEMENMT2>
          <SPEMENMT2>
            <AddInfMT21>GD1SPMT1Info</AddInfMT21>
            <AddInfCodMT23>GD1S1</AddInfCodMT23>
          </SPEMENMT2>
          <SPEMENMT2>
            <AddInfMT21>GD1S2Info</AddInfMT21>
            <AddInfCodMT23>GD1S2</AddInfCodMT23>
          </SPEMENMT2>
          <CONNR2>
            <ConNumNR21>GD1CN1NUM1</ConNumNR21>
          </CONNR2>
          <CONNR2>
            <ConNumNR21>GD1CN2NUMS</ConNumNR21>
          </CONNR2>
          <PACGS2>
            <KinOfPacGS23>VQ</KinOfPacGS23>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD1PK2MK</MarNumOfPacGS21>
            <KinOfPacGS23>NE</KinOfPacGS23>
            <NumOfPieGS25>12</NumOfPieGS25>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD1PK3MK</MarNumOfPacGS21>
            <KinOfPacGS23>BAG</KinOfPacGS23>
            <NumOfPacGS24>2</NumOfPacGS24>
          </PACGS2>
        </GOOITEGDS>
        <ITI>
          <CouOfRouCodITI1>CA</CouOfRouCodITI1>
        </ITI>
        <TRACORSEC037>
          <NamTRACORSEC041>SafeSecName</NamTRACORSEC041>
          <StrNumTRACORSEC043>SecConorLine1</StrNumTRACORSEC043>
          <PosCodTRACORSEC042>SecCorL3</PosCodTRACORSEC042>
          <CitTRACORSEC038>SecConorLine2</CitTRACORSEC038>
          <CouCodTRACORSEC039>CN</CouCodTRACORSEC039>
        </TRACORSEC037>
        <TRACONSEC029>
          <NameTRACONSEC033>SafeSecName</NameTRACONSEC033>
          <StrNumTRACONSEC035>SecConeeLine1</StrNumTRACONSEC035>
          <PosCodTRACONSEC034>SecCeeL3</PosCodTRACONSEC034>
          <CitTRACONSEC030>SecConeeLine2</CitTRACONSEC030>
          <CouCodTRACONSEC031>CN</CouCodTRACONSEC031>
        </TRACONSEC029>
      </CC015B>

      val generatedXml = service.convert(userAnswers)
        .futureValue
        .right.value.toXml.map(scala.xml.Utility.trim)

      validate(generatedXml.toString()) mustBe Success(())

      generatedXml xmlMustEqual expectedXml.map(scala.xml.Utility.trim)
    }
    "Scenario 4: " in new Setup {
      val firstGoodItem: Index =  Index(0)

      val userAnswers: UserAnswers = emptyUserAnswers
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
        .unsafeSetVal(pages.PrincipalAddressPage)(CommonAddress("PrincipalStreet", "PrincipalTown", "AA1 1AA", Country(CountryCode("FR"), "France") ))
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
        .unsafeSetVal(pages.ItemTotalGrossMassPage(firstGoodItem))(25000.123)
        .unsafeSetVal(pages.AddTotalNetMassPage(firstGoodItem))(true)
        .unsafeSetVal(pages.TotalNetMassPage(firstGoodItem))("12342")
        .unsafeSetVal(pages.IsCommodityCodeKnownPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.CommodityCodePage(firstGoodItem))("ComoCode1")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorEoriKnownPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorEoriNumberPage(firstGoodItem))("Conor123")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorNamePage(firstGoodItem))("ConorName")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsignorAddressPage(firstGoodItem))(CommonAddress("ConorLine1", "ConorLine2", "ConorL3", Country(CountryCode("GA"), "SomethingCO")))
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeEoriKnownPage(firstGoodItem))(true)
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeEoriNumberPage(firstGoodItem))("Conee123")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeNamePage(firstGoodItem))("ConeeName")
        .unsafeSetVal(pages.addItems.traderDetails.TraderDetailsConsigneeAddressPage(firstGoodItem))(CommonAddress("ConeeLine1", "ConeeLine2", "ConeeL3", Country(CountryCode("GA"), "SomethingCE")))
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

      val expectedXml = <CC015B>
        <SynIdeMES1>UNOC</SynIdeMES1>
        <SynVerNumMES2>3</SynVerNumMES2>
        <MesRecMES6>NCTS</MesRecMES6>
        <DatOfPreMES9>20201212</DatOfPreMES9>
        <TimOfPreMES10>2030</TimOfPreMES10>
        <IntConRefMES11>DF202012121</IntConRefMES11>
        <AppRefMES14>NCTS</AppRefMES14>
        <TesIndMES18>0</TesIndMES18>
        <MesIdeMES19>1</MesIdeMES19>
        <MesTypMES20>GB015B</MesTypMES20>
        <HEAHEA>
          <RefNumHEA4>TestRefNumber</RefNumHEA4>
          <TypOfDecHEA24>T2</TypOfDecHEA24>
          <CouOfDesCodHEA30>DC</CouOfDesCodHEA30>
          <PlaOfLoaCodHEA46>LoadPLace</PlaOfLoaCodHEA46>
          <CouOfDisCodHEA55>SC</CouOfDisCodHEA55>
          <CusSubPlaHEA66>CUSAPPLOC</CusSubPlaHEA66>
          <InlTraModHEA75>2</InlTraModHEA75>
          <TraModAtBorHEA76>2</TraModAtBorHEA76>
          <TypOfMeaOfTraCroHEA88>2</TypOfMeaOfTraCroHEA88>
          <ConIndHEA96>1</ConIndHEA96>
          <DiaLanIndAtDepHEA254>EN</DiaLanIndAtDepHEA254>
          <NCTSAccDocHEA601LNG>EN</NCTSAccDocHEA601LNG>
          <TotNumOfIteHEA305>1</TotNumOfIteHEA305>
          <TotNumOfPacHEA306>1</TotNumOfPacHEA306>
          <TotGroMasHEA307>25000.123</TotGroMasHEA307>
          <DecDatHEA383>20201212</DecDatHEA383>
          <DecPlaHEA394>XX1 1XX</DecPlaHEA394>
          <SpeCirIndHEA1>E</SpeCirIndHEA1>
          <ComRefNumHEA>COMREFALL</ComRefNumHEA>
          <SecHEA358>1</SecHEA358>
        </HEAHEA>
        <TRAPRIPC1>
          <NamPC17>PrincipalName</NamPC17>
          <StrAndNumPC122>PrincipalStreet</StrAndNumPC122>
          <PosCodPC123>AA1 1AA</PosCodPC123>
          <CitPC124>PrincipalTown</CitPC124>
          <CouPC125>GB</CouPC125>
        </TRAPRIPC1>
        <CUSOFFDEPEPT>
          <RefNumEPT1>OOD1234A</RefNumEPT1>
        </CUSOFFDEPEPT>
        <CUSOFFTRARNS>
          <RefNumRNS1>TOP12341</RefNumRNS1>
          <ArrTimTRACUS085>202005072112</ArrTimTRACUS085>
        </CUSOFFTRARNS>
        <CUSOFFDESEST>
          <RefNumEST1>DOP1234A</RefNumEST1>
        </CUSOFFDESEST>
        <REPREP>
          <NamREP5>John Doe</NamREP5>
          <RepCapREP18>direct</RepCapREP18>
        </REPREP>
        <SEAINFSLI>
          <SeaNumSLI2>2</SeaNumSLI2>
          <SEAIDSID>
            <SeaIdeSID1>SEAL1</SeaIdeSID1>
          </SEAIDSID>
          <SEAIDSID>
            <SeaIdeSID1>SEAL2</SeaIdeSID1>
          </SEAIDSID>
        </SEAINFSLI>
        <GUAGUA>
          <GuaTypGUA1>1</GuaTypGUA1>
          <GUAREFREF>
            <GuaRefNumGRNREF1>GUA1Ref</GuaRefNumGRNREF1>
            <AccCodREF6>1234</AccCodREF6>
          </GUAREFREF>
        </GUAGUA>
        <GUAGUA>
          <GuaTypGUA1>0</GuaTypGUA1>
          <GUAREFREF>
            <GuaRefNumGRNREF1>GUA2Ref</GuaRefNumGRNREF1>
            <AccCodREF6>4321</AccCodREF6>
          </GUAREFREF>
        </GUAGUA>
        <GOOITEGDS>
          <IteNumGDS7>1</IteNumGDS7>
          <ComCodTarCodGDS10>ComoCode1</ComCodTarCodGDS10>
          <GooDesGDS23>ItemOnesDescription</GooDesGDS23>
          <GroMasGDS46>25000.123</GroMasGDS46>
          <NetMasGDS48>12342</NetMasGDS48>
          <MetOfPayGDI12>W</MetOfPayGDI12>
          <UNDanGooCodGDI1>GD1C</UNDanGooCodGDI1>
          <PREADMREFAR2>
            <PreDocTypAR21>GD1PR1</PreDocTypAR21>
            <PreDocRefAR26>GD1PR1Ref</PreDocRefAR26>
            <ComOfInfAR29>GD1PR1Info</ComOfInfAR29>
          </PREADMREFAR2>
          <PREADMREFAR2>
            <PreDocTypAR21>GD1PR2</PreDocTypAR21>
            <PreDocRefAR26>GD1PR2Ref</PreDocRefAR26>
          </PREADMREFAR2>
          <PRODOCDC2>
            <DocTypDC21>G1D1</DocTypDC21>
            <DocRefDC23>G1D1Ref</DocRefDC23>
            <ComOfInfDC25>G1D1Info</ComOfInfDC25>
          </PRODOCDC2>
          <PRODOCDC2>
            <DocTypDC21>G1D2</DocTypDC21>
            <DocRefDC23>G1D2Ref</DocRefDC23>
          </PRODOCDC2>
          <SPEMENMT2>
            <AddInfMT21>10000EURGUA1Ref</AddInfMT21>
            <AddInfCodMT23>CAL</AddInfCodMT23>
          </SPEMENMT2>
          <SPEMENMT2>
            <AddInfMT21>500GBPGUA2Ref</AddInfMT21>
            <AddInfCodMT23>CAL</AddInfCodMT23>
          </SPEMENMT2>
          <SPEMENMT2>
            <AddInfMT21>GD1SPMT1Info</AddInfMT21>
            <AddInfCodMT23>GD1S1</AddInfCodMT23>
          </SPEMENMT2>
          <SPEMENMT2>
            <AddInfMT21>GD1S2Info</AddInfMT21>
            <AddInfCodMT23>GD1S2</AddInfCodMT23>
          </SPEMENMT2>
          <TRACONCO2>
            <NamCO27>ConorName</NamCO27>
            <StrAndNumCO222>ConorLine1</StrAndNumCO222>
            <PosCodCO223>ConorL3</PosCodCO223>
            <CitCO224>ConorLine2</CitCO224>
            <CouCO225>GA</CouCO225>
            <TINCO259>Conor123</TINCO259>
          </TRACONCO2>
          <TRACONCE2>
            <NamCE27>ConeeName</NamCE27>
            <StrAndNumCE222>ConeeLine1</StrAndNumCE222>
            <PosCodCE223>ConeeL3</PosCodCE223>
            <CitCE224>ConeeLine2</CitCE224>
            <CouCE225>GA</CouCE225>
            <TINCE259>Conee123</TINCE259>
          </TRACONCE2>
          <CONNR2>
            <ConNumNR21>GD1CN1NUM1</ConNumNR21>
          </CONNR2>
          <CONNR2>
            <ConNumNR21>GD1CN2NUMS</ConNumNR21>
          </CONNR2>
          <PACGS2>
            <KinOfPacGS23>VQ</KinOfPacGS23>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD1PK2MK</MarNumOfPacGS21>
            <KinOfPacGS23>NE</KinOfPacGS23>
            <NumOfPieGS25>12</NumOfPieGS25>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD1PK3MK</MarNumOfPacGS21>
            <KinOfPacGS23>BAG</KinOfPacGS23>
            <NumOfPacGS24>2</NumOfPacGS24>
          </PACGS2>
        </GOOITEGDS>
        <ITI>
          <CouOfRouCodITI1>CA</CouOfRouCodITI1>
        </ITI>
        <CARTRA100>
          <NamCARTRA121>CarrierName</NamCARTRA121>
          <StrAndNumCARTRA254>CarAddL1</StrAndNumCARTRA254>
          <PosCodCARTRA121>CarAddL3</PosCodCARTRA121>
          <CitCARTRA789>CarAddL2</CitCARTRA789>
          <CouCodCARTRA587>CA</CouCodCARTRA587>
        </CARTRA100>
        <TRACORSEC037>
          <TINTRACORSEC044>SafeSecConorEori</TINTRACORSEC044>
        </TRACORSEC037>
        <TRACONSEC029>
          <TINTRACONSEC036>SafeSecConeeEori</TINTRACONSEC036>
        </TRACONSEC029>
      </CC015B>

      val generatedXml = service.convert(userAnswers)
        .futureValue
        .right.value.toXml.map(scala.xml.Utility.trim)

      validate(generatedXml.toString()) mustBe Success(())

      generatedXml xmlMustEqual expectedXml.map(scala.xml.Utility.trim)
    }
  }
}
