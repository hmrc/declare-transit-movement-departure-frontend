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

import base.{GeneratorSpec, SpecBase, UserAnswersSpecHelper}
import cats.data.NonEmptyList
import generators.JourneyModelGenerators
import models.journeyDomain.PackagesSpec.UserAnswersNoErrorSet
import models.reference.CircumstanceIndicator
import models.{Index, UserAnswers}
import org.scalacheck.{Arbitrary, Gen}
import pages.{AddSecurityDetailsPage, ConsignorForAllItemsPage, ContainersUsedPage}
import pages.addItems.AddDocumentsPage
import pages.addItems.specialMentions.AddSpecialMentionPage
import pages.safetyAndSecurity.{AddCircumstanceIndicatorPage, AddCommercialReferenceNumberPage, CircumstanceIndicatorPage}

class ItemSectionSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {
  "ItemSection" - {
    "can be parsed UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(genItemSectionOld(), arb[UserAnswers]) {
          case (itemSection: ItemSection, userAnswers) =>
            val updatedUserAnswer = ItemSectionSpec
              .setItemSection(itemSection, index)(userAnswers)

            val result: Option[ItemSection] = ItemSection.readerItemSection(index).run(updatedUserAnswer)

            result.value.itemDetails mustEqual itemSection.itemDetails
            result.value.consignor mustEqual itemSection.consignor
            result.value.consignee mustEqual itemSection.consignee
            result.value.packages mustEqual itemSection.packages
            result.value.containers mustEqual itemSection.containers
            result.value.specialMentions mustEqual itemSection.specialMentions
            result.value.producedDocuments mustEqual itemSection.producedDocuments
            result.value.itemSecurityTraderDetails mustEqual itemSection.itemSecurityTraderDetails
        }
      }
    }

    "cannot be parsed" - {
      "when an answer is missing" in {
        forAll(arb[ItemSection], arb[UserAnswers]) {
          case (itemSection, ua) =>
            val userAnswers                 = ItemDetailsSpec.setItemDetailsUserAnswers(itemSection.itemDetails, index)(ua)
            val result: Option[ItemSection] = ItemSection.readerItemSection(index).run(userAnswers)

            result mustBe None
        }
      }
    }
  }

  "Seq of ItemSection" - {
    "can be parsed UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(nonEmptyListOf[ItemSection](2)(Arbitrary(genItemSectionOld())), arb[UserAnswers]) {
          case (itemSections, userAnswers) =>
            val updatedUserAnswer = ItemSectionSpec.setItemSections(itemSections.toList)(userAnswers)
            val result            = ItemSection.readerItemSections.run(updatedUserAnswer)

            result.value.head mustEqual itemSections.head
            result.value.tail mustEqual itemSections.tail
        }
      }
    }
  }
}

object ItemSectionSpec extends UserAnswersSpecHelper {

  private def setPackages(packages: NonEmptyList[Packages], itemIndex: Index)(startUserAnswers: UserAnswers): UserAnswers =
    packages.zipWithIndex.foldLeft(startUserAnswers) {
      case (userAnswers, (pckge, index)) => PackagesSpec.setPackageUserAnswers(pckge, itemIndex, Index(index))(userAnswers)
    }

  def setItemSections(itemSections: Seq[ItemSection])(startUserAnswers: UserAnswers): UserAnswers =
    itemSections.zipWithIndex.foldLeft(startUserAnswers) {
      case (ua, (section, i)) =>
        ItemSectionSpec.setItemSection(section, Index(i))(ua)
    }

  private def setContainers(containers: Option[NonEmptyList[Container]], itemIndex: Index)(startUserAnswers: UserAnswers): UserAnswers = {
    val ua = startUserAnswers.unsafeSetVal(ContainersUsedPage)(containers.isDefined)
    containers match {
      case Some(containers) => ContainerSpec.setContainers(containers.toList, itemIndex)(startUserAnswers)
      case None             => ua
    }
  }

  private def setSpecialMentions(specialMentions: Option[NonEmptyList[SpecialMention]], itemIndex: Index)(startUserAnswers: UserAnswers): UserAnswers = {
    val smUserAnswers = startUserAnswers.set(AddSpecialMentionPage(itemIndex), false).toOption.get
    specialMentions.fold(smUserAnswers)(_.zipWithIndex.foldLeft(smUserAnswers) {
      case (userAnswers, (specialMention, index)) =>
        SpecialMentionSpec.setSpecialMentionsUserAnswers(specialMention, itemIndex, Index(index))(userAnswers)
    })
  }

  private def setProducedDocuments(producedDocument: Option[NonEmptyList[ProducedDocument]], itemIndex: Index)(startUserAnswers: UserAnswers): UserAnswers = {
    val indicator = CircumstanceIndicator.conditionalIndicators.head
    val smUserAnswers = startUserAnswers
      .unsafeSetVal(AddSecurityDetailsPage)(producedDocument.isDefined)
      .unsafeSetVal(AddCircumstanceIndicatorPage)(producedDocument.isDefined)
      .unsafeSetVal(AddCommercialReferenceNumberPage)(producedDocument.isDefined)
      .unsafeSetVal(AddDocumentsPage(itemIndex))(producedDocument.isDefined)
      .unsafeSetVal(CircumstanceIndicatorPage)(indicator)

    producedDocument.fold(smUserAnswers)(_.zipWithIndex.foldLeft(smUserAnswers) {
      case (userAnswers, (producedDocument, index)) =>
        ProducedDocumentSpec.setProducedDocumentsUserAnswers(producedDocument, itemIndex, Index(index))(userAnswers)
    })
  }

  def setItemsSecurityTraderDetails(itemsSecurityTraderDetails: Option[ItemsSecurityTraderDetails], index: Index)(userAnswers: UserAnswers) =
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
        setItemsSecurityTraderDetails(itemSection.itemSecurityTraderDetails, itemIndex)
    )(startUserAnswers)
}
