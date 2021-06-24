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
import models.ProcedureType.Simplified
import models.journeyDomain.GoodsSummary.GoodSummarySimplifiedDetails
import models.journeyDomain.GuaranteeDetails.GuaranteeOther
import models.journeyDomain.MovementDetails.{DeclarationForSelf, SimplifiedMovementDetails}
import models.journeyDomain.Packages.{BulkPackages, OtherPackages, UnpackedPackages}
import models.journeyDomain.RouteDetails.TransitInformation
import models.journeyDomain.TransportDetails.DetailsAtBorder.NewDetailsAtBorder
import models.journeyDomain.TransportDetails.InlandMode.NonSpecialMode
import models.journeyDomain.TransportDetails.ModeCrossingBorder.ModeWithNationality
import models.journeyDomain.traderDetails.{ConsigneeDetails, ConsignorDetails, PrincipalTraderEoriInfo, TraderDetails}
import models.journeyDomain.{GoodsSummary, ItemDetails, ItemSection, JourneyDomain, PreTaskListDetails, RouteDetails, TransportDetails}
import models.reference._
import models.{CommonAddress, DeclarationType, EoriNumber, GuaranteeType, Index, LocalReferenceNumber, ProcedureType, UserAnswers}
import play.api.libs.json.Json

import java.time.{LocalDate, LocalDateTime}

object Scenario2 extends UserAnswerScenario {

  private val firstGoodItem: Index      = Index(0)
  private val eoriNumber: EoriNumber    = EoriNumber("EoriNumber")
  private val lrn: LocalReferenceNumber = LocalReferenceNumber("ABCD1234567890123").get

  val userAnswers: UserAnswers = UserAnswers(lrn, eoriNumber, Json.obj())
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
    .unsafeSetVal(pages.ItemTotalGrossMassPage(firstGoodItem))("25000")
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

  private val preTaskList = PreTaskListDetails(lrn, Simplified, CustomsOffice("OOD1234A", "OfficeOfDeparturePage", CountryCode("CC"), List.empty, None), false)

  private val guarantee = NonEmptyList(GuaranteeOther(GuaranteeType.CashDepositGuarantee, "GUA1Reference"), List.empty)

  private val goodsSummary = GoodsSummary(1, None, GoodSummarySimplifiedDetails("AuthLocationCode", LocalDate.of(2020, 12, 12)), List.empty)

  private val itemDetails = NonEmptyList(
    ItemSection(
      itemDetails = ItemDetails("ItemOnesDescription", "25000", None, None),
      consignor = None,
      consignee = None,
      packages = NonEmptyList(
        BulkPackages(PackageType("VQ", "GD1PKG1"), None),
        List(UnpackedPackages(PackageType("NE", "GD1PKG2"), 12, Some("GD1PK2MK")), OtherPackages(PackageType("BAG", "GD1PKG3"), 2, "GD1PK3MK"))
      ),
      containers = None,
      specialMentions = None,
      producedDocuments = None,
      itemSecurityTraderDetails = None,
      previousReferences = None
    ),
    List.empty
  )

  private val traderDetails = TraderDetails(
    principalTraderDetails = PrincipalTraderEoriInfo(EoriNumber("PRINCEORI")),
    consignor = Some(
      ConsignorDetails("ConsignorName", models.domain.Address("ConorLine1", "ConorLine2", "ConorL3", Some(Country(CountryCode("CN"), "SomethingCO"))), None)
    ),
    consignee = Some(
      ConsigneeDetails("ConsigneeName", models.domain.Address("ConeeLine1", "ConeeLine2", "ConeeL3", Some(Country(CountryCode("CN"), "SomethingCE"))), None)
    )
  )

  private val transportDetails = TransportDetails(
    NonSpecialMode(3, Some(CountryCode("ND")), Some("SomeIdAtDeparture")),
    NewDetailsAtBorder("3", ModeWithNationality(CountryCode("NC"), 8, "IDCBP"))
  )

  private val movementDetails = SimplifiedMovementDetails(
    Option2,
    false,
    "XX1 1XX",
    DeclarationForSelf
  )

  private val routeDetails = RouteDetails(
    CountryOfDispatch(CountryCode("SC"), false),
    CountryCode("DC"),
    CustomsOffice("DOP1234A", "DestinationOfficePage", CountryCode("DO"), List.empty, None),
    NonEmptyList(TransitInformation("TOP12341", None), List(TransitInformation("TOP12342", None)))
  )

  val toModel = JourneyDomain(
    preTaskList,
    movementDetails,
    routeDetails,
    transportDetails,
    traderDetails,
    itemDetails,
    goodsSummary,
    guarantee,
    None,
    None
  )

}
