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

package models.journeyDomain

import base.{GeneratorSpec, SpecBase}
import cats.data.NonEmptyList
import commonTestUtils.UserAnswersSpecHelper
import generators.JourneyModelGenerators
import models.DeclarationType.Option1
import models.journeyDomain.Packages.UnpackedPackages
import models.journeyDomain.PackagesSpec.UserAnswersSpecHelperOps
import models.journeyDomain.addItems.{ItemsSecurityTraderDetails, ItemsSecurityTraderDetailsSpec}
import models.reference.{CountryCode, CountryOfDispatch, PackageType}
import models.{Index, UserAnswers}
import org.scalacheck.Gen
import pages._
import pages.addItems.containers.ContainerNumberPage
import pages.addItems.securityDetails.AddDangerousGoodsCodePage
import pages.addItems.specialMentions.{AddSpecialMentionPage, SpecialMentionAdditionalInfoPage, SpecialMentionTypePage}
import pages.addItems._
import pages.safetyAndSecurity._

class ItemSectionSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {

  private val itemSectionUa = emptyUserAnswers
    //ItemDetails
    .unsafeSetVal(ItemDescriptionPage(index))("itemDescription")
    .unsafeSetVal(ItemTotalGrossMassPage(index))(123)
    .unsafeSetVal(AddTotalNetMassPage(index))(true)
    .unsafeSetVal(TotalNetMassPage(index))("123")
    .unsafeSetVal(IsCommodityCodeKnownPage(index))(true)
    .unsafeSetVal(CommodityCodePage(index))("commodityCode")
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

        result.right.value mustBe expectedResult
      }

      "when containers used is true and containers are defined" in {

        val userAnswers = itemSectionUa
          .unsafeSetVal(ContainersUsedPage)(true)
          .unsafeSetVal(ContainerNumberPage(index, referenceIndex))("123")
          .unsafeSetVal(ContainerNumberPage(index, Index(1)))("123")

        val expectedResult = NonEmptyList(Container("123"), List(Container("123")))

        val result = ItemSection.readerItemSection(index).run(userAnswers).right.value

        result.containers.value mustBe expectedResult
      }

      "when add special mention is true and special mentions are defined" in {

        val userAnswers = itemSectionUa
          .unsafeSetVal(AddSpecialMentionPage(index))(true)
          .unsafeSetVal(SpecialMentionTypePage(index, referenceIndex))("specialMentionType")
          .unsafeSetVal(SpecialMentionAdditionalInfoPage(index, referenceIndex))("additionalInfo")

        val expectedResult = NonEmptyList(SpecialMentionDomain("specialMentionType", "additionalInfo"), List.empty)

        val result = ItemSection.readerItemSection(index).run(userAnswers).right.value

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

        result.right.value mustEqual expectedResult
      }
    }
  }
}

object ItemSectionSpec extends UserAnswersSpecHelper {

  private def setPackages(packages: NonEmptyList[Packages], itemIndex: Index)(startUserAnswers: UserAnswers): UserAnswers =
    packages.zipWithIndex.foldLeft(startUserAnswers) {
      case (userAnswers, (pckge, index)) =>
        PackagesSpec.setPackageUserAnswers(pckge, itemIndex, Index(index))(userAnswers)
    }

  def setItemSections(itemSections: Seq[ItemSection])(startUserAnswers: UserAnswers): UserAnswers = {

    val methodOfPayments: Seq[Option[Option[String]]]     = itemSections.map(_.itemSecurityTraderDetails.map(_.methodOfPayment))
    val commercialReferences: Seq[Option[Option[String]]] = itemSections.map(_.itemSecurityTraderDetails.map(_.commercialReferenceNumber))

    require(sequenceIsConsistent(methodOfPayments), "Method of payment contained a false")
    require(sequenceIsConsistent(commercialReferences), "Commercial reference contained a false")

    itemSections.zipWithIndex.foldLeft(startUserAnswers) {
      case (ua, (section, i)) =>
        val updatedUserAnswer =
          ua.unsafeSetVal(AddDocumentsPage(Index(i)))(section.producedDocuments.isDefined)
            .unsafeSetVal(AddAdministrativeReferencePage(Index(i)))(section.previousReferences.isDefined)
        ItemSectionSpec.setItemSection(section, Index(i))(updatedUserAnswer)
    }
  }

  def sequenceIsConsistent[A](xs: Seq[Option[Option[A]]]): Boolean = {

    val topLevelNotDefined: Boolean = xs.forall(_.isEmpty)
    val allDefined: Boolean         = xs.flatten.forall(_.isDefined)
    val allUndefined: Boolean       = xs.flatten.forall(_.isEmpty)

    topLevelNotDefined | allDefined | allUndefined
  }

  private def setContainers(containers: Option[NonEmptyList[Container]], itemIndex: Index)(startUserAnswers: UserAnswers): UserAnswers = {
    val ua = startUserAnswers.unsafeSetVal(ContainersUsedPage)(containers.isDefined)
    containers match {
      case Some(containers) => ContainerSpec.setContainers(containers.toList, itemIndex)(startUserAnswers)
      case None             => ua
    }
  }

  private def setSpecialMentions(specialMentions: Option[NonEmptyList[SpecialMentionDomain]], itemIndex: Index)(startUserAnswers: UserAnswers): UserAnswers = {
    val smUserAnswers = startUserAnswers.unsafeSetVal(AddSpecialMentionPage(itemIndex))(specialMentions.isDefined)
    specialMentions.fold(smUserAnswers)(_.zipWithIndex.foldLeft(smUserAnswers) {
      case (userAnswers, (specialMention, index)) =>
        SpecialMentionSpec.setSpecialMentionsUserAnswers(specialMention, itemIndex, Index(index))(userAnswers)
    })
  }

  private def setProducedDocuments(producedDocument: Option[NonEmptyList[ProducedDocument]], itemIndex: Index)(startUserAnswers: UserAnswers): UserAnswers = {
    val prodDocsUserAnswers = startUserAnswers.unsafeSetVal(AddDocumentsPage(itemIndex))(producedDocument.isDefined)
    producedDocument.fold(prodDocsUserAnswers)(_.zipWithIndex.foldLeft(prodDocsUserAnswers) {
      case (userAnswers, (producedDocument, index)) =>
        ProducedDocumentSpec.setProducedDocumentsUserAnswers(producedDocument, itemIndex, Index(index))(userAnswers)
    })
  }

  private def setPreviousReferences(previousReferences: Option[NonEmptyList[PreviousReferences]], itemIndex: Index)(
    startUserAnswers: UserAnswers
  ): UserAnswers = {
    val preRefUserAnswers = startUserAnswers.unsafeSetVal(AddAdministrativeReferencePage(itemIndex))(previousReferences.isDefined)
    previousReferences.fold(preRefUserAnswers)(_.zipWithIndex.foldLeft(preRefUserAnswers) {
      case (userAnswers, (previousReferences, index)) =>
        PreviousReferenceSpec.setPreviousReferenceUserAnswers(previousReferences, itemIndex, Index(index))(userAnswers)
    })
  }

  private def setItemsSecurityTraderDetails(itemsSecurityTraderDetails: Option[ItemsSecurityTraderDetails], index: Index)(
    userAnswers: UserAnswers
  ): UserAnswers =
    itemsSecurityTraderDetails match {
      case Some(result) => ItemsSecurityTraderDetailsSpec.setItemsSecurityTraderDetails(result, index)(userAnswers)
      case None         => userAnswers
    }

  def setItemSection(itemSection: ItemSection, itemIndex: Index)(startUserAnswers: UserAnswers): UserAnswers =
    (
      ItemDetailsSpec.setItemDetailsUserAnswers(itemSection.itemDetails, itemIndex) _ andThen
        ItemTraderDetailsSpec.setItemTraderDetails(ItemTraderDetails(itemSection.consignor, itemSection.consignee), itemIndex) andThen
        setPackages(itemSection.packages, itemIndex) andThen
        setContainers(itemSection.containers, itemIndex) andThen
        setSpecialMentions(itemSection.specialMentions, itemIndex) andThen
        setProducedDocuments(itemSection.producedDocuments, itemIndex) andThen
        setItemsSecurityTraderDetails(itemSection.itemSecurityTraderDetails, itemIndex) andThen
        setPreviousReferences(itemSection.previousReferences, itemIndex)
    )(startUserAnswers)
}
