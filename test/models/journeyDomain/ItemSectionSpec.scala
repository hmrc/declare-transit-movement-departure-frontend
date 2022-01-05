/*
 * Copyright 2022 HM Revenue & Customs
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

package models.journeyDomain

import base.{GeneratorSpec, SpecBase}
import cats.data.NonEmptyList
import commonTestUtils.UserAnswersSpecHelper
import models.DeclarationType.Option1
import models.Index
import models.journeyDomain.Packages.UnpackedPackages
import models.journeyDomain.addItems.ItemsSecurityTraderDetails
import models.reference.{CountryCode, CountryOfDispatch, CustomsOffice, PackageType}
import org.scalacheck.Gen
import pages._
import pages.addItems._
import pages.addItems.containers.ContainerNumberPage
import pages.addItems.securityDetails.AddDangerousGoodsCodePage
import pages.addItems.specialMentions.{AddSpecialMentionPage, SpecialMentionAdditionalInfoPage, SpecialMentionTypePage}
import pages.generalInformation.ContainersUsedPage
import pages.routeDetails.CountryOfDispatchPage
import pages.safetyAndSecurity._
import pages.traderDetails.{AddConsigneePage, AddConsignorPage}

class ItemSectionSpec extends SpecBase with GeneratorSpec with UserAnswersSpecHelper {

  private val itemSectionUa = emptyUserAnswers
    //ItemDetails
    .unsafeSetVal(ItemDescriptionPage(index))("itemDescription")
    .unsafeSetVal(ItemTotalGrossMassPage(index))(123)
    .unsafeSetVal(AddTotalNetMassPage(index))(true)
    .unsafeSetVal(TotalNetMassPage(index))("123")
    .unsafeSetVal(IsCommodityCodeKnownPage(index))(true)
    .unsafeSetVal(CommodityCodePage(index))("commodityCode")
    .unsafeSetVal(IsNonEuOfficePage)(false)
    //Consignor
    .unsafeSetVal(AddConsignorPage)(true)
    //Consignee
    .unsafeSetVal(AddConsigneePage)(true)
    //Packages
    .unsafeSetVal(PackageTypePage(itemIndex, packageIndex))(PackageType("NE", "description"))
    .unsafeSetVal(TotalPiecesPage(itemIndex, packageIndex))(123)
    .unsafeSetVal(AddMarkPage(itemIndex, packageIndex))(false)
    //Containers
    .unsafeSetVal(ContainersUsedPage)(false)
    //SpecialMention
    .unsafeSetVal(AddSpecialMentionPage(itemIndex))(false)
    //ProducedDocuments
    .unsafeSetVal(AddSecurityDetailsPage)(true)
    .unsafeSetVal(AddCommercialReferenceNumberPage)(true)
    .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
    .unsafeSetVal(AddDocumentsPage(index))(false)
    //ItemSecurityTraderDetails
    .unsafeSetVal(AddCommercialReferenceNumberAllItemsPage)(true)
    .unsafeSetVal(AddDangerousGoodsCodePage(index))(false)
    .unsafeSetVal(AddTransportChargesPaymentMethodPage)(true)
    .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(true)
    .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(true)
    //PreviousReferences
    .unsafeSetVal(AddAdministrativeReferencePage(index))(false)
    .unsafeSetVal(DeclarationTypePage)(Option1)
    .unsafeSetVal(CountryOfDispatchPage)(CountryOfDispatch(CountryCode("IT"), isNotEu = false))

  "ItemSection" - {
    "can be parsed UserAnswers" - {
      "when all mandatory answers for section have been defined" in {

        val expectedResult = ItemSection(
          ItemDetails("itemDescription", "123.000", Some("123"), Some("commodityCode")),
          None,
          None,
          NonEmptyList(UnpackedPackages(PackageType("NE", "description"), 123, None), List.empty),
          None,
          None,
          None,
          Some(ItemsSecurityTraderDetails(None, None, None, None, None)),
          None
        )

        val result = ItemSection.readerItemSection(index).run(itemSectionUa)

        result.value mustBe expectedResult
      }

      "when containers used is true and containers are defined" in {

        val userAnswers = itemSectionUa
          .unsafeSetVal(ContainersUsedPage)(true)
          .unsafeSetVal(ContainerNumberPage(index, referenceIndex))("123")
          .unsafeSetVal(ContainerNumberPage(index, Index(1)))("123")

        val expectedResult = NonEmptyList(Container("123"), List(Container("123")))

        val result = ItemSection.readerItemSection(index).run(userAnswers).value

        result.containers.value mustBe expectedResult
      }

      "when add special mention is true and special mentions are defined" in {
        val customsOffice: CustomsOffice = CustomsOffice("id", "name", CountryCode("GB"), None)
        val userAnswers = itemSectionUa
          .unsafeSetVal(AddSpecialMentionPage(index))(true)
          .unsafeSetVal(SpecialMentionTypePage(index, referenceIndex))("specialMentionType")
          .unsafeSetVal(SpecialMentionAdditionalInfoPage(index, referenceIndex))("additionalInfo")
          .unsafeSetVal(OfficeOfDeparturePage)(customsOffice)

        val expectedResult = NonEmptyList(SpecialMentionDomain("specialMentionType", "additionalInfo", customsOffice), List.empty)

        val result = ItemSection.readerItemSection(index).run(userAnswers).value

        result.specialMentions.value mustBe expectedResult
      }
    }

    "cannot be parsed" - {

      val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
        AddSpecialMentionPage(itemIndex),
        ContainersUsedPage
      )

      "when a mandatory answer is missing" in {

        forAll(mandatoryPages) {
          mandatoryPage =>
            val userAnswers = itemSectionUa.unsafeRemove(mandatoryPage)

            val result = ItemSection.readerItemSection(index).run(userAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }

      "when all packages cannot be derived" in {

        val userAnswers = itemSectionUa
          .unsafeSetVal(PackageTypePage(itemIndex, Index(1)))(PackageType("NE", "description"))
          .unsafeSetVal(TotalPiecesPage(itemIndex, Index(1)))(123)
          .unsafeSetVal(AddMarkPage(itemIndex, Index(1)))(false)
          .unsafeRemove(PackageTypePage(itemIndex, packageIndex))

        val result = ItemSection.readerItemSection(index).run(userAnswers).left.value

        result.page mustBe PackageTypePage(itemIndex, packageIndex)

      }

      "when containers used is true and containers are not defined" in {

        val userAnswers = itemSectionUa
          .unsafeSetVal(ContainersUsedPage)(true)
          .unsafeRemove(ContainerNumberPage(index, referenceIndex))
          .unsafeRemove(ContainerNumberPage(index, Index(1)))

        val result = ItemSection.readerItemSection(index).run(userAnswers).left.value

        result.page mustBe ContainerNumberPage(index, referenceIndex)
      }

      "when add special mention is true and special mentions are not defined" in {

        val userAnswers = itemSectionUa
          .unsafeSetVal(AddSpecialMentionPage(index))(true)
          .unsafeRemove(SpecialMentionTypePage(index, referenceIndex))
          .unsafeRemove(SpecialMentionAdditionalInfoPage(index, referenceIndex))

        val result = ItemSection.readerItemSection(index).run(userAnswers).left.value

        result.page mustBe SpecialMentionTypePage(index, referenceIndex)
      }
    }
  }

  "Seq of ItemSection" - {
    "can be parsed UserAnswers" - {
      "when all details for section have been answered" in {

        val userAnswersWithSecondItem = itemSectionUa
          //ItemDetails
          .unsafeSetVal(ItemDescriptionPage(Index(1)))("itemDescription")
          .unsafeSetVal(ItemTotalGrossMassPage(Index(1)))(123.000)
          .unsafeSetVal(AddTotalNetMassPage(Index(1)))(true)
          .unsafeSetVal(TotalNetMassPage(Index(1)))("123")
          .unsafeSetVal(IsCommodityCodeKnownPage(Index(1)))(true)
          .unsafeSetVal(CommodityCodePage(Index(1)))("commodityCode")
          .unsafeSetVal(IsNonEuOfficePage)(false)
          //Consignor
          .unsafeSetVal(AddConsignorPage)(true)
          //Consignee
          .unsafeSetVal(AddConsigneePage)(true)
          //Packages
          .unsafeSetVal(PackageTypePage(Index(1), packageIndex))(PackageType("NE", "description"))
          .unsafeSetVal(TotalPiecesPage(Index(1), packageIndex))(123)
          .unsafeSetVal(AddMarkPage(Index(1), packageIndex))(false)
          //Containers
          .unsafeSetVal(ContainersUsedPage)(false)
          //SpecialMention
          .unsafeSetVal(AddSpecialMentionPage(Index(1)))(false)
          //ProducedDocuments
          .unsafeSetVal(AddSecurityDetailsPage)(true)
          .unsafeSetVal(AddCommercialReferenceNumberPage)(true)
          .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
          .unsafeSetVal(AddDocumentsPage(Index(1)))(false)
          //ItemSecurityTraderDetails
          .unsafeSetVal(AddCommercialReferenceNumberAllItemsPage)(true)
          .unsafeSetVal(AddDangerousGoodsCodePage(Index(1)))(false)
          .unsafeSetVal(AddTransportChargesPaymentMethodPage)(true)
          .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(true)
          .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(true)
          //PreviousReferences
          .unsafeSetVal(AddAdministrativeReferencePage(Index(1)))(false)
          .unsafeSetVal(DeclarationTypePage)(Option1)
          .unsafeSetVal(CountryOfDispatchPage)(CountryOfDispatch(CountryCode("IT"), isNotEu = false))

        val expectedResult = NonEmptyList(
          ItemSection(
            ItemDetails("itemDescription", "123.000", Some("123"), Some("commodityCode")),
            None,
            None,
            NonEmptyList(UnpackedPackages(PackageType("NE", "description"), 123, None), List.empty),
            None,
            None,
            None,
            Some(ItemsSecurityTraderDetails(None, None, None, None, None)),
            None
          ),
          List(
            ItemSection(
              ItemDetails("itemDescription", "123.000", Some("123"), Some("commodityCode")),
              None,
              None,
              NonEmptyList(UnpackedPackages(PackageType("NE", "description"), 123, None), List.empty),
              None,
              None,
              None,
              Some(ItemsSecurityTraderDetails(None, None, None, None, None)),
              None
            )
          )
        )

        val result = ItemSection.readerItemSections.run(userAnswersWithSecondItem)

        result.value mustEqual expectedResult
      }
    }
  }
}
